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
    public PersonsResponse searchPersons(SearchRequest searchRequest) {
        System.out.println("Начало поиска персон");

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Person> cq = cb.createQuery(Person.class);
        Root<Person> personRoot = cq.from(Person.class);
        System.out.println("Критерии поиска и корень сущности созданы");

        // Создаем предикаты на основе фильтров
        List<Predicate> predicates = buildPredicates(cb, personRoot, searchRequest.getPersonFilter());
        System.out.println("Список предикатов: " + predicates);
        cq.where(predicates.toArray(new Predicate[0]));

        // Добавляем сортировку
        if (searchRequest.getSortCriteria() != null && searchRequest.getSortCriteria().getFields() != null) {
            List<Order> orders = buildOrders(cb, personRoot, searchRequest.getSortCriteria());
            System.out.println("Список сортировок: " + orders);
            cq.orderBy(orders);
        }

        // Пагинация
        TypedQuery<Person> query = entityManager.createQuery(cq);
        int page = (searchRequest.getPage() != null && searchRequest.getPage() > 0) ? searchRequest.getPage() : 1;
        int size = (searchRequest.getSize() != null && searchRequest.getSize() > 0) ? searchRequest.getSize() : 10;
        System.out.println("Параметры пагинации - Страница: " + page + ", Размер: " + size);

        // Подсчет общего количества записей
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Person> countRoot = countQuery.from(Person.class);
        countQuery.select(cb.count(countRoot));

        // Rebuild the predicates for the count query
        List<Predicate> countPredicates = buildPredicates(cb, countRoot, searchRequest.getPersonFilter());
        System.out.println("Список предикатов для подсчета: " + countPredicates);
        countQuery.where(countPredicates.toArray(new Predicate[0]));

        Long totalRecords = entityManager.createQuery(countQuery).getSingleResult();
        int totalPages = (int) Math.ceil((double) totalRecords / size);
        System.out.println("Общее количество записей: " + totalRecords + ", Общее количество страниц: " + totalPages);

        // Устанавливаем параметры пагинации
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);

        // Получаем результаты
        List<Person> persons = query.getResultList();
        System.out.println("Найдено персон: " + persons.size());
        List<PersonDTO> personDTOs = persons.stream()
                .map(p -> modelMapper.map(p, PersonDTO.class))
                .collect(Collectors.toList());
        System.out.println("DTO персон: " + personDTOs);

        // Формируем ответ
        System.out.println("Формирование ответа");
        return new PersonsResponse(totalRecords, totalPages, page, size, personDTOs);
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Person> root, PersonFilter personFilter) {
        System.out.println("Начало создания предикатов");
        List<Predicate> predicates = new ArrayList<>();

        if (personFilter != null) {
            // Фильтр по имени
            if (personFilter.getName() != null && !personFilter.getName().isEmpty()) {
                Predicate namePredicate = root.get("name").in(personFilter.getName());
                predicates.add(namePredicate);
                System.out.println("Добавлен предикат по имени: " + namePredicate);
            }

            // Фильтр по высоте
            if (personFilter.getHeight() != null) {
                if (personFilter.getHeight().getMin() != null) {
                    Predicate minHeightPredicate = cb.ge(root.get("height"), personFilter.getHeight().getMin());
                    predicates.add(minHeightPredicate);
                    System.out.println("Добавлен предикат по минимальной высоте: " + minHeightPredicate);
                }
                if (personFilter.getHeight().getMax() != null) {
                    Predicate maxHeightPredicate = cb.le(root.get("height"), personFilter.getHeight().getMax());
                    predicates.add(maxHeightPredicate);
                    System.out.println("Добавлен предикат по максимальной высоте: " + maxHeightPredicate);
                }
                if (personFilter.getHeight().getIn() != null && !personFilter.getHeight().getIn().isEmpty()) {
                    Predicate heightInPredicate = root.get("height").in(personFilter.getHeight().getIn());
                    predicates.add(heightInPredicate);
                    System.out.println("Добавлен предикат по высоте (in): " + heightInPredicate);
                }
            }

            // Фильтр по национальности
            if (personFilter.getNationality() != null && !personFilter.getNationality().isEmpty()) {
                List<Country> countries = personFilter.getNationality().stream()
                        .map(Country::valueOf)
                        .collect(Collectors.toList());
                Predicate nationalityPredicate = root.get("nationality").in(countries);
                predicates.add(nationalityPredicate);
                System.out.println("Добавлен предикат по национальности: " + nationalityPredicate);
            }

            // Фильтр по координатам
            if (personFilter.getCoordinates() != null && personFilter.getCoordinates().getIn() != null) {
                List<Predicate> coordPredicates = new ArrayList<>();
                for (CoordinatesDTO coord : personFilter.getCoordinates().getIn()) {
                    Predicate coordPredicate = cb.and(
                            cb.equal(root.get("coordinates").get("x"), coord.getX()),
                            cb.equal(root.get("coordinates").get("y"), coord.getY()));
                    coordPredicates.add(coordPredicate);
                    System.out.println("Добавлен предикат по координатам: " + coordPredicate);
                }
                Predicate combinedCoordPredicate = cb.or(coordPredicates.toArray(new Predicate[0]));
                predicates.add(combinedCoordPredicate);
                System.out.println("Добавлен объединенный предикат по координатам: " + combinedCoordPredicate);
            }

            // Фильтр по местоположению
            if (personFilter.getLocation() != null && personFilter.getLocation().getIn() != null) {
                List<Predicate> locPredicates = new ArrayList<>();
                for (LocationDTO loc : personFilter.getLocation().getIn()) {
                    Predicate locPredicate = cb.and(
                            cb.equal(root.get("location").get("x"), loc.getX()),
                            cb.equal(root.get("location").get("y"), loc.getY()),
                            cb.equal(root.get("location").get("z"), loc.getZ()),
                            cb.equal(root.get("location").get("name"), loc.getName()));
                    locPredicates.add(locPredicate);
                    System.out.println("Добавлен предикат по местоположению: " + locPredicate);
                }
                Predicate combinedLocPredicate = cb.or(locPredicates.toArray(new Predicate[0]));
                predicates.add(combinedLocPredicate);
                System.out.println("Добавлен объединенный предикат по местоположению: " + combinedLocPredicate);
            }
        }

        System.out.println("Создание предикатов завершено");
        return predicates;
    }

    private List<Order> buildOrders(CriteriaBuilder cb, Root<Person> root, SortCriteria sortCriteria) {
        System.out.println("Начало создания сортировок");
        List<Order> orders = new ArrayList<>();
        for (SortField field : sortCriteria.getFields()) {
            Order order = null;
            if ("asc".equalsIgnoreCase(field.getOrder())) {
                order = cb.asc(root.get(field.getName()));
            } else if ("desc".equalsIgnoreCase(field.getOrder())) {
                order = cb.desc(root.get(field.getName()));
            }
            if (order != null) {
                orders.add(order);
                System.out.println("Добавлена сортировка: " + order);
            }
        }
        System.out.println("Создание сортировок завершено");
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
