package ru.clinic.recipe.view.doctorView;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import ru.clinic.recipe.entityes.Doctor;
import ru.clinic.recipe.entityes.Specialization;
import ru.clinic.recipe.exception.RecordNotFoundException;
import ru.clinic.recipe.services.DoctorService;
import ru.clinic.recipe.services.SpecializationService;
import ru.clinic.recipe.view.specializationView.SpecializationList;

import javax.print.Doc;

/**
 * Класс предназначен для создания нового
 * или редактирования существующего врача.
 */
@Route("manageDoctor")
public class ManageDoctor extends AppLayout implements HasUrlParameter<Long> {

    private DoctorService doctorService;
    private SpecializationService specializationService;

    private Long id;
    private FormLayout doctorForm;
    private TextField surname;
    private TextField name;
    private TextField patronymic;
    private ComboBox<Specialization> specializationComboBox;
    private Button buttonSaveDoctor;
    private Button buttonCancelDoctor;
    private Binder<Doctor> binder;

    public ManageDoctor() {
        doctorForm = new FormLayout();
        surname = new TextField("Фамилия");
        name = new TextField("Имя");
        patronymic = new TextField("Отчество");
        specializationComboBox = new ComboBox<>();
        specializationComboBox.setItemLabelGenerator(Specialization::getName);
        buttonSaveDoctor = new Button("Сохранить");
        buttonCancelDoctor = new Button("Отмена");
        binder = new Binder<>(Doctor.class);
        doctorForm.add(
                surname,
                name,
                patronymic,
                specializationComboBox,
                buttonSaveDoctor,
                buttonCancelDoctor
        );
    }

    @Override
    public void setParameter(BeforeEvent event, Long id) {
        this.id = id;
        if (id == 0) {
            addToNavbar(new H3("Создание врача"));
        } else {
            addToNavbar(new H3("Редактирование врача"));
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
            specializationComboBox.setItems(specializationService.findAll());
            if (id != 0) {
                Doctor doctor = doctorService.findById(id);
                surname.setValue(doctor.getSurname());
                name.setValue(doctor.getName());
                patronymic.setValue(doctor.getPatronymic());
                specializationComboBox.setLabel("Специализация");
                if (doctor.getSpecialization() != null) {
                    specializationComboBox.setValue(doctor.getSpecialization());
                }
            }

            /* Обработка кнопки "Сохранить" */
            saveButtonHandling();

            /* Обработка кнопки "Отмена" */
            cancelButtonHandling();
        } catch (RecordNotFoundException ex) {
            doctorForm.removeAll();
            doctorForm.add(new H1(ex.getMessage()));
        }
        setContent(doctorForm);
    }

    /**
     * Метод saveButtonHandling предназначен для обработки
     * кнопки "Сохранить"
     */
    private void saveButtonHandling() {
        buttonSaveDoctor.addClickListener(clickEvent -> {
            Doctor newDoctor = new Doctor();
            if (id != 0) {
                newDoctor.setId(this.id);
            }
            newDoctor.setSurname(surname.getValue().trim());
            newDoctor.setName(name.getValue().trim());
            newDoctor.setPatronymic(patronymic.getValue().trim());
            newDoctor.setSpecialization(specializationComboBox.getValue());

            /*Проверка валидности полей*/
            checkValidation();

            try {
                binder.writeBean(newDoctor);
                doctorService.save(newDoctor);

                /* Вывод уведомления пользователю и переход к списку врачей */
                Notification notification = new Notification(id == 0 ? "Запись успешно добавлена!"
                        : "Запись успешно изменена!", 1000);
                notification.setPosition(Notification.Position.MIDDLE);
                notification.addDetachListener(detachEvent -> {
                    UI.getCurrent().navigate(DoctorList.class);
                });
                doctorForm.setEnabled(false);
                notification.open();
            } catch (ValidationException e) { }
        });
    }

    /**
     * Метод cancelButtonHandling предназначен для обработки
     * кнопки "Отмена"
     */
    private void cancelButtonHandling() {
        buttonCancelDoctor.addClickListener(clickEvent -> {
            Notification notification = new Notification("", 1);
            notification.addDetachListener(detachEvent -> {
                UI.getCurrent().navigate(DoctorList.class);
            });
            notification.open();
        });
    }

    /**
     * Метод checkValidation предназначен для проверки
     * полей на валидность
     */
    private void checkValidation() {
        binder.forField(surname)
                .withValidator(
                        surname -> surname.length() >= 4,
                        "Поле должно содержать минимум 4 символа")
                .bind(Doctor::getSurname, Doctor::setSurname);

        binder.forField(name)
                .withValidator(
                        name -> name.length() >= 2,
                        "Поле должно содержать минимум 2 символа")
                .bind(Doctor::getName, Doctor::setName);

        binder.forField(patronymic)
                .withValidator(
                        patronymic -> patronymic.length() >= 4,
                        "Поле должно содержать минимум 4 символа")
                .bind(Doctor::getPatronymic, Doctor::setPatronymic);

        binder.forField(specializationComboBox)
                .withValidator(
                        specializationComboBox -> specializationComboBox.getName().length() >= 1,
                        "Специализация не выбрана")
                .bind(Doctor::getSpecialization, Doctor::setSpecialization);
    }


    @Autowired
    public void setDoctorService(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @Autowired
    public void setSpecialization(SpecializationService specializationService) {
        this.specializationService = specializationService;
    }
}
