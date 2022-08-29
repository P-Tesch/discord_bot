package com.tesch.api.games.tictactoe;

import com.tesch.api.games.Piece;
import com.tesch.api.games.Teams;

public class TicTacToePiece extends Piece{

    public TicTacToePiece(Teams owner) {
        super(owner);
    }
    
    @Override
    public String toString() {
        return this.getOwner() == Teams.A ? "X" : "O";
    }
}
