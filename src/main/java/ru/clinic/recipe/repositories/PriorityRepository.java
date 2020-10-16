package ru.clinic.recipe.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.clinic.recipe.entityes.Priority;

import java.util.List;

@Repository
public interface PriorityRepository extends JpaRepository<Priority, Long> {

    @Query(value = "SELECT p.id, p.name FROM Priority p " +
            "WHERE lower (p.name) like lower(concat('%', :values, '%'))")
    List<Priority> findAllByValue(@Param("values") String values);
}
