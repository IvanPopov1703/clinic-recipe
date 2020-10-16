package ru.clinic.recipe.view.patientView;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
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
import ru.clinic.recipe.entityes.Patient;
import ru.clinic.recipe.exception.RecordNotFoundException;
import ru.clinic.recipe.services.PatientService;
import ru.clinic.recipe.view.doctorView.DoctorList;

@Route("managePatient")
public class ManagePatient extends AppLayout implements HasUrlParameter<Long> {

    private PatientService patientService;

    private Long id;
    private FormLayout patientForm;
    private TextField surname;
    private TextField name;
    private TextField patronymic;
    private TextField phone;
    private Button buttonSavePatient;
    private Button buttonCancelPatient;
    private Binder<Patient> binder;

    public ManagePatient(){
        patientForm = new FormLayout();
        surname = new TextField("Фамилия");
        name = new TextField("Имя");
        patronymic = new TextField("Отчество");
        phone = new TextField("Телефон");
        buttonSavePatient = new Button("Сохранить");
        buttonCancelPatient = new Button("Отмена");
        binder = new Binder<>(Patient.class);
        patientForm.add(
                surname,
                name,
                patronymic,
                phone,
                buttonSavePatient,
                buttonCancelPatient
        );
    }

    @Override
    public void setParameter(BeforeEvent event, Long id) {
        this.id = id;
        if (id == 0) {
            addToNavbar(new H3("Создание пациента"));
        } else {
            addToNavbar(new H3("Редактирование пациента"));
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
                Patient patient = patientService.findById(id);
                surname.setValue(patient.getSurname());
                name.setValue(patient.getName());
                patronymic.setValue(patient.getPatronymic());
                phone.setValue(patient.getPhone());
            }

            /* Обработка кнопки "Сохранить" */
            saveButtonHandling();

            /* Обработка кнопки "Отмена" */
            cancelButtonHandling();
        } catch (RecordNotFoundException ex) {        //Обработка исключительной ситуации
            patientForm.removeAll();
            patientForm.add(new H1(ex.getMessage()));
        }
        setContent(patientForm);
    }

    /**
     * Метод saveButtonHandling предназначен для обработки
     * кнопки "Сохранить"
     */
    private void saveButtonHandling() {
        buttonSavePatient.addClickListener(clickEvent -> {
            Patient newPatient = new Patient();
            if (id != 0) {
                newPatient.setId(this.id);
            }
            newPatient.setSurname(surname.getValue().trim());
            newPatient.setName(name.getValue().trim());
            newPatient.setPatronymic(patronymic.getValue().trim());
            newPatient.setPhone(phone.getValue().trim());

            /*Проверка валидности полей*/
            checkValidation();

            try {
                binder.writeBean(newPatient);
                patientService.save(newPatient);

                /* Вывод уведомления пользователю и переход к списку пациентов */
                Notification notification = new Notification(id == 0 ? "Запись успешно добавлена!"
                        : "Запись успешно изменена!", 1000);
                notification.setPosition(Notification.Position.MIDDLE);
                notification.addDetachListener(detachEvent -> {
                    UI.getCurrent().navigate(PatientList.class);
                });
                patientForm.setEnabled(false);
                notification.open();
            } catch (ValidationException e) { }
        });
    }

    /**
     * Метод cancelButtonHandling предназначен для обработки
     * кнопки "Отмена"
     */
    private void cancelButtonHandling() {
        buttonCancelPatient.addClickListener(clickEvent -> {
            Notification notification = new Notification("", 1);
            notification.addDetachListener(detachEvent -> {
                UI.getCurrent().navigate(PatientList.class);
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
                .bind(Patient::getSurname, Patient::setSurname);

        binder.forField(name)
                .withValidator(
                        name -> name.length() >= 2,
                        "Поле должно содержать минимум 2 символа")
                .bind(Patient::getName, Patient::setName);

        binder.forField(patronymic)
                .withValidator(
                        patronymic -> patronymic.length() >= 4,
                        "Поле должно содержать минимум 4 символа")
                .bind(Patient::getPatronymic, Patient::setPatronymic);

        binder.forField(phone)
                .withValidator(
                        phone -> phone.length() >= 6
                                && phone.length() <= 11,
                        "Поле должно содержать от 6 до 11 символов")
                .bind(Patient::getPhone, Patient::setPhone);
    }

    @Autowired
    public void setPatientService(PatientService patientService){
        this.patientService = patientService;
    }
}
