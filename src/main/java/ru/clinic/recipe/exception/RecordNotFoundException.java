package ru.clinic.recipe.exception;

public class RecordNotFoundException extends RuntimeException {
    public RecordNotFoundException(Long id){
        super("Ошибка! Запись с номером " + id + " не найдена!");
    }
}
