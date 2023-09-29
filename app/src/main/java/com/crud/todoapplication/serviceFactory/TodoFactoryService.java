package com.crud.todoapplication.serviceFactory;

import com.crud.todoapplication.api.AuthenticationService;
import com.crud.todoapplication.api.ProjectService;
import com.crud.todoapplication.api.TodoItemService;

public class TodoFactoryService {

    private static TodoFactoryService todoFactoryService;

    private TodoFactoryService() {
    }

    public static TodoFactoryService getInstance() {
        return null == todoFactoryService ? todoFactoryService = new TodoFactoryService() : todoFactoryService;
    }

    public AuthenticationService createAuthentication(final String baseUrl) {
        return new AuthenticationService(baseUrl);
    }

    public AuthenticationService createAuthentication(final String baseUrl, final String token) {
        return new AuthenticationService(baseUrl, token);
    }

    public ProjectService createProject(final String baseUrl, final String token) {
        return new ProjectService(baseUrl, token);
    }

    public TodoItemService createTodoItem(final String baseUrl, final String token) {
        return new TodoItemService(baseUrl, token);
    }
}
