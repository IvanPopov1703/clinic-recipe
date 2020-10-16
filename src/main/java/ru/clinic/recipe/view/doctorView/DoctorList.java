package ru.clinic.recipe.view.doctorView;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dependency.CssImport;
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
import ru.clinic.recipe.entityes.Doctor;
import ru.clinic.recipe.entityes.Specialization;
import ru.clinic.recipe.exception.RecordNotFoundException;
import ru.clinic.recipe.services.DoctorService;
import ru.clinic.recipe.view.MainView;
import ru.clinic.recipe.view.patientView.PatientList;
import ru.clinic.recipe.view.priorityView.PriorityList;
import ru.clinic.recipe.view.recipeView.RecipeList;
import ru.clinic.recipe.view.specializationView.SpecializationList;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Класс предназначен для отображения
 * и работы со списком врачей.
 */
@Route("doctorList")
@CssImport("./styles/style.css")
public class DoctorList extends AppLayout {

    private DoctorService doctorService;
    private final String STATISTICS_TEXT = "Количество выданных рецептов: ";

    private Grid<Doctor> doctorGrid;
    private VerticalLayout layout;
    private RouterLink linkCreate;
    private H3 statisticsText;
    private MenuBar menuBar;

    public DoctorList() {
        layout = new VerticalLayout();
        linkCreate = new RouterLink("Создать врача", ManageDoctor.class, 0L);
        doctorGrid = new Grid<>();
        menuBar = new MenuBar();
        statisticsText = new H3(STATISTICS_TEXT + "-");
        setupMainMenu();
        addToNavbar(menuBar);
        statisticsText.addClassName("textH3");
        layout.add(linkCreate, doctorGrid);
        layout.add(doctorGrid);
        addToNavbar(new H3("Список врачей  "), statisticsText);
        setContent(layout);
    }

    /**
     * Метод fillGrid предназначен для настройки
     * и заполнения компонента Grid
     */
    @PostConstruct
    private void fillGrid() {
        List<Doctor> doctorList = doctorService.findAll();
        if (!doctorList.isEmpty()) {
            doctorGrid.addColumn(Doctor::getSurname).setHeader("Фамилия");
            doctorGrid.addColumn(Doctor::getName).setHeader("Имя");
            doctorGrid.addColumn(Doctor::getPatronymic).setHeader("Отчество");
            doctorGrid.addColumn(doctor -> {
                Specialization specialization = doctor.getSpecialization();
                return specialization == null ? "-" : specialization.getName();
            }).setHeader("Специализация");
            //doctorGrid.addColumn(statisticsButton);

            /* Обработка кнопки "Редактировать" */
            editButtonHandling();

            /* Обработка кнопки "Удалить" */
            deleteButtonHandling();

            /* Обработка кнопки "Статистика" */
            statisticsButtonHandling();
            doctorGrid.setItems(doctorService.findAll());
        }
    }

    /**
     * Метод editButtonHandling предназначен для обработки
     * кнопки "Редактировать"
     */
    private void editButtonHandling() {
        doctorGrid.addColumn(new NativeButtonRenderer<Doctor>("Редактировать",
                doctor -> {
                    UI.getCurrent().navigate(ManageDoctor.class, doctor.getId());
                }));
    }

    /**
     * Метод deleteButtonHandling предназначен для обработки
     * кнопки "Удалить"
     */
    private void deleteButtonHandling() {
        doctorGrid.addColumn(new NativeButtonRenderer<Doctor>("Удалить", doctor -> {
            Dialog dialog = new Dialog();
            Button delete = new Button("Удалить");
            Button cancel = new Button("Отмена");
            dialog.add("Вы уверены, что хотите удалить запись?");
            dialog.add(delete, cancel);
            delete.addClickListener(clickEvent -> {
                String message = "";
                try {
                    doctorService.deleteById(doctor.getId());
                    message = "Запись успешно удалена!";
                } catch (RecordNotFoundException ex) {
                    message = ex.getMessage();
                } catch (DataIntegrityViolationException ex) {
                    message = "Для удаления доктора требуется удалить все " +
                            "связанные с ним рецепты!";
                }
                dialog.close();
                Notification notification = new Notification(message, 1500);
                notification.setPosition(Notification.Position.MIDDLE);
                notification.open();
                doctorGrid.setItems(doctorService.findAll());
            });
            cancel.addClickListener(clickEvent -> {
                dialog.close();
            });
            dialog.open();
        }));
    }

    /**
     * Метод statisticsButtonHandling предназначен для обработки
     * кнопки "Статистика"
     */
    private void statisticsButtonHandling() {
        doctorGrid.addColumn(new NativeButtonRenderer<Doctor>("Показать статистику",
                doctor -> {
                    statisticsText.setText(STATISTICS_TEXT + String.valueOf(
                            doctorService.getNumberOfIssuedRecipes(doctor.getId())));
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
        }).setEnabled(false);

        /* Поциент */
        menuBar.addItem("Пациент", event -> {
            UI.getCurrent().navigate(PatientList.class);
        });

        /* Рецепт */
        menuBar.addItem("Рецепт", event -> {
            UI.getCurrent().navigate(RecipeList.class);
        });
    }

    @Autowired
    public void setDoctorService(DoctorService doctorService) {
        this.doctorService = doctorService;
    }
}
