package it.ddalpra.acme.menu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@Named
@ApplicationScoped
public class AppMenu {

    List<MenuCategory> menuCategories;
    List<MenuItem> menuItems;

    @PostConstruct
    public void init() {
        menuCategories = new ArrayList<>();
        menuItems = new ArrayList<>();

        //Menu di base delle impostazioni
        List<MenuItem> impostazioniMenu = new ArrayList<>();
        impostazioniMenu.add(new MenuItem("Applicazioni", "/impostazioni"));
        menuCategories.add(new MenuCategory("Impostazioni", impostazioniMenu));
        //Menu di base delle impostazioni fine

        
        for (MenuCategory category : menuCategories) {
            for (MenuItem menuItem : category.getMenuItems()) {
                menuItem.setParent(category);
                if (menuItem.getUrl() != null) {
                    menuItems.add(menuItem);
                }
                if (menuItem.getMenuItems() != null) {
                    for (MenuItem item : menuItem.getMenuItems()) {
                        item.setParent(menuItem);
                        if (item.getUrl() != null) {
                            menuItems.add(item);
                        }
                    }
                }
            }
        }
    }

    public List<MenuItem> completeMenuItem(String query) {
        String queryLowerCase = query.toLowerCase();
        List<MenuItem> filteredItems = new ArrayList<>();
        for (MenuItem item : menuItems) {
            if (item.getUrl() != null
                    && (item.getLabel().toLowerCase().contains(queryLowerCase) || anyParentContainsQuery(item, queryLowerCase))) {
                filteredItems.add(item);
            } else if (item.getBadge() != null) {
                if (item.getBadge().toLowerCase().contains(queryLowerCase)) {
                    filteredItems.add(item);
                }
            }
        }
        filteredItems.sort(Comparator.comparing(m -> m.getParent().getLabel()));
        return filteredItems;
    }

    protected boolean anyParentContainsQuery(MenuItem item, String query) {
        MenuItem parent = item.getParent();
        while (parent != null) {
            if (parent.getLabel().toLowerCase().contains(query)) {
                return true;
            }
            parent = parent.getParent();
        }

        return false;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public List<MenuCategory> getMenuCategories() {
        return menuCategories;
    }
}