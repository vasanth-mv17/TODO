package com.crud.todoapplication.model;

public class User {

    private Long id;
    private String name;
    private String title;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public StringBuilder setProfileIcon() {
        final String[] nameWords = name.split(" ");
        final StringBuilder profileText = new StringBuilder();

        for (final String word : nameWords) {
            if (!word.isEmpty()) {
                profileText.append(Character.toUpperCase(word.charAt(0)));
            }
        }
        return profileText;
    }
}
