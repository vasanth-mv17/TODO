package com.crud.todoapplication.model;

import java.util.UUID;

public class TodoItem {

    private String id;
    private String label;
    private boolean isChecked;
    private boolean completed;

    public TodoItem() {
    }

    public TodoItem(final String label) {
        this.label = label;
        this.completed = completed;
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

    public boolean isCompleted() {
        return completed;
    }

//    public void setCompleted(boolean completed) {
//        this.completed = completed;
//    }

    public String toString() {
        return label;
    }
}
