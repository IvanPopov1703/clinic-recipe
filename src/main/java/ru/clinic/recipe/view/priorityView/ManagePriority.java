package ru.clinic.recipe.view.priorityView;

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
import ru.clinic.recipe.entityes.Priority;
import ru.clinic.recipe.exception.RecordNotFoundException;
import ru.clinic.recipe.services.PriorityService;
import ru.clinic.recipe.view.priorityView.PriorityList;

@Route("managePriority")
public class ManagePriority extends AppLayout implements HasUrlParameter<Long> {

    private PriorityService priorityService;

    private Long id;
    private VerticalLayout priorityForm;
    private TextField name;
    private Button saveButtonPriority;
    private Button cancelButtonPriority;
    private Binder<Priority> binder;

    public ManagePriority(){
        priorityForm = new VerticalLayout();
        name = new TextField("Название");
        saveButtonPriority = new Button("Сохранить");
        cancelButtonPriority = new Button("Отмена");
        binder = new Binder<>(Priority.class);
        priorityForm.add(
                name,
                saveButtonPriority,
                cancelButtonPriority
        );
    }

    @Override
    public void setParameter(BeforeEvent event, Long id) {
        this.id = id;
        if (id != 0) {
            addToNavbar(new H3("Редактирование приоритета"));
        } else {
            addToNavbar(new H3("Создание приоритета"));
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
                Priority priority = priorityService.findById(id);
                name.setValue(priority.getName());
            }
            /* Обработка кнопки "Сохранить" */
            saveButtonHandling();

            /* Обработка кнопки "Отмена" */
            cancelButtonHandling();
        } catch (RecordNotFoundException ex) {
            priorityForm.removeAll();
            priorityForm.add(new H1(ex.getMessage()));
        }
        setContent(priorityForm);
    }

    /**
     * Метод saveButtonHandling предназначен для обработки
     * кнопки "Сохранить"
     */
    private void saveButtonHandling() {
        saveButtonPriority.addClickListener(clickEvent -> {
            Priority newPriority = new Priority();
            if (id != 0) {
                newPriority.setId(this.id);
            }
            newPriority.setName(name.getValue().trim());

            /*Проверка валидности полей*/
            checkValidation();
            try {
                binder.writeBean(newPriority);
                priorityService.save(newPriority);
                /* Вывод уведомления пользователю и переход к списку приоритетов */
                Notification notification = new Notification(id == 0 ? "Запись успешно добавлена!"
                        : "Запись успешно изменена!", 1000);
                notification.setPosition(Notification.Position.MIDDLE);
                notification.addDetachListener(detachEvent -> {
                    UI.getCurrent().navigate(PriorityList.class);
                });
                priorityForm.setEnabled(false);
                notification.open();
            } catch (ValidationException e) { }
        });
    }

    /**
     * Метод cancelButtonHandling предназначен для обработки
     * кнопки "Отмена"
     */
    private void cancelButtonHandling() {
        cancelButtonPriority.addClickListener(clickEvent -> {
            Notification notification = new Notification("", 1);
            notification.addDetachListener(detachEvent -> {
                UI.getCurrent().navigate(PriorityList.class);
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
                .bind(Priority::getName, Priority::setName);
    }

    @Autowired
    public void setPriorityService(PriorityService priorityService){
        this.priorityService = priorityService;
    }
}
