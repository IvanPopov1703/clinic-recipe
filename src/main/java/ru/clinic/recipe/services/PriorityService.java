package ru.clinic.recipe.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.clinic.recipe.entityes.Priority;
import ru.clinic.recipe.exception.RecordNotFoundException;
import ru.clinic.recipe.repositories.PriorityRepository;

import java.util.List;

@Service
public class PriorityService {

    private PriorityRepository priorityRepository;

    /**
     * Метод existsById проверяет наличие объекта в базе данных по id
     *
     * @param id проверяемого объекта
     * @return возвращает true или false если элемент не найден
     * */
    public boolean existsById(Long id){
        return priorityRepository.existsById(id);
    }

    /**
     * Метод findById возвращиет запись по id
     *
     * @param id искомого объекта
     * @return возвращает объект или генерирует исключение
     * */
    public Priority findById(Long id) throws RecordNotFoundException {
        return priorityRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(id));
    }

    /**
     * Метод findAll предназначен для получения всех записей
     * из таблицы базы данных
     *
     * @return возвращает список всех записей
     * */
    public List<Priority> findAll(){
        return priorityRepository.findAll();
    }

    /**
     * Метод findAllByValue предназначен для получения всех записей
     * из таблицы базы данных соответствующих заданному параметру
     *
     * @param value значение по которому будет происходить фильтрация
     * @return возвращает список записей соответветствующий параметру
     * */
    public List<Priority> findAllByValue(String value){
        if (value == null || value.isEmpty()){
            return priorityRepository.findAll();
        } else {
            return priorityRepository.findAllByValue(value);
        }
    }

    /**
     * Метод save для добавления нового объекта
     *
     * @param priority добавляемый объект
     * */
    public void save(Priority priority){
        Priority tmpPriority = priorityRepository.save(priority);
    }

    /**
     * Метод deleteById удаляет запись по id
     * иначе генерирует исключение если такого объекта нет
     *
     * @param id удаляемого объекта
     * */
    public void deleteById(Long id) throws RecordNotFoundException {
        if (!existsById(id)){
            throw new RecordNotFoundException(id);
        }
        else {
            priorityRepository.deleteById(id);
        }
    }

    @Autowired
    public void setPriorityRepository(PriorityRepository priorityRepository){
        this.priorityRepository = priorityRepository;
    }
}
