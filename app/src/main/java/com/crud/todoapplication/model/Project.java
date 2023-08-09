package com.crud.todoapplication.model;

import java.util.UUID;

public class Project {

    private String id;
    private String label;
    private boolean isChecked;

    public Project() {
    }

    public Project(final String label) {
        this.label = label;
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked() {
        this.isChecked = !this.isChecked;
    }

    public String toString() {
        return label;
    }
}
