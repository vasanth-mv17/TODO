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

    public boolean add(final TodoItem todoItem) {
        return todoItems.add(todoItem);
    }

    public void remove(final String id) {
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
}
