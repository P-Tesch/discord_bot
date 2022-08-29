package com.tesch.api.games;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tesch.api.games.tictactoe.enums.TicTacToeTeams;
import com.tesch.api.games.tictactoe.exceptions.TicTacToeException;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public abstract class Board {

    private Piece[][] board;
    private Map<User, Teams> players;
    private Teams currentPlayer;
    private Teams win;
    private boolean finished;

    protected Piece[][] getBoard() {
        return board;
    }

    protected Map<User, Teams> getPlayers() {
        return players;
    }

   protected Teams getCurrentPlayer() {
        return currentPlayer;
    }

    protected Teams getWin() {
        return win;
    }

    public boolean getFinished() {
        return this.finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    protected Board(Integer boardSize, User[] players) {
        this.board = new Piece[boardSize][boardSize];
        this.players = new HashMap<>();
        this.currentPlayer = Teams.A;
        this.players.put(players[0], Teams.A);
        this.players.put(players[1], Teams.B);
        this.win = null;
        this.finished = false;
    }

    protected void nextPlayer() {
        this.currentPlayer = Teams.getOther(this.currentPlayer);
    }

    protected void checkWin() {
        this.win = checkWinImpl();
    }

    protected abstract Message getBoardAsMessage();
    protected abstract void makeMove(Position position, User player);
    protected abstract Teams checkWinImpl();
}
