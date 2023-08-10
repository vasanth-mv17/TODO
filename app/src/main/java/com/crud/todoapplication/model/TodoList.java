package com.crud.todoapplication.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TodoList {

    private List<Project> projectList;
    private String parentId;

    public TodoList(final String parentId) {
        this.parentId = parentId;
        this.projectList = new ArrayList<>();
    }
    public String getParentId() {
        return parentId;
    }

    public void setParentId(final String parentId) {
        this.parentId = parentId;
    }

    public void add(final Project project) {
        projectList.add(project);
    }

    public void remove(final String id) {
        projectList = projectList.stream().filter(project -> !Objects.equals(project.getId(), id)).collect(Collectors.toList());
    }

    public List<Project> getAll() {
        return projectList;
    }

    public List getAllList(final Long parentId) {
        return  projectList.stream().filter(project -> getParentId().equals(parentId)).collect(Collectors.toList());
    }
}
