package com.crud.todoapplication.model;

public class Credentials {

    private Long id;
    private String email;
    private String password;
    private String hint;
    private String conformPassword;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(final String hint) {
        this.hint = hint;
    }

    public String getConformPassword() {
        return conformPassword;
    }

    public void setConformPassword(final String conformPassword) {
        this.conformPassword = conformPassword;
    }
}
