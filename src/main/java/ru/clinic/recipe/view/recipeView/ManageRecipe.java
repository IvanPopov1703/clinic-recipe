package ru.clinic.recipe.view.recipeView;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import ru.clinic.recipe.entityes.Doctor;
import ru.clinic.recipe.entityes.Patient;
import ru.clinic.recipe.entityes.Priority;
import ru.clinic.recipe.entityes.Recipe;
import ru.clinic.recipe.exception.RecordNotFoundException;
import ru.clinic.recipe.services.DoctorService;
import ru.clinic.recipe.services.PatientService;
import ru.clinic.recipe.services.PriorityService;
import ru.clinic.recipe.services.RecipeService;

import java.time.LocalDate;

@Route("manageRecipe")
public class ManageRecipe extends AppLayout implements HasUrlParameter<Long> {

    private RecipeService recipeService;
    private DoctorService doctorService;
    private PatientService patientService;
    private PriorityService priorityService;

    private Long id;
    private FormLayout recipeForm;
    private ComboBox<Doctor> doctorComboBox;
    private ComboBox<Patient> patientComboBox;
    private DatePicker dateOfCreation;
    private DatePicker expirationDate;
    private ComboBox<Priority> priorityComboBox;
    private TextArea description;
    private Button buttonSaveRecipe;
    private Button buttonCancelRecipe;
    private Binder<Recipe> binder;

    public ManageRecipe() {
        recipeForm = new FormLayout();
        doctorComboBox = new ComboBox<>("Врач");
        patientComboBox = new ComboBox<>("Пациент");
        dateOfCreation = new DatePicker("Дата выдачи");
        expirationDate = new DatePicker("Действует до");
        priorityComboBox = new ComboBox<>("Приоритет");
        description = new TextArea("Описание");
        buttonSaveRecipe = new Button("Сохранить");
        buttonCancelRecipe = new Button("Отмена");
        binder = new Binder<>(Recipe.class);
        recipeForm.add(
                doctorComboBox,
                patientComboBox,
                dateOfCreation,
                expirationDate,
                priorityComboBox,
                description,
                buttonSaveRecipe,
                buttonCancelRecipe
        );
    }

    private void componentSetup() {
        doctorComboBox.setItemLabelGenerator(Doctor::toString);
        patientComboBox.setItemLabelGenerator(Patient::toString);
        priorityComboBox.setItemLabelGenerator(Priority::getName);
        doctorComboBox.setItems(doctorService.findAll());
        patientComboBox.setItems(patientService.findAll());
        priorityComboBox.setItems(priorityService.findAll());
        dateOfCreation.setValue(LocalDate.now());
        expirationDate.setValue(LocalDate.now());
    }

    @Override
    public void setParameter(BeforeEvent event, Long id) {
        this.id = id;
        if (id == 0) {
            addToNavbar(new H3("Создание рецепта"));
        } else {
            addToNavbar(new H3("Редактирование рецепта"));
        }
        fillForm();
    }

    /**
     * Метод fillForm предназначен
     * для добавления компонентов на форму
     * и обработки кнопок.
     */
    private void fillForm() {
        componentSetup();
        try {
            if (id != 0) {
                Recipe recipe = recipeService.findById(id);
                if (recipe.getDoctor() != null) {
                    doctorComboBox.setValue(recipe.getDoctor());
                }
                if (recipe.getPatient() != null) {
                    patientComboBox.setValue(recipe.getPatient());
                }
                dateOfCreation.setValue(
                        new java.sql.Date(recipe.getDateOfCreation().getTime()).toLocalDate()
                );
                expirationDate.setValue(
                        new java.sql.Date(recipe.getExpirationDate().getTime()).toLocalDate()
                );
                if (recipe.getPriority() != null) {
                    priorityComboBox.setValue(recipe.getPriority());
                }
                description.setValue(recipe.getDescription());
            }

            /* Обработка кнопки "Сохранить" */
            saveButtonHandling();

            /* Обработка кнопки "Отмена" */
            cancelButtonHandling();
        } catch (RecordNotFoundException ex) {        //Обработка исключительной ситуации
            recipeForm.removeAll();
            recipeForm.add(new H1(ex.getMessage()));
        }
        setContent(recipeForm);
    }

    /**
     * Метод saveButtonHandling предназначен для обработки
     * кнопки "Сохранить"
     */
    private void saveButtonHandling() {
        buttonSaveRecipe.addClickListener(clickEvent -> {
            Recipe newRecipe = new Recipe();
            if (id != 0) {
                newRecipe.setId(this.id);
            }
            newRecipe.setDoctor(doctorComboBox.getValue());
            newRecipe.setPatient(patientComboBox.getValue());
            newRecipe.setDateOfCreation(java.sql.Date.valueOf(dateOfCreation.getValue()));
            newRecipe.setExpirationDate(java.sql.Date.valueOf(expirationDate.getValue()));
            newRecipe.setPriority(priorityComboBox.getValue());
            newRecipe.setDescription(description.getValue().trim());
            /*Проверка валидности полей*/
            checkValidation();
            try {
                binder.writeBean(newRecipe);

                recipeService.save(newRecipe);

                /* Вывод уведомления пользователю и переход к списку рецептов */
                Notification notification = new Notification(id == 0 ? "Запись успешно добавлена!"
                        : "Запись успешно изменена!", 1000);
                notification.setPosition(Notification.Position.MIDDLE);
                notification.addDetachListener(detachEvent -> {
                    UI.getCurrent().navigate(RecipeList.class);
                });
                recipeForm.setEnabled(false);
                notification.open();
            } catch (ValidationException e) {
            }
        });
    }

    /**
     * Метод cancelButtonHandling предназначен для обработки
     * кнопки "Отмена"
     */
    private void cancelButtonHandling() {
        buttonCancelRecipe.addClickListener(clickEvent -> {
            Notification notification = new Notification("", 1);
            notification.addDetachListener(detachEvent -> {
                UI.getCurrent().navigate(RecipeList.class);
            });
            notification.open();
        });
    }

    /**
     * Метод checkValidation предназначен для проверки
     * полей на валидность
     */
    private void checkValidation() {

        binder.forField(description)
                .withValidator(description -> description.length() >= 1,
                        "Поле должно содержать минимум 1 символ")
                .bind(Recipe::getDescription, Recipe::setDescription);

        binder.forField(expirationDate)
                .withValidator(
                        expirationDate -> !expirationDate.isBefore(
                                dateOfCreation.getValue()),
                        "Дата срока действи не может быть раньше даты выдачи")
                .bind(Recipe::getParseExpirationDate, Recipe::setParseExpirationDate);

        binder.forField(dateOfCreation)
                .withValidator(
                        dateOfCreation -> !dateOfCreation.isBefore(
                                LocalDate.now()),
                        "Дата выдачи не может быть раньше текущей даты")
                .bind(Recipe::getParseDateOfCreation, Recipe::setParseDateOfCreation);

        binder.forField(doctorComboBox)
                .withValidator(
                        doctorComboBox -> doctorComboBox.toString().length() >= 1,
                        "Врач не выбран!")
                .bind(Recipe::getDoctor, Recipe::setDoctor);

        binder.forField(patientComboBox)
                .withValidator(
                        patientComboBox -> patientComboBox.toString().length() >= 1,
                        "Пациент не выбран!")
                .bind(Recipe::getPatient, Recipe::setPatient);

        binder.forField(priorityComboBox)
                .withValidator(
                        priorityComboBox -> priorityComboBox.getName().length() >= 1,
                        "Приоритет не выбран!")
                .bind(Recipe::getPriority, Recipe::setPriority);
    }


    @Autowired
    public void setRecipeService(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @Autowired
    public void setDoctorService(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @Autowired
    public void setPatientService(PatientService patientService) {
        this.patientService = patientService;
    }

    @Autowired
    public void setPriorityService(PriorityService priorityService) {
        this.priorityService = priorityService;
    }
}
