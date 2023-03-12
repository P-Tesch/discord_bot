package com.tesch.games.tictactoe;

import com.tesch.games.Piece;
import com.tesch.games.Teams;

public class TicTacToePiece extends Piece{

    public TicTacToePiece(Teams owner) {
        super(owner);
    }
    
    @Override
    public String toString() {
        return this.getOwner() == Teams.A ? "X" : "O";
    }
}
