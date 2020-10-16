package ru.clinic.recipe.view.specializationView;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import ru.clinic.recipe.entityes.Specialization;
import ru.clinic.recipe.exception.RecordNotFoundException;
import ru.clinic.recipe.services.SpecializationService;

/**
 * Класс предназначен для создания новой
 * или редактирования существующей специализации.
 */
@Route("manageSpecialization")
public class ManageSpecialization extends AppLayout implements HasUrlParameter<Long> {

    private SpecializationService specializationService;

    private Long id;
    private VerticalLayout specializationForm;
    private TextField name;
    private Button saveButtonSpecialization;
    private Button cancelButtonSpecialization;
    private Binder<Specialization> binder;


    public ManageSpecialization() {
        specializationForm = new VerticalLayout();
        name = new TextField("Название");
        saveButtonSpecialization = new Button("Сохранить");
        cancelButtonSpecialization = new Button("Отмена");
        binder = new Binder<>(Specialization.class);
        specializationForm.add(
                name,
                saveButtonSpecialization,
                cancelButtonSpecialization
        );
    }

    @Override
    public void setParameter(BeforeEvent event, Long id) {
        this.id = id;
        if (id != 0) {
            addToNavbar(new H3("Редактирование специализации"));
        } else {
            addToNavbar(new H3("Создание специализации"));
        }
        fillForm();
    }

    /**
     * Метод fillForm предназначен
     * для добавления компонентов на форму
     * и обработки кнопок.
     */
    private void fillForm() {
        try {
            if (id != 0) {
                Specialization specialization = specializationService.findById(id);
                name.setValue(specialization.getName());
            }
            /* Обработка кнопки "Сохранить" */
            saveButtonHandling();

            /* Обработка кнопки "Отмена" */
            cancelButtonHandling();
        } catch (RecordNotFoundException ex) {
            specializationForm.removeAll();
            specializationForm.add(new H1(ex.getMessage()));
        }
        setContent(specializationForm);
    }

    /**
     * Метод saveButtonHandling предназначен для обработки
     * кнопки "Сохранить"
     */
    private void saveButtonHandling() {
        saveButtonSpecialization.addClickListener(clickEvent -> {
            Specialization newSpecialization = new Specialization();
            if (id != 0) {
                newSpecialization.setId(this.id);
            }
            newSpecialization.setName(name.getValue().trim());

            /*Проверка валидности полей*/
            checkValidation();
            try {
                binder.writeBean(newSpecialization);
                specializationService.save(newSpecialization);
                /* Вывод уведомления пользователю и переход к списку специализаций */
                Notification notification = new Notification(id == 0 ? "Запись успешно добавлена!"
                        : "Запись успешно изменена!", 1000);
                notification.setPosition(Notification.Position.MIDDLE);
                notification.addDetachListener(detachEvent -> {
                    UI.getCurrent().navigate(SpecializationList.class);
                });
                specializationForm.setEnabled(false);
                notification.open();
            } catch (ValidationException e) { }
        });
    }

    /**
     * Метод cancelButtonHandling предназначен для обработки
     * кнопки "Отмена"
     */
    private void cancelButtonHandling() {
        cancelButtonSpecialization.addClickListener(clickEvent -> {
            Notification notification = new Notification("", 1);
            notification.addDetachListener(detachEvent -> {
                UI.getCurrent().navigate(SpecializationList.class);
            });
            notification.open();
        });
    }

    /**
     * Метод checkValidation предназначен для проверки
     * полей на валидность
     */
    private void checkValidation() {
        binder.forField(name)
                .withValidator(
                        name -> name.length() >= 3,
                        "Поле должно содержать минимум 3 символа")
                .bind(Specialization::getName, Specialization::setName);
    }


    @Autowired
    public void setSpecializationService(SpecializationService specializationService) {
        this.specializationService = specializationService;
    }
}
