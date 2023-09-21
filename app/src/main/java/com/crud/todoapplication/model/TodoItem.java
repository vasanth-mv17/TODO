package com.crud.todoapplication.model;

public class TodoItem {

    private String id;
    private String name;
    private String description;
    private boolean isChecked;
    private String parentId;
    private Status status;
    private Long order;

   public enum Status {
       CHECKED,
       UNCHECKED;
   }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSortingValue(final String attribute) {

        if ("label".equals(attribute)) {
            return getName();
        } else if ("isChecked".equals(attribute)) {
            return getName();
        } else if ("unChecked".equals(attribute)) {
            return getName();
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

    public String getParentId() {
        return parentId;
    }

    public void setParentId(final String parentId) {
        this.parentId = parentId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }
    public String toString() {
        return name;
    }
}

