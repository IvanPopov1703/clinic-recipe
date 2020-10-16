package ru.clinic.recipe.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.clinic.recipe.entityes.Doctor;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    /**
     * Запрос на получение количества выдынных рецептов доктором.
     *
     * @param id проверяемого доктора
     */
    @Query(value = "SELECT COUNT(R.DOCTOR_ID) AS Количество " +
            "FROM DOKTOR AS D INNER JOIN RECIPE AS R " +
            "ON R.DOCTOR_ID = D.ID WHERE D.ID = :id", nativeQuery = true)
    int getNumberOfIssuedRecipes(@Param("id") Long id);
}
