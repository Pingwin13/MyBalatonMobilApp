package com.example.mybalaton;

public class HelperClass {
    String email, password, passwordAgain;

    public HelperClass() {
    }

    public HelperClass(String email, String password, String passwordAgain) {
        this.email = email;
        this.password = password;
        this.passwordAgain = passwordAgain;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordAgain() {
        return passwordAgain;
    }

    public void setPasswordAgain(String passwordAgain) {
        this.passwordAgain = passwordAgain;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
