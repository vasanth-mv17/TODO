package com.crud.todoapplication.model;

public class TodoItem {

    private Long id;
    private String label;
    private boolean isChecked;
    private Long parentId;
    private Status status;
    private Long order;

   public enum Status {
       CHECKED,
       UNCHECKED;
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

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }

    public boolean isChecked() {
        return isChecked;
    }

//    public void setChecked(final boolean isChecked) {
//        this.isChecked = isChecked;
//    }
    public void setChecked() {
    this.isChecked = !this.isChecked;
}

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(final Long parentId) {
        this.parentId = parentId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }
    public String toString() {
        return label;
    }
}

