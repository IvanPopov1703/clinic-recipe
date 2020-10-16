package ru.clinic.recipe.entityes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "SPECIALIZATION")
public class Specialization {

    @Id
    @GeneratedValue
    @Column(name = "ID", unique = true, nullable = false, updatable = false)
    private Long id;

    @NotNull(message = "Поле не может быть пустым1!")
    @Size(min = 4, message = "Поле должно содержать как минимум 4 символа")
    @Column(name = "NAME")
    private String name;

    @OneToMany(mappedBy = "specialization")
    @JsonIgnoreProperties("specialization")
    private List<Doctor> doctors;
}