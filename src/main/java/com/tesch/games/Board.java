package com.tesch.games;

import java.util.HashMap;
import java.util.Map;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public abstract class Board {

    private Piece[][] board;
    private Map<User, Teams> players;
    private Teams currentPlayer;

    protected Board(Integer boardSize, User[] players) {
        this.board = new Piece[boardSize][boardSize];
        this.players = new HashMap<>();
        this.currentPlayer = Teams.A;
        this.players.put(players[0], Teams.A);
        this.players.put(players[1], Teams.B);
    }

    public Piece[][] getBoard() {
        return board;
    }

    protected Map<User, Teams> getPlayers() {
        return players;
    }

    protected Teams getCurrentPlayer() {
        return currentPlayer;
    }

    protected void nextPlayer() {
        this.currentPlayer = Teams.getOther(this.currentPlayer);
    }

    public abstract MessageCreateData getBoardAsMessageCreateData();
}
