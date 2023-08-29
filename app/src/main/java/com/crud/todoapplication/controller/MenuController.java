package com.crud.todoapplication.controller;

import android.content.Context;

import com.crud.todoapplication.DatabaseConnection;
import com.crud.todoapplication.MenuActivity;
import com.crud.todoapplication.model.Project;
import com.crud.todoapplication.model.ProjectList;
import com.crud.todoapplication.service.MenuView;

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
    private ProjectList projectList;
    private MenuActivity menuActivity;
    private DatabaseConnection databaseConnection;


    public MenuController(final MenuActivity menuActivity, final MenuView view, final ProjectList projectList,final Context context) {
        this.view = view;
        this.menuActivity = menuActivity;
        this.projectList = projectList;
        this.databaseConnection = new DatabaseConnection(context);
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
    public void onNameAdded(final String name, final Long id, final Long userId) {
        if (!name.isEmpty()) {
            final Project project = new Project();
            project.setUserId(userId);
            project.setId(id);
            project.setLabel(name);
            projectList.add(project);
            databaseConnection.insertProject(project);
        }
    }

    /**
     * <p>
     * Updates the view with the current todo list
     * </p>
     */
    public void updateView() {
        //view.updateTodoList(projectList);
    }

    /**
     * <p>
     * The list Item is to applied has to be added
     * </p>
     *
     * @param indexPosition Refers the index position for the navigate to another activity
     */
    public void onListItemClicked(final Project indexPosition) {
//        final Intent intent = new Intent(context, ProjectTodoItemActivity.class);
//        //selectedList = projectList.get(indexPosition);
//        intent.putExtra("List Reference", selectedList);
//        context.startActivity(intent);
        menuActivity.goToListPage(indexPosition);
    }

    /**
     * <p>
     * By Long press the list is to be deleted
     * </p>
     *
     * @param project Refers the project for the deletion
     */
    public void onListItemLongClicked(final Project project) {
        //projectList.remove(indexPosition);
        //updateView();
        projectList.remove(project.getId());
        menuActivity.removeProjectFromList(project);
    }
}

