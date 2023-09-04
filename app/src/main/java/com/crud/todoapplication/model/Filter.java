package com.crud.todoapplication.model;

import com.crud.todoapplication.ProjectTodoItemActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Filter {

    private String searchAttribute;
    private String sortingAttribute;
    private String filterObjectAttribute;
    private int skip = 0;
    private int limit = 5;

    public String getFilterObjectAttribute() {
        return filterObjectAttribute;
    }

    public void setFilterObjectAttribute(final String filterObjectAttribute) {
        this.filterObjectAttribute = filterObjectAttribute;
    }

    public String getAttribute() {
        return sortingAttribute;
    }

    public void setAttribute(final String sortingAttribute) {
        this.sortingAttribute = sortingAttribute;
    }

    public String getSearchAttribute() {
        return searchAttribute;
    }

    public void setSearchAttribute(final String searchAttribute) {
        this.searchAttribute = searchAttribute;
    }
    public int getSkip() {
        return skip;
    }

    public int getLimit() {
        return limit;
    }
}
