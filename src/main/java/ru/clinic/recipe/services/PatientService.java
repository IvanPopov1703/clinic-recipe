package ru.clinic.recipe.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.clinic.recipe.entityes.Patient;
import ru.clinic.recipe.exception.RecordNotFoundException;
import ru.clinic.recipe.repositories.PatientRepository;

import java.util.List;

@Service
public class PatientService {

    private PatientRepository patientRepository;

    /**
     * Метод existsById проверяет наличие объекта в базе данных по id
     *
     * @param id проверяемого объекта
     * @return возвращает true или false если элемент не найден
     * */
    public boolean existsById(Long id){
        return patientRepository.existsById(id);
    }

    /**
     * Метод findById возвращиет запись по id
     *
     * @param id искомого объекта
     * @return возвращает объект или генерирует исключение
     * */
    public Patient findById(Long id) throws RecordNotFoundException {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(id));
    }

    /**
     * Метод findAll предназначен для получения всех записей
     * из таблицы базы данных
     *
     * @return возвращает список всех записей
     * */
    public List<Patient> findAll(){
        return patientRepository.findAll();
    }

    /**
     * Метод save для добавления нового объекта
     *
     * @param patient добавляемый объект
     * */
    public void save(Patient patient){
        Patient tmpPatient = patientRepository.save(patient);
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
            patientRepository.deleteById(id);
        }
    }

    @Autowired
    public void setPatientRepository(PatientRepository patientRepository){
        this.patientRepository = patientRepository;
    }
}
