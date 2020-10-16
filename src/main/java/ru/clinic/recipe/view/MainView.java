package ru.clinic.recipe.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import org.springframework.beans.factory.annotation.Autowired;
import ru.clinic.recipe.services.SpecializationService;
import ru.clinic.recipe.view.doctorView.DoctorList;
import ru.clinic.recipe.view.patientView.PatientList;
import ru.clinic.recipe.view.priorityView.ManagePriority;
import ru.clinic.recipe.view.priorityView.PriorityList;
import ru.clinic.recipe.view.recipeView.RecipeList;
import ru.clinic.recipe.view.specializationView.SpecializationList;

@Route("")
public class MainView extends VerticalLayout {
    private MenuBar menuBar;

    public MainView() {
        menuBar = new MenuBar();
        setupMainMenu();
        add(menuBar);
    }

    private void setupMainMenu(){

        /* Главная */
        menuBar.addItem("Главная", event -> {
            UI.getCurrent().navigate(MainView.class);
        }).setEnabled(false);

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
        });
    }
}
