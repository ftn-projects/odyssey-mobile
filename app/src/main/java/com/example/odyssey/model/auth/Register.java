package com.example.odyssey.model.auth;

import com.example.odyssey.model.Address;
import com.example.odyssey.model.users.User;

public class Register extends User {
    private String password;
    private String role;

    public Register(Long id, String email, String name, String surname, String phone, Address address, Settings settings,
                    String bio, String password, String role) {
        super(id, email, name, surname, phone, address, settings, bio);
        this.password = password;
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
