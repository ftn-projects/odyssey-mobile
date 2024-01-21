package com.example.odyssey.model.accommodations;

import java.io.Serializable;

public class Amenity implements Serializable {
    private Long id;
    private String title;

    public Amenity(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
