package com.ntnu.idatt2105.model;

/**
 * Class to represent the login details for an attempted login
 */
public class UserDetails {
    private String email;
    private String password;

    public UserDetails(){}

    public UserDetails(String email, String password){
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
