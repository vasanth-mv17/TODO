package com.crud.todoapplication.model;

import java.util.List;

public class Filter {

    private String search;
    private String searchAttribute;
    private String attribute;
    private Type type;
    private String filterObjectAttribute;
    private List<Object> values;
    private int skip;
    private int limit;

    public enum Type {
        ASC,
        DESC

    }

    public String getFilterObjectAttribute() {
        return filterObjectAttribute;
    }

    public void setFilterObjectAttribute(String filterObjectAttribute) {
        this.filterObjectAttribute = filterObjectAttribute;
    }

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(final String attribute) {
        this.attribute = attribute;
    }

    public Type getType() {
        return type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public int getSkip() {
        return skip;
    }

    public void setSkip(final int skip) {
        this.skip = skip;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(final int limit) {
        this.limit = limit;
    }

    public String getSearchAttribute() {
        return searchAttribute;
    }

    public void setSearchAttribute(final String searchAttribute) {
        this.searchAttribute = searchAttribute;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(final String search) {
        this.search = search;
    }
}
