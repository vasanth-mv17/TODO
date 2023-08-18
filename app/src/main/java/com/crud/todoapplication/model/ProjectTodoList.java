package com.crud.todoapplication.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProjectTodoList {

    private List<TodoItem> todoItemList;
    private String parentId;

    public ProjectTodoList(final String parentId) {
        this.parentId = parentId;
        this.todoItemList = new ArrayList<>();
    }
    public String getParentId() {
        return parentId;
    }

    public void setParentId(final String parentId) {
        this.parentId = parentId;
    }

    public void add(final TodoItem todoItem) {
        todoItemList.add(todoItem);
    }
    public void clear() {
        todoItemList.clear();
    }

    public void remove(final String id) {
        todoItemList = todoItemList.stream().filter(todoItem -> !Objects.equals(todoItem.getId(), id)).collect(Collectors.toList());
    }

    public List<TodoItem> getAll() {
        return todoItemList;
    }

    public List getAllList(final Long parentId) {
        return  todoItemList.stream().filter(todoItem -> getParentId().equals(parentId)).collect(Collectors.toList());
    }
}
