package com.tesch.api.games.tictactoe.enums;

public enum TicTacToeTeams {
    NULL (' '),
    X ('X'),
    O ('O');

    Character print;

    private TicTacToeTeams(Character print) {
        this.print = print;
    }

    public String toString() {
        return print.toString();
    }
}
