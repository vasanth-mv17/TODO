package com.crud.todoapplication.service;

/**
 * <p>
 * Provides service for the Project List Activity
 * </p>
 *
 * @author vasanth
 * @version 1.0
 */
public interface ProjectView {

    /**
     * <p>
     * Stores the items into table layout
     * </p>
     */
    void addNewTodoItem();

    /**
     * <p>
     * Filter and display items in the tableLayout based on a given query
     * </p>
     *
     * @param query Refer the query to filter items by search
     */
    void filterAndDisplayItems(final String query);
}
