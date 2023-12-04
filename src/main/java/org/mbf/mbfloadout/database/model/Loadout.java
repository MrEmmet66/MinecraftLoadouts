package org.mbf.mbfloadout.database.model;

import java.io.Serializable;

public class Loadout implements Serializable {
    private String name;
    private String items;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public Loadout(String name, String items) {
        this.name = name;
        this.items = items;
    }

}
