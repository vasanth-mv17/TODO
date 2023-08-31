package com.crud.todoapplication.model;

public class TodoItem {

    private Long id;
    private String label;
    private boolean isChecked;
    private Long parentId;

    public TodoItem() {

    }
    public TodoItem(final String label) {
        this.label = label;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getSortingValue(final String attribute) {

        if ("label".equals(attribute)) {
            return getLabel();
        } else if ("isChecked".equals(attribute)) {
            return getLabel();
        } else if ("unChecked".equals(attribute)) {
            return getLabel();
        }
        return "";
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked() {
        this.isChecked = !this.isChecked;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String toString() {
        return label;
    }
}

