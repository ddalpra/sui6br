package it.ddalpra.acme.domain.menu;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class MenuCategory extends MenuItem implements Serializable {

    private final String label;
    private List<MenuItem> menuItems;
    private boolean custom;

    public MenuCategory(String label, List<MenuItem> menuItems) {
        super(label, (String) null);
        this.label = label;
        this.menuItems = menuItems;
    }

    public MenuCategory(String label, List<MenuItem> menuItems, boolean custom) {
        super(label, (String) null);
        this.label = label;
        this.menuItems = menuItems;
        this.custom = custom;
    }

    public String getLabel() {
        return label;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public boolean getCustom() {
        return custom;
    }

    @Override
    public int hashCode() {
        return Objects.hash(label);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof MenuCategory)) {
            return false;
        }
        MenuCategory other = (MenuCategory) obj;
        return Objects.equals(label, other.label);
    }

    @Override
    public String toString() {
        return "MenuCategory [label=" + label + "]";
    }
}