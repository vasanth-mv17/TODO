package com.crud.todoapplication.controller;

import android.content.Context;
import android.content.Intent;


import com.crud.todoapplication.ProjectListActivity;
import com.crud.todoapplication.model.TodoItem;
import com.crud.todoapplication.service.MenuView;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Handles request and response for the menu activity
 * </p>
 *
 * @author vasanth
 * @version 1.0
 */
public class MenuController {

    private MenuView view;
    private List<String> todoList;
    private String selectedList;
    private Context context;

    public MenuController(final Context context, final MenuView view) {
        this.context = context;
        this.view = view;
        todoList = new ArrayList<>();
    }

    /**
     * <p>
     * Stores the items into table layout
     * </p>
     */
    public void onAddNameClicked() {
        view.showAddNameDialog();
    }

    /**
     * <p>
     * Called when a new name is added to the project
     * </p>
     *
     * @param name Refer the name to be added to the project
     */
    public void onNameAdded(final String name) {
        if (!name.isEmpty()) {
            final TodoItem todoItem = new TodoItem();
            todoItem.setLabel(name);
            todoList.add(todoItem.toString());
            updateView();
        }
    }

    /**
     * <p>
     * Updates the view with the current todo list
     * </p>
     */
    public void updateView() {
        view.updateTodoList(todoList);
    }

    /**
     * <p>
     * The list Item is to applied has to be added
     * </p>
     *
     * @param indexPosition Refers the index position for the navigate to another activity
     */
    public void onListItemClicked(int indexPosition) {
        final Intent intent = new Intent(context, ProjectListActivity.class);
        selectedList = todoList.get(indexPosition);
        intent.putExtra("List Reference", selectedList);
        context.startActivity(intent);
    }

    /**
     * <p>
     * By Long press the list is to be deleted
     * </p>
     *
     * @param indexPosition Refers the index position for the deletion
     */
    public void onListItemLongClicked(int indexPosition) {
        todoList.remove(indexPosition);
        updateView();
    }
}

