package com.crud.todoapplication.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import com.crud.todoapplication.ProjectTodoItemActivity;
import com.crud.todoapplication.ProjectTodoItemActivity2;

public class TodoList {

    private List<TodoItem> todoItems;
    private Filter filter;

    public TodoList() {
        todoItems = new ArrayList<>();
        filter = new Filter();
    }

    public void add(final TodoItem todoItem) {
        todoItems.add(todoItem);
    }

    public void remove(final Long id) {
        todoItems = todoItems.stream().filter(todoItem -> ! Objects.equals(todoItem.getId(), id))
                .collect(Collectors.toList());
    }

    public List<TodoItem> getAllItems() {
        return todoItems;
    }

    public List<TodoItem> getAllItems(final String parentId) {
        return todoItems.stream().filter(todoItem -> todoItem.getParentId().equals(parentId))
                .collect(Collectors.toList());
    }

    public void setAllItems(List<TodoItem> todoItemList) {
        todoItems.addAll(todoItemList);
    }
    public List<TodoItem> getAllFilterItems(ProjectTodoItemActivity2.Status status) {
        List<TodoItem> filteredItems = new ArrayList<>();

        switch (status) {
            case CHECKED:
                for (final TodoItem todoItem : todoItems) {
                    if (todoItem.isChecked()) {
                        filteredItems.add(todoItem);
                    }
                }
                break;
            case UNCHECKED:
                for (final TodoItem todoItem : todoItems) {
                    if (!todoItem.isChecked()) {
                        filteredItems.add(todoItem);
                    }
                }
                break;
            default:
                filteredItems = new ArrayList<>(todoItems);
                break;
        }

        return filteredItems.stream().skip(filter.getSkip()).limit(filter.getLimit()).collect(Collectors.toList());
    }


}
