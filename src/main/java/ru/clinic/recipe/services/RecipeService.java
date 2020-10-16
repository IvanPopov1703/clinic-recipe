package ru.clinic.recipe.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.clinic.recipe.entityes.Recipe;
import ru.clinic.recipe.exception.RecordNotFoundException;
import ru.clinic.recipe.repositories.RecipeRepository;

import java.util.List;

@Service
public class RecipeService {

    private RecipeRepository recipeRepository;

    /**
     * Метод existsById проверяет наличие объекта в базе данных по id
     *
     * @param id проверяемого объекта
     * @return возвращает true или false если элемент не найден
     */
    public boolean existsById(Long id) {
        return recipeRepository.existsById(id);
    }

    /**
     * Метод findById возвращиет запись по id
     *
     * @param id искомого объекта
     * @return возвращает объект или генерирует исключение
     */
    public Recipe findById(Long id) throws RecordNotFoundException {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(id));
    }

    /**
     * Метод findAll предназначен для получения всех записей
     * из таблицы базы данных
     *
     * @return возвращает список всех записей
     */
    public List<Recipe> findAll() {
        return recipeRepository.findAll();
    }

    /**
     * Метод save для добавления нового объекта
     *
     * @param recipe добавляемый объект
     */
    public void save(Recipe recipe) {
        Recipe tmpRecipe = recipeRepository.save(recipe);
    }

    /**
     * Метод deleteById удаляет запись по id
     * иначе генерирует исключение если такого объекта нет
     *
     * @param id удаляемого объекта
     */
    public void deleteById(Long id) throws RecordNotFoundException {
        if (!existsById(id)) {
            throw new RecordNotFoundException(id);
        } else {
            recipeRepository.deleteById(id);
        }
    }

    /**
     * Метод getSortedList сортирует список рецептов
     * по заданным параметрам
     *
     * @param strQuery строка с параметрами сортировки
     * @return отсортированный список
     */
    public List<Recipe> getSortedList(String strQuery) {
        return recipeRepository.getSortedList(strQuery);
    }

    /**
     * Метод getFiltrationList фильтрует список рецептов
     * по заданным параметрам
     *
     * @param strQuery строка с параметрами сортировки
     * @return отсортированный список
     */
    public List<Recipe> getFiltrationList(String strQuery) {
        return recipeRepository.getFiltrationList(strQuery);
    }

    @Autowired
    public void setRecipeRepository(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }
}
