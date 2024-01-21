package com.example.odyssey.model.auth;

import com.example.odyssey.model.Address;
import com.example.odyssey.model.users.User;

public class Register extends User {
    private String password;

    public Register(Long id, String email, String name, String surname, String phone, String role, Address address, Settings settings,
                    String bio, String password) {
        super(id, email, name, surname, phone, role, address, settings, bio);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
