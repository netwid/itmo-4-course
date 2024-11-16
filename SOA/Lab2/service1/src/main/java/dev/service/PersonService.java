package dev.service;

import dev.dto.*;
import dev.entity.*;
import dev.repository.PersonRepository;
import jakarta.persistence.criteria.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ModelMapper modelMapper;

    @PersistenceContext
    private EntityManager entityManager;

    // Добавить
    public PersonDTO savePerson(PersonDTO personDTO) {
        personDTO.setId(null); // Обнуление ID для автоинкремента
        Person person = modelMapper.map(personDTO, Person.class);
        Person savedPerson = personRepository.save(person);
        return modelMapper.map(savedPerson, PersonDTO.class);
    }

    // Поиск персон с фильтрацией и сортировкой
    public Map<String, Object> searchPersons(SearchRequest searchRequest) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Person> cq = cb.createQuery(Person.class);
        Root<Person> personRoot = cq.from(Person.class);

        List<Predicate> predicates = buildPredicates(cb, personRoot, searchRequest.getFilter());

        cq.where(predicates.toArray(new Predicate[0]));

        // Сортировка
        if (searchRequest.getSortCriteria() != null && searchRequest.getSortCriteria().getFields() != null) {
            List<Order> orders = buildOrders(cb, personRoot, searchRequest.getSortCriteria());
            cq.orderBy(orders);
        }

        // Пагинация
        TypedQuery<Person> query = entityManager.createQuery(cq);

        int page = (searchRequest.getPage() != null && searchRequest.getPage() > 0) ? searchRequest.getPage() : 1;
        int size = (searchRequest.getSize() != null && searchRequest.getSize() > 0) ? searchRequest.getSize() : 10;

        // Подсчет общего количества записей
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Person> countRoot = countQuery.from(Person.class);
        countQuery.select(cb.count(countRoot));
        countQuery.where(predicates.toArray(new Predicate[0]));
        Long totalRecords = entityManager.createQuery(countQuery).getSingleResult();
        int totalPages = (int) Math.ceil((double) totalRecords / size);

        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);

        List<Person> persons = query.getResultList();
        List<PersonDTO> personDTOs = persons.stream()
                .map(p -> modelMapper.map(p, PersonDTO.class))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("totalResults", totalRecords);
        result.put("totalPages", totalPages);
        result.put("currentPage", page);
        result.put("pageSize", size);
        result.put("persons", personDTOs);

        return result;
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Person> root, PersonFilter filter) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter != null) {
            // Фильтр по имени
            if (filter.getName() != null && !filter.getName().isEmpty()) {
                predicates.add(root.get("name").in(filter.getName()));
            }

            // Фильтр по высоте
            if (filter.getHeight() != null) {
                if (filter.getHeight().getMin() != null) {
                    predicates.add(cb.ge(root.get("height"), filter.getHeight().getMin()));
                }
                if (filter.getHeight().getMax() != null) {
                    predicates.add(cb.le(root.get("height"), filter.getHeight().getMax()));
                }
                if (filter.getHeight().getIn() != null && !filter.getHeight().getIn().isEmpty()) {
                    predicates.add(root.get("height").in(filter.getHeight().getIn()));
                }
            }

            // Фильтр по национальности
            if (filter.getNationality() != null && !filter.getNationality().isEmpty()) {
                List<Country> countries = filter.getNationality().stream()
                        .map(Country::valueOf)
                        .collect(Collectors.toList());
                predicates.add(root.get("nationality").in(countries));
            }

            // Фильтр по координатам
            if (filter.getCoordinates() != null && filter.getCoordinates().getIn() != null) {
                List<Predicate> coordPredicates = new ArrayList<>();
                for (CoordinatesDTO coord : filter.getCoordinates().getIn()) {
                    Predicate coordPredicate = cb.and(
                            cb.equal(root.get("coordinates").get("x"), coord.getX()),
                            cb.equal(root.get("coordinates").get("y"), coord.getY())
                    );
                    coordPredicates.add(coordPredicate);
                }
                predicates.add(cb.or(coordPredicates.toArray(new Predicate[0])));
            }

            // Фильтр по местоположению
            if (filter.getLocation() != null && filter.getLocation().getIn() != null) {
                List<Predicate> locPredicates = new ArrayList<>();
                for (LocationDTO loc : filter.getLocation().getIn()) {
                    Predicate locPredicate = cb.and(
                            cb.equal(root.get("location").get("x"), loc.getX()),
                            cb.equal(root.get("location").get("y"), loc.getY()),
                            cb.equal(root.get("location").get("z"), loc.getZ()),
                            cb.equal(root.get("location").get("name"), loc.getName())
                    );
                    locPredicates.add(locPredicate);
                }
                predicates.add(cb.or(locPredicates.toArray(new Predicate[0])));
            }

            // Дополнительные фильтры могут быть добавлены аналогично
        }

        return predicates;
    }

    private List<Order> buildOrders(CriteriaBuilder cb, Root<Person> root, SortCriteria sortCriteria) {
        List<Order> orders = new ArrayList<>();
        for (SortField field : sortCriteria.getFields()) {
            if ("asc".equalsIgnoreCase(field.getOrder())) {
                orders.add(cb.asc(root.get(field.getName())));
            } else if ("desc".equalsIgnoreCase(field.getOrder())) {
                orders.add(cb.desc(root.get(field.getName())));
            }
        }
        return orders;
    }

    // Получить персон по ID
    public PersonDTO getPersonById(Long id) {
        Optional<Person> personOptional = personRepository.findById(id);
        return personOptional.map(person -> modelMapper.map(person, PersonDTO.class)).orElse(null);
    }

    // Обновить персон по ID
    public PersonDTO updatePerson(Long id, PersonDTO personDTO) {
        Optional<Person> existingPersonOptional = personRepository.findById(id);
        if (existingPersonOptional.isPresent()) {
            Person existingPerson = existingPersonOptional.get();
            personDTO.setId(existingPerson.getId()); // Убедиться, что ID совпадают
            modelMapper.map(personDTO, existingPerson);
            Person updatedPerson = personRepository.save(existingPerson);
            return modelMapper.map(updatedPerson, PersonDTO.class);
        } else {
            return null;
        }
    }

    // Удалить персон по ID
    public boolean deletePerson(Long id) {
        if (personRepository.existsById(id)) {
            personRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    // Рассчитать среднее значение поля height
    public Double getAverageHeight() {
        return personRepository.findAverageHeight();
    }

    // Получить количество элементов по location
    public Long countByLocation(Integer x, float y, float z, String name) {
        return personRepository.countByLocation(x, y, z, name);
    }

    // Получить элементы с nationality больше заданного
    public List<PersonDTO> getPersonsByNationalityGreaterThan(String nationalityStr, int page, int size) {
        try {
            Country nationality = Country.valueOf(nationalityStr);
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<Person> personPage = personRepository.findByNationalityGreaterThan(nationality, pageable);

            return personPage.getContent().stream()
                    .map(person -> modelMapper.map(person, PersonDTO.class))
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return Collections.emptyList();
        }
    }

}
