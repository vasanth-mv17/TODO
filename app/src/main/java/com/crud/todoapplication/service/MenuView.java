package com.crud.todoapplication.service;

import java.util.List;

/**
 * <p>
 * Provides service for the Menu activity
 * </p>
 *
 * @author vasanth
 * @version 1.0
 */
public interface MenuView {

    /**
     * <p>
     * Displays a dialog box for adding a new list name
     * </p>
     */
    void showAddNameDialog();

    /**
     * <p>
     * Updates the todo list displayed in the listView
     * </p>
     *
     * @param todoList Refer the list of the todo list to be displayed
     */
    void updateTodoList(List<String> todoList);
}
