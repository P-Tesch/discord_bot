package com.tesch.api.games.tictactoe;

import com.tesch.api.games.tictactoe.enums.TicTacToeTeams;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class TicTacToeBoard {

    private TicTacToeTeams[][] board;
    
    public TicTacToeBoard() {
        board = new TicTacToeTeams[3][3];
        for (int i = 0; i < this.board.length; i++) {
            for (int j = 0; j < this.board[i].length; j++) {
                this.board[i][j] = TicTacToeTeams.NULL;
            }
        }
    }

    protected boolean makeMove(Position position, User player) {
        if (this.board[position.getRow()][position.getColumn()] == TicTacToeTeams.NULL) {
            //this.board[position.getRow()][position.getColumn()] = 
        }
        return false;
    }

    protected Message getBoardAsMessage() {
        MessageBuilder message = new MessageBuilder();
        message.setContent("Board: ");
        ActionRow row1 = ActionRow.of(Button.primary("0 0", board[0][0].toString()), Button.primary("0 1", board[0][1].toString()), Button.primary("0 2", board[0][2].toString()));
        ActionRow row2 = ActionRow.of(Button.primary("1 0", board[1][0].toString()), Button.primary("1 1", board[1][1].toString()), Button.primary("1 2", board[1][2].toString()));
        ActionRow row3 = ActionRow.of(Button.primary("2 0", board[2][0].toString()), Button.primary("2 1", board[2][1].toString()), Button.primary("2 2", board[2][2].toString()));
        message.setActionRows(row1, row2, row3);
        return message.build();
    }

}
