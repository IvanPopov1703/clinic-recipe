package ru.clinic.recipe.entityes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "PATIENT")
public class Patient {

    @Id
    @GeneratedValue
    @Column(name = "ID", unique = true, nullable = false, updatable = false)
    private Long id;

    @NotNull(message = "Поле не может быть пустым!")
    @Size(min = 2, message = "Поле должно содержать как минимум 2 символа")
    @Column(name = "NAME")
    private String name;

    @NotNull(message = "Поле не может быть пустым!")
    @Size(min = 4, message = "Поле должно содержать как минимум 4 символа")
    @Column(name = "SURNAME")
    private String surname;

    @NotNull(message = "Поле не может быть пустым!")
    @Size(min = 4, message = "Поле должно содержать как минимум 4 символа")
    @Column(name = "PATRONYMIC")
    private String patronymic;

    @NotNull(message = "Поле не может быть пустым!")
    @Size(min = 4, message = "Поле должно содержать как минимум 4 символа")
    @Column(name = "PHONE")
    private String phone;

    //Соединение с рецептом
    @OneToMany(mappedBy = "patient")
    @JsonIgnoreProperties("patient")
    private List<Recipe> recipes;

    @Override
    public String toString(){
        return surname + " " + name + " " + patronymic;
    }
}
