package com.example.archivault.model;

public class Users {

    private String user_email;
    private String user_password;
    private String user_token;
    private String token_type;


    public Users(String user_email, String user_password, String user_token, String token_type) {
        this.user_email = user_email;
        this.user_password = user_password;
        this.user_token = user_token;
        this.token_type = token_type;
    }

    public String getUser_email() {
        return user_email;
    }

    public String getUser_password() {
        return user_password;
    }

    public String getUser_token() {
        return user_token;
    }

    public String getToken_type() {
        return token_type;
    }
}
