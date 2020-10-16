package ru.clinic.recipe.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.clinic.recipe.entityes.Specialization;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, Long> {

}
