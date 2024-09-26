package com.crud.todoapplication.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TodoList {

//    private List<TodoItem> todoItemList;
//    private String parentId;
//
//    public void add(final TodoItem todoItem) {
//        todoItemList.add(todoItem);
//    }
//
//    public void remove(final String id) {
//        todoItemList = todoItemList.stream().filter(todoItem -> !Objects.equals(todoItem.getId(), id)).collect(Collectors.toList());
//
//    }
//
//    public String getParentId() {
//        return parentId;
//    }
//
//    public void setParentId(String parentId) {
//        this.parentId = parentId;
//    }
//
//    public List<TodoItem> getAll() {
//        return todoItemList;
//    }
//
//    public List getAllList(final Long parentId) {
//        return  todoItemList.stream().filter(todoItem -> getParentId().equals(parentId)).collect(Collectors.toList());
//    }
private List<TodoItem> todoItems;

    public TodoList() {
        todoItems = new ArrayList<>();
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

    public List<TodoItem> getAllItems(final Long parentId) {
        return todoItems.stream().filter(todoItem -> todoItem.getParentId().equals(parentId))
                .collect(Collectors.toList());
    }
}
