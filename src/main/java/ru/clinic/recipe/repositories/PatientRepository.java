package ru.clinic.recipe.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.clinic.recipe.entityes.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

}
