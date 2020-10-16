package ru.clinic.recipe.view.recipeView;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.NativeButtonRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import org.springframework.beans.factory.annotation.Autowired;
import ru.clinic.recipe.entityes.Doctor;
import ru.clinic.recipe.entityes.Patient;
import ru.clinic.recipe.entityes.Priority;
import ru.clinic.recipe.entityes.Recipe;
import ru.clinic.recipe.exception.RecordNotFoundException;
import ru.clinic.recipe.services.RecipeService;
import ru.clinic.recipe.view.MainView;
import ru.clinic.recipe.view.doctorView.DoctorList;
import ru.clinic.recipe.view.patientView.PatientList;
import ru.clinic.recipe.view.priorityView.PriorityList;
import ru.clinic.recipe.view.specializationView.SpecializationList;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс предназначен для отображения
 * и работы со списком рецептов.
 */
@Route("recipeList")
public class RecipeList extends AppLayout {

    private RecipeService recipeService;

    private Grid<Recipe> recipeGrid;
    private VerticalLayout layout;
    private VerticalLayout sortingPanel;
    private RouterLink linkCreate;
    private CheckboxGroup<String> sortingCheckBox;
    private Button sortingButton;
    private MenuBar menuBar;

    private VerticalLayout filtrationPanel;
    private TextField filtrationTextField;
    private CheckboxGroup<String> filtrationCheckBox;
    private Button filtrationButton;

    public RecipeList() {
        layout = new VerticalLayout();
        sortingPanel = new VerticalLayout();
        sortingButton = new Button("Сортировать");
        linkCreate = new RouterLink("Создать рецепт", ManageRecipe.class, 0L);
        recipeGrid = new Grid<>();
        menuBar = new MenuBar();
        sortingCheckBox = new CheckboxGroup<>();

        filtrationPanel = new VerticalLayout();
        filtrationTextField = new TextField("Фильтрация");
        filtrationCheckBox = new CheckboxGroup<>();
        filtrationButton = new Button("Примернить");

        componentSetup();
        layout.add(linkCreate, recipeGrid);
        addToNavbar(menuBar);
        addToNavbar(new H3("Список рецептов"));
        setContent(layout);
        sortButtonHandling();
        filtrationButtonHandling();
        setupMainMenu();
    }

    /**
     * Метод fillGrid предназначен для настройки
     * и заполнения компонента Grid
     */
    @PostConstruct
    private void fillGrid() {
        List<Recipe> recipeList = recipeService.findAll();
        if (!recipeList.isEmpty()) {
            recipeGrid.addColumn(recipe -> {
                Doctor doctor = recipe.getDoctor();
                return doctor == null ? "-" : doctor.toString();
            }).setHeader("Врач").setAutoWidth(true);
            recipeGrid.addColumn(recipe -> {
                Patient patient = recipe.getPatient();
                return patient == null ? "-" : patient.toString();
            }).setHeader("Пациент").setAutoWidth(true);
            recipeGrid.addColumn(Recipe::getParseDateOfCreation)
                    .setHeader("Дата назначения").setAutoWidth(true);
            recipeGrid.addColumn(Recipe::getParseExpirationDate)
                    .setHeader("Действует до").setAutoWidth(true);
            recipeGrid.addColumn(recipe -> {
                Priority priority = recipe.getPriority();
                return priority == null ? "-" : priority.getName();
            }).setHeader("Приоритет").setAutoWidth(true);
            recipeGrid.addColumn(Recipe::getDescription).setHeader("Описание");

            /* Обработка кнопки "Редактировать" */
            editButtonHandling();

            /* Обработка кнопки "Удалить" */
            deleteButtonHandling();
            recipeGrid.setItems(recipeService.findAll());
            //recipeGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        }
    }

    /**
     * Метод filtrationButtonHandling предназначен для обработки
     * кнопки "Сортировать"
     */
    private void filtrationButtonHandling() {
        filtrationButton.addClickListener(clickEvent -> {
            String strQuery = filtrationTextField.getValue().trim();
            if (strQuery != null) {
                recipeGrid.setItems(recipeService.getFiltrationList(strQuery));
            }
        });
    }

    /**
     * Метод stringCreate предназначен для формирования строки
     * которая подставляется в запрос при сортировки
     *
     * @return сформированная строка или {@code null}, если строка пуста
     */
    private String stringCreate() {
        String queryStr = "";
        int lenStr = queryStr.length();
        List<String> stringList = new ArrayList<>();
        stringList.addAll(sortingCheckBox.getSelectedItems());
        for (String s : stringList) {
            switch (s) {
                case "Пациент":
                    if (lenStr != queryStr.length()) {
                        queryStr += ", PAT.SURNAME, PAT.NAME, PAT.PATRONYMIC";
                    } else {
                        queryStr += "PAT.SURNAME, PAT.NAME, PAT.PATRONYMIC";
                    }
                    break;
                case "Приоритет":
                    if (lenStr != queryStr.length()) {
                        queryStr += ", P.NAME";
                    } else {
                        queryStr += "P.NAME";
                    }
                    break;
            }
        }
        return lenStr == queryStr.length() ? null : queryStr;
    }

    /**
     * Метод sortButtonHandling предназначен для обработки
     * кнопки "Сортировать"
     */
    private void sortButtonHandling() {
        sortingButton.addClickListener(clickEvent -> {
            String strQuery = stringCreate();
            if (strQuery != null) {
                recipeGrid.setItems(recipeService.getSortedList(strQuery));
            }
        });
    }

    /**
     * Метод deleteButtonHandling предназначен для обработки
     * кнопки "Удалить"
     */
    private void deleteButtonHandling() {
        recipeGrid.addColumn(new NativeButtonRenderer<Recipe>("Удалить", recipe -> {
            Dialog dialog = new Dialog();
            Button delete = new Button("Удалить");
            Button cancel = new Button("Отмена");
            dialog.add("Вы уверены, что хотите удалить запись?");
            dialog.add(delete, cancel);
            delete.addClickListener(clickEvent -> {
                String message = "";
                try {
                    recipeService.deleteById(recipe.getId());
                    message = "Запись успешно удалена!";
                } catch (RecordNotFoundException ex) {
                    message = ex.getMessage();
                }
                dialog.close();
                Notification notification = new Notification(message, 1500);
                notification.setPosition(Notification.Position.MIDDLE);
                notification.open();
                recipeGrid.setItems(recipeService.findAll());
            });
            cancel.addClickListener(clickEvent -> {
                dialog.close();
            });
            dialog.open();
        })).setAutoWidth(true);
    }

    /**
     * Метод editButtonHandling предназначен для обработки
     * кнопки "Редактировать"
     */
    private void editButtonHandling() {
        recipeGrid.addColumn(new NativeButtonRenderer<Recipe>("Редактировать",
                recipe -> {
                    UI.getCurrent().navigate(ManageRecipe.class, recipe.getId());
                })).setAutoWidth(true);
    }

    private void componentSetup() {

        /*Настройка для фильтрации*/
        filtrationCheckBox.setLabel("Фильтрация");
        filtrationCheckBox.setItems("Пациент", "Приоритет", "Описание");
        filtrationPanel.add(filtrationCheckBox, filtrationTextField, filtrationButton);

        /*Настройка для сортировки*/
        sortingCheckBox.setLabel("Сортировка");
        sortingCheckBox.setItems("Пациент", "Приоритет");
        sortingPanel.add(sortingCheckBox, sortingButton);
        addToDrawer(filtrationPanel, sortingPanel);
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
    public void setRecipeService(RecipeService recipeService) {
        this.recipeService = recipeService;
    }
}
