package com.crud.todoapplication.model;

public class SignUp {

    private String name;
    private String email;
    private String title;
    private String password;
    private String hint;

    public SignUp(final User user, final Credentials userCredentials) {
        this.name = user.getName();
        this.title = user.getTitle();
        this.hint = userCredentials.getHint();
        this.email = userCredentials.getEmail();
        this.password = userCredentials.getPassword();
    }
}
