package com.tesch.db.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Genre {
    
    @JsonProperty("Genre_id")
    private Long id;

    @JsonProperty("Name")
    private String name;

    public Genre() {
    }

    public Genre(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    } 

    @Override
    public String toString() {
        return this.name;
    }
}
