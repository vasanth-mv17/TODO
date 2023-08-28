package com.crud.todoapplication.controller;

import com.crud.todoapplication.service.ProjectView;

/**
 * <p>
 * Handles request and response for the project list activity
 * </p>
 *
 * @author vasanth
 * @version 1.0
 */
public class ProjectListController {

    private ProjectView projectView;

    public ProjectListController(final ProjectView projectView) {
        this.projectView = projectView;
    }

    /**
     * <p>
     * Stores the items into table layout
     * </p>
     */
    public void onAddButtonClick() {
        projectView.addNewTodoItem();
    }

    /**
     * <p>
     * Filter and display items in the tableLayout based on a given query
     * </p>
     *
     * @param newText Refer the new text to filter items by search
     */
    public void onSearchAndDisplayItems(final String newText) {
        projectView.filterAndDisplayItems(newText);
    }
}
