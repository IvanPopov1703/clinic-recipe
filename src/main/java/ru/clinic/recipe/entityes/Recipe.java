package ru.clinic.recipe.entityes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "RECIPE")
public class Recipe {

    @Id
    @GeneratedValue
    @Column(name = "ID", unique = true, nullable = false, updatable = false)
    private Long id;

    @Column(name = "DATE_OF_CREATION")
    private Date dateOfCreation;

    @Column(name = "EXPIRATION_DATE")
    private Date expirationDate;

    @NotNull(message = "Поле не может быть пустым!")
    @Size(min = 1, message = "Поле должно содержать как минимум 1 символ")
    @Column(name = "DESCRIPTION")
    private String description;

    //Соединение с приоритетом
    @ManyToOne
    @JoinColumn(name = "PRIORITY_ID")
    @JsonIgnoreProperties("recipe")
    private Priority priority;

    //Соединение с врачом
    @ManyToOne
    @JoinColumn(name = "DOCTOR_ID")
    @JsonIgnoreProperties("recipe")
    private Doctor doctor;

    //Соединение с пациентом
    @ManyToOne
    @JoinColumn(name = "PATIENT_ID")
    @JsonIgnoreProperties("recipe")
    private Patient patient;

    public LocalDate getParseDateOfCreation() {
        return new java.sql.Date(dateOfCreation.getTime()).toLocalDate();
    }

    public LocalDate getParseExpirationDate() {
        return new java.sql.Date(expirationDate.getTime()).toLocalDate();
    }

    public void setParseExpirationDate(LocalDate localDate) {
        expirationDate = java.sql.Date.valueOf(localDate);
    }

    public void setParseDateOfCreation(LocalDate localDate) {
        dateOfCreation = java.sql.Date.valueOf(localDate);
    }
}
