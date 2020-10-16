package ru.clinic.recipe.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.clinic.recipe.entityes.Doctor;
import ru.clinic.recipe.exception.RecordNotFoundException;
import ru.clinic.recipe.repositories.DoctorRepository;

import java.util.List;

@Service
public class DoctorService {

    private DoctorRepository doctorRepository;

    /**
     * Метод existsById проверяет наличие объекта в базе данных по id
     *
     * @param id проверяемого объекта
     * @return возвращает true или false если элемент не найден
     * */
    public boolean existsById(Long id){
        return doctorRepository.existsById(id);
    }

    /**
     * Метод findById возвращиет запись по id
     *
     * @param id искомого объекта
     * @return возвращает объект или генерирует исключение
     * */
    public Doctor findById(Long id) throws RecordNotFoundException {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(id));
    }

    /**
     * Метод findAll предназначен для получения всех записей
     * из таблицы базы данных
     *
     * @return возвращает список всех записей
     * */
    public List<Doctor> findAll(){
        return doctorRepository.findAll();
    }

    /**
     * Метод findAllByValue предназначен для получения всех записей
     * из таблицы базы данных соответствующих заданному параметру
     *
     * @param value значение по которому будет происходить фильтрация
     * @return возвращает список записей соответветствующий параметру
     * */
    /*public List<Doctor> findAllByValue(String value){
        if (value == null || value.isEmpty()){
            return doctorRepository.findAll();
        } else {
            return doctorRepository.findAllByValue(value);
        }
    }*/

    /**
     * Метод save для добавления нового объекта
     *
     * @param doctor добавляемый объект
     * */
    public void save(Doctor doctor){
        Doctor tmpDoctor = doctorRepository.save(doctor);
    }

    /**
     * Метод update для редактирования объекта
     *
     * @param doctor редактируемый объект
     * */
    public Doctor update(Doctor doctor) throws RecordNotFoundException{
        if (doctor.getId() != null && !existsById(doctor.getId())){
            throw new RecordNotFoundException(doctor.getId());
        }
        return doctorRepository.save(doctor);
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
            doctorRepository.deleteById(id);
        }
    }

    /**
     * Метод getNumberOfIssuedRecipes подсчитывает количество
     * рецепитов выданных доктором
     *
     * @param id доктора для которорого требуется подсчитать
     *           количество выданных им рецептов
     * @return количество выданных рецептов
     */
    public int getNumberOfIssuedRecipes(Long id){
        return doctorRepository.getNumberOfIssuedRecipes(id);
    }
    
    @Autowired
    public void setDoctorRepository(DoctorRepository doctorRepository){
        this.doctorRepository = doctorRepository;
    }
}
