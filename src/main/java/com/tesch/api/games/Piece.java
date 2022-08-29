package com.tesch.api.games;

public abstract class Piece {
    
    private Teams owner;

    public Piece(Teams owner) {
        this.owner = owner;
    }

    public Teams getOwner() {
        return this.owner;
    }
}
