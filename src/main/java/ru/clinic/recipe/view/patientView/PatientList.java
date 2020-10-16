package ru.clinic.recipe.view.patientView;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.NativeButtonRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import ru.clinic.recipe.entityes.Patient;
import ru.clinic.recipe.exception.RecordNotFoundException;
import ru.clinic.recipe.services.PatientService;
import ru.clinic.recipe.view.MainView;
import ru.clinic.recipe.view.doctorView.DoctorList;
import ru.clinic.recipe.view.priorityView.PriorityList;
import ru.clinic.recipe.view.recipeView.RecipeList;
import ru.clinic.recipe.view.specializationView.SpecializationList;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Класс предназначен для отображения
 * и работы со списком пациентов.
 */
@Route("patientList")
public class PatientList extends AppLayout {

    private PatientService patientService;

    private Grid<Patient> patientGrid;
    private VerticalLayout layout;
    private RouterLink linkCreate;
    private MenuBar menuBar;

    public PatientList() {
        layout = new VerticalLayout();
        linkCreate = new RouterLink("Создать пациента", ManagePatient.class, 0L);
        patientGrid = new Grid<>();
        menuBar = new MenuBar();
        setupMainMenu();
        layout.add(linkCreate, patientGrid);
        layout.add(patientGrid);
        addToNavbar(menuBar);
        addToNavbar(new H3("Список пациентов"));
        setContent(layout);
    }

    /**
     * Метод fillGrid предназначен для настройки
     * и заполнения компонента Grid
     */
    @PostConstruct
    private void fillGrid() {
        List<Patient> patientList = patientService.findAll();
        if (!patientList.isEmpty()) {
            patientGrid.addColumn(Patient::getSurname).setHeader("Фамилия");
            patientGrid.addColumn(Patient::getName).setHeader("Имя");
            patientGrid.addColumn(Patient::getPatronymic).setHeader("Отчество");
            patientGrid.addColumn(Patient::getPhone).setHeader("Телефон");

            /* Обработка кнопки "Редактировать" */
            editButtonHandling();

            /* Обработка кнопки "Удалить" */
            deleteButtonHandling();

            patientGrid.setItems(patientService.findAll());
        }
    }

    /**
     * Метод editButtonHandling предназначен для обработки
     * кнопки "Редактировать"
     */
    private void editButtonHandling() {
        patientGrid.addColumn(new NativeButtonRenderer<Patient>("Редактировать",
                patient -> {
                    UI.getCurrent().navigate(ManagePatient.class, patient.getId());
                }));
    }

    /**
     * Метод deleteButtonHandling предназначен для обработки
     * кнопки "Удалить"
     */
    private void deleteButtonHandling() {
        patientGrid.addColumn(new NativeButtonRenderer<Patient>("Удалить", patient -> {
            Dialog dialog = new Dialog();
            Button delete = new Button("Удалить");
            Button cancel = new Button("Отмена");
            dialog.add("Вы уверены, что хотите удалить запись?");
            dialog.add(delete, cancel);
            delete.addClickListener(clickEvent -> {
                String message = "";
                try {
                    patientService.deleteById(patient.getId());
                    message = "Запись успешно удалена!";
                } catch (RecordNotFoundException ex) {
                    message = ex.getMessage();
                } catch (DataIntegrityViolationException ex) {
                    message = "Для удаления пациента требуется удалить все " +
                            "связанные с ним рецепты!";
                }
                dialog.close();
                Notification notification = new Notification(message, 1500);
                notification.setPosition(Notification.Position.MIDDLE);
                notification.open();
                patientGrid.setItems(patientService.findAll());
            });
            cancel.addClickListener(clickEvent -> {
                dialog.close();
            });
            dialog.open();
        }));
    }

    /*
     * Метод для настройки меню
     * */
    private void setupMainMenu() {

        /* Главная */
        menuBar.addItem("Главная", event -> {
            UI.getCurrent().navigate(MainView.class);
        });

        /* Справочники */
        MenuItem directory = menuBar.addItem("Справочник");
        SubMenu directorySubMenu = directory.getSubMenu();
        directorySubMenu.addItem("Специализация", event -> {
            UI.getCurrent().navigate(SpecializationList.class);
        });
        directorySubMenu.addItem("Приоритет", event -> {
            UI.getCurrent().navigate(PriorityList.class);
        });

        /* Доктор */
        menuBar.addItem("Врач", event -> {
            UI.getCurrent().navigate(DoctorList.class);
        });

        /* Поциент */
        menuBar.addItem("Пациент", event -> {
            UI.getCurrent().navigate(PatientList.class);
        }).setEnabled(false);

        /* Рецепт */
        menuBar.addItem("Рецепт", event -> {
            UI.getCurrent().navigate(RecipeList.class);
        });
    }

    @Autowired
    public void setPatientService(PatientService patientService) {
        this.patientService = patientService;
    }
}
