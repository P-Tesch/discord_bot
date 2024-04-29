package com.tesch.db.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Botuser {
    
    @JsonProperty("Botuser_id")
    private Long id;

    @JsonProperty("Discord_id")
    private long discordId;

    @JsonProperty("Currency")
    private Integer currency;

    @JsonProperty("Score")
    private Score score;

    @JsonProperty("Items")
    private List<Item> items;

    public Botuser() {
    }

    public Botuser(Long id, long discordId, Integer currency, Score score, List<Item> items) {
        this.id = id;
        this.discordId = discordId;
        this.currency = currency;
        this.score = score;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getDiscordId() {
        return discordId;
    }

    public void setDiscordId(long discordId) {
        this.discordId = discordId;
    }

    public Integer getCurrency() {
        return currency;
    }

    public void setCurrency(Integer currency) {
        this.currency = currency;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + (int) (discordId ^ (discordId >>> 32));
        result = prime * result + ((currency == null) ? 0 : currency.hashCode());
        result = prime * result + ((score == null) ? 0 : score.hashCode());
        result = prime * result + ((items == null) ? 0 : items.hashCode());
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
        Botuser other = (Botuser) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (discordId != other.discordId)
            return false;
        if (currency == null) {
            if (other.currency != null)
                return false;
        } else if (!currency.equals(other.currency))
            return false;
        if (score == null) {
            if (other.score != null)
                return false;
        } else if (!score.equals(other.score))
            return false;
        if (items == null) {
            if (other.items != null)
                return false;
        } else if (!items.equals(other.items))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Id = " + this.id + " Discord id = " + this.discordId;
    }
}
