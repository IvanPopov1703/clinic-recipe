package ru.clinic.recipe.view.specializationView;

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
import ru.clinic.recipe.entityes.Specialization;
import ru.clinic.recipe.exception.RecordNotFoundException;
import ru.clinic.recipe.services.SpecializationService;
import ru.clinic.recipe.view.MainView;
import ru.clinic.recipe.view.doctorView.DoctorList;
import ru.clinic.recipe.view.patientView.PatientList;
import ru.clinic.recipe.view.priorityView.PriorityList;
import ru.clinic.recipe.view.recipeView.RecipeList;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Класс предназначен для отображения
 * и работы со списком специализаций.
 */
@Route("specializationList")
public class SpecializationList extends AppLayout {

    private SpecializationService specializationService;

    private VerticalLayout layout;
    private Grid<Specialization> grid;
    private RouterLink linkCreate;
    private MenuBar menuBar;

    public SpecializationList() {
        layout = new VerticalLayout();
        grid = new Grid<>();
        linkCreate = new RouterLink("Создать специализацию", ManageSpecialization.class, 0L);
        menuBar = new MenuBar();
        layout.add(linkCreate);
        layout.add(grid);
        setupMainMenu();
        addToNavbar(menuBar);
        addToNavbar(new H3("Список специализаций"));
        setContent(layout);
    }

    /**
     * Метод fillGrid предназначен для настройки
     * и заполнения компонента Grid
     */
    @PostConstruct
    private void fillGrid() {
        List<Specialization> specializationList = specializationService.findAll();
        if (!specializationList.isEmpty()) {
            grid.addColumn(Specialization::getName).setHeader("Название");

            /* Обработка кнопки "Редактировать" */
            editButtonHandling();

            /* Обработка кнопки "Удалить" */
            deleteButtonHandling();
            grid.setItems(specializationList);
        }
    }

    /**
     * Метод editButtonHandling предназначен для обработки
     * кнопки "Редактировать"
     */
    private void editButtonHandling() {
        grid.addColumn(new NativeButtonRenderer<Specialization>("Редактировать",
                specialization -> {
                    UI.getCurrent().navigate(ManageSpecialization.class, specialization.getId());
                }));
    }

    /**
     * Метод deleteButtonHandling предназначен для обработки
     * кнопки "Удалить"
     */
    private void deleteButtonHandling() {
        grid.addColumn(new NativeButtonRenderer<Specialization>("Удалить",
                specialization -> {
                    Dialog dialog = new Dialog();
                    Button delete = new Button("Удалить");
                    Button cancel = new Button("Отмена");
                    dialog.add("Вы уверены, что хотите удалить запись?");
                    dialog.add(delete);
                    dialog.add(cancel);
                    delete.addClickListener(clickEvent -> {
                        String message = "";
                        try {
                            specializationService.deleteById(specialization.getId());
                            message = "Запись успешно удалена!";
                        } catch (RecordNotFoundException ex) {
                            message = ex.getMessage();
                        } catch (DataIntegrityViolationException ex) {
                            message = "Для удаления специализации требуется удалить всех " +
                                    "связанных с ней врачей!";
                        }
                        dialog.close();
                        Notification notification = new Notification(message, 1500);
                        notification.setPosition(Notification.Position.MIDDLE);
                        notification.open();
                        grid.setItems(specializationService.findAll());
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
    private void setupMainMenu(){

        /* Главная */
        menuBar.addItem("Главная", event -> {
            UI.getCurrent().navigate(MainView.class);
        });

        /* Справочники */
        MenuItem directory = menuBar.addItem("Справочник");
        SubMenu directorySubMenu = directory.getSubMenu();
        directorySubMenu.addItem("Специализация", event -> {
            UI.getCurrent().navigate(SpecializationList.class);
        }).setEnabled(false);
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
        });

        /* Рецепт */
        menuBar.addItem("Рецепт", event -> {
            UI.getCurrent().navigate(RecipeList.class);
        }).setEnabled(false);
    }

    @Autowired
    public void setSpecializationService(SpecializationService specializationService) {
        this.specializationService = specializationService;
    }

}
