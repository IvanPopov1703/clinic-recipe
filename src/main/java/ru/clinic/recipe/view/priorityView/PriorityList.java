package ru.clinic.recipe.view.priorityView;


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
import ru.clinic.recipe.entityes.Priority;
import ru.clinic.recipe.exception.RecordNotFoundException;
import ru.clinic.recipe.services.PriorityService;
import ru.clinic.recipe.view.MainView;
import ru.clinic.recipe.view.doctorView.DoctorList;
import ru.clinic.recipe.view.patientView.PatientList;
import ru.clinic.recipe.view.recipeView.RecipeList;
import ru.clinic.recipe.view.specializationView.SpecializationList;

import javax.annotation.PostConstruct;
import java.util.List;

@Route("priorityList")
public class PriorityList extends AppLayout {

    private PriorityService priorityService;

    private VerticalLayout layout;
    private Grid<Priority> grid;
    private RouterLink linkCreate;
    private MenuBar menuBar;

    public PriorityList() {
        layout = new VerticalLayout();
        grid = new Grid<>();
        linkCreate = new RouterLink("Создать приоритет", ManagePriority.class, 0L);
        menuBar = new MenuBar();
        setupMainMenu();
        layout.add(linkCreate);
        layout.add(grid);
        addToNavbar(menuBar);
        addToNavbar(new H3("Список приоритетов"));
        setContent(layout);
    }

    /**
     * Метод fillGrid предназначен для настройки
     * и заполнения компонента Grid
     */
    @PostConstruct
    private void fillGrid() {
        List<Priority> priorityList = priorityService.findAll();
        if (!priorityList.isEmpty()) {
            grid.addColumn(Priority::getName).setHeader("Название");

            /* Обработка кнопки "Редактировать" */
            editButtonHandling();

            /* Обработка кнопки "Удалить" */
            deleteButtonHandling();
            grid.setItems(priorityList);
        }
    }

    /**
     * Метод editButtonHandling предназначен для обработки
     * кнопки "Редактировать"
     */
    private void editButtonHandling() {
        grid.addColumn(new NativeButtonRenderer<Priority>("Редактировать",
                priority -> {
                    UI.getCurrent().navigate(ManagePriority.class, priority.getId());
                }));
    }

    /**
     * Метод deleteButtonHandling предназначен для обработки
     * кнопки "Удалить"
     */
    private void deleteButtonHandling() {
        grid.addColumn(new NativeButtonRenderer<Priority>("Удалить",
                priority -> {
                    Dialog dialog = new Dialog();
                    Button delete = new Button("Удалить");
                    Button cancel = new Button("Отмена");
                    dialog.add("Вы уверены, что хотите удалить запись?");
                    dialog.add(delete);
                    dialog.add(cancel);
                    delete.addClickListener(clickEvent -> {
                        String message = "";
                        try {
                            priorityService.deleteById(priority.getId());
                            message = "Запись успешно удалена!";
                        } catch (DataIntegrityViolationException ex) {
                            message = "Для удаления приоритета требуется удалить все " +
                                    "связанные с ним рецепты!";
                        } catch (RecordNotFoundException ex) {
                            message = ex.getMessage();
                        }
                        dialog.close();
                        Notification notification = new Notification(message, 1500);
                        notification.setPosition(Notification.Position.MIDDLE);
                        notification.open();
                        grid.setItems(priorityService.findAll());
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
        });
        directorySubMenu.addItem("Приоритет", event -> {
            UI.getCurrent().navigate(PriorityList.class);
        }).setEnabled(false);

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
        });
    }


    @Autowired
    public void setPriorityService(PriorityService priorityService) {
        this.priorityService = priorityService;
    }
}
