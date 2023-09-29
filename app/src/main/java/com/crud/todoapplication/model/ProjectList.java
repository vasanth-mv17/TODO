package com.crud.todoapplication.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProjectList {

    private List<Project> projectList;

    public ProjectList() {
        this.projectList = new ArrayList<>();
    }

    public boolean add(final Project project) {
        return projectList.add(project);
    }

    public List<Project> getAllList() {
        return projectList;
    }

}
