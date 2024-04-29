package com.tesch.db.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Song {
    
    @JsonProperty("Song_id")
    private Long id;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Url")
    private String url;

    @JsonProperty("Genre")
    private Genre genre;

    @JsonProperty("Interpreters")
    private List<Interpreter> interpreters;

    public Song() {
    }

    public Song(Long id, String name, String url, Genre genre, List<Interpreter> interpreters) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.genre = genre;
        this.interpreters = interpreters;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public List<Interpreter> getInterpreters() {
        return interpreters;
    }

    public void setInterpreters(List<Interpreter> interpreters) {
        this.interpreters = interpreters;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        result = prime * result + ((genre == null) ? 0 : genre.hashCode());
        result = prime * result + ((interpreters == null) ? 0 : interpreters.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Song other = (Song) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        if (genre == null) {
            if (other.genre != null)
                return false;
        } else if (!genre.equals(other.genre))
            return false;
        if (interpreters == null) {
            if (other.interpreters != null)
                return false;
        } else if (!interpreters.equals(other.interpreters))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
