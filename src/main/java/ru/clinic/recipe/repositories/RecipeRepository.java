package ru.clinic.recipe.repositories;

import org.hibernate.query.criteria.internal.expression.ConcatExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.clinic.recipe.entityes.Recipe;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    /**
     * Мне так и не удалось понять, как слепить ORDER BY со стракой,
     * которую я передаю в качестве параметра.
     * В этой строке содержатся поля, по которым группировать,
     * (например strQuery = PAT.SURNAME, PAT.NAME, PAT.PATRONYMIC, P.NAME),
     * т.е. в этом случае пользователь выбрал группировку по ФИО и по Приоритету.
     *
     * Так же т.к. я не понял как слеплять строчки в запрос,
     * у меня фильтрация выполняется по всем полям.
     */

    /*@Query(value = "SELECT R.*, P.*, PAT.* FROM RECIPE AS R " +
            "INNER JOIN PRIORITY AS P ON R.PRIORITY_ID = P.ID " +
            "INNER JOIN PATIENT AS PAT ON R.PATIENT_ID = PAT.ID " +
            "ORDER BY :strQuery", nativeQuery = true)
    List<Recipe> getSortedList(@Param("strQuery") String strQuery);*/

    /**
     * Запрос на фильтрацию по заданному параметру.
     * Фильтрация выполняет по ФИО пациента, описанию рецепта
     * и приоритету.
     */
    @Query(value = "SELECT R.*, P.*, PAT.* " +
            "FROM RECIPE AS R INNER JOIN PATIENT AS PAT " +
            "ON R.PATIENT_ID = PAT.ID " +
            "INNER JOIN PRIORITY AS P " +
            "ON R.PRIORITY_ID = P.ID " +
            "WHERE LOWER(PAT.SURNAME) LIKE LOWER(CONCAT('%', :searchStr, '%')) " +
            "OR LOWER(PAT.NAME) LIKE LOWER(CONCAT('%', :searchStr, '%')) " +
            "OR LOWER(PAT.PATRONYMIC) LIKE LOWER(CONCAT('%', :searchStr, '%')) " +
            "OR LOWER(P.NAME) LIKE LOWER(CONCAT('%', :searchStr, '%')) " +
            "OR LOWER(R.DESCRIPTION) LIKE LOWER(CONCAT('%', :searchStr, '%'))", nativeQuery = true)
    List<Recipe> getFiltrationList(@Param("searchStr") String searchStr);

    /**
     * Запрос на сортировку, по заданным полям.
     * Сортировка выполняет по ФИО пациента и приоритету.
     */
    @Query(value = "SELECT R.*, P.*, PAT.* FROM RECIPE AS R " +
            "INNER JOIN PRIORITY AS P ON R.PRIORITY_ID = P.ID " +
            "INNER JOIN PATIENT AS PAT ON R.PATIENT_ID = PAT.ID " +
            "ORDER BY PAT.SURNAME, PAT.NAME, PAT.PATRONYMIC, P.NAME", nativeQuery = true)
    List<Recipe> getSortedList(@Param("strQuery") String strQuery);


}
