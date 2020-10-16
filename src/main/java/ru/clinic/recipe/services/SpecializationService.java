package ru.clinic.recipe.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.clinic.recipe.entityes.Specialization;
import ru.clinic.recipe.exception.RecordNotFoundException;
import ru.clinic.recipe.repositories.SpecializationRepository;

import java.util.List;

@Service
public class SpecializationService {

    private SpecializationRepository specializationRepository;

    /**
     * Метод existsById проверяет наличие объекта в базе данных по id
     *
     * @param id проверяемого объекта
     * @return возвращает true или false если элемент не найден
     * */
    public boolean existsById(Long id){
        return specializationRepository.existsById(id);
    }

    /**
     * Метод findById возвращиет запись по id
     *
     * @param id искомого объекта
     * @return возвращает объект или генерирует исключение
     * */
    public Specialization findById(Long id) throws RecordNotFoundException {
        return specializationRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(id));
    }

    /**
     * Метод findAll предназначен для получения всех записей
     * из таблицы базы данных
     *
     * @return возвращает список всех записей
     * */
    public List<Specialization> findAll(){
        return specializationRepository.findAll();
    }

    /**
     * Метод save для добавления нового объекта
     *
     * @param specialization добавляемый объект
     * */
    public void save(Specialization specialization){
        specialization.setId(1L);
        Specialization tmpSpecialization = specializationRepository.save(specialization);
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
            specializationRepository.deleteById(id);
        }
    }

    @Autowired
    public void setSpecializationRepository(SpecializationRepository specializationRepository) {
        this.specializationRepository = specializationRepository;
    }
}
