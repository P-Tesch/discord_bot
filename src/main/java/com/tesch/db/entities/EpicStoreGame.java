package com.tesch.db.entities;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EpicStoreGame {
    
    @JsonProperty("title")
    private String title;

    @JsonProperty("startDate")
    private LocalDate freeStartDate;

    @JsonProperty("endDate")
    private LocalDate freeEndDate;

    public EpicStoreGame() {
    }

    public EpicStoreGame(String title, LocalDate freeStartDate, LocalDate freeEndDate) {
        this.title = title;
        this.freeStartDate = freeStartDate;
        this.freeEndDate = freeEndDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getFreeStartDate() {
        return freeStartDate;
    }

    public void setFreeStartDate(LocalDate freeStartDate) {
        this.freeStartDate = freeStartDate;
    }

    public LocalDate getFreeEndDate() {
        return freeEndDate;
    }

    public void setFreeEndDate(LocalDate freeEndDate) {
        this.freeEndDate = freeEndDate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((title == null) ? 0 : title.hashCode());
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
        EpicStoreGame other = (EpicStoreGame) obj;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        return true;
    }
}
