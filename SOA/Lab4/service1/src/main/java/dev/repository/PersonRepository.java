package dev.repository;

import dev.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonRepository extends JpaRepository<Person, Long> {

    @Query("SELECT AVG(p.height) FROM Person p")
    Double findAverageHeight();

    @Query("SELECT COUNT(p) FROM Person p WHERE p.location.x = :x AND p.location.y = :y AND p.location.z = :z AND p.location.name = :name")
    Long countByLocation(@Param("x") Integer x, @Param("y") float y, @Param("z") float z, @Param("name") String name);

    Page<Person> findByNationalityGreaterThan(Country nationality, Pageable pageable);

}
