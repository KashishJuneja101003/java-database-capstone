package com.project.back_end.DTO;

public class Login {

    private String identifier; // email (Doctor/Patient) or username (Admin)
    private String password;

    public Login() {
        // Default constructor required for JSON deserialization
    }

    public Login(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
