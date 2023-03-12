package com.tesch.games;

public abstract class Piece {
    
    private Teams owner;
    private Position position;

    public Piece(Teams owner) {
        this.owner = owner;
    }

    public Teams getOwner() {
        return this.owner;
    }

    public Position getPosition() {
        return this.position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setPosition(int x, int y) {
        this.position = new Position(x, y);
    }
}
