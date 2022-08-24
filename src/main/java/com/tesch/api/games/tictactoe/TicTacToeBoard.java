package com.tesch.api.games.tictactoe;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.tesch.api.games.tictactoe.enums.TicTacToeTeams;
import com.tesch.api.games.tictactoe.exceptions.TicTacToeException;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class TicTacToeBoard {

    private TicTacToeTeams[][] board;
    private Map<User, TicTacToeTeams> players;
    private TicTacToeTeams currentPlayer;
    private TicTacToeTeams win;
    
    public TicTacToeBoard(User[] players) {
        board = new TicTacToeTeams[3][3];
        for (int i = 0; i < this.board.length; i++) {
            for (int j = 0; j < this.board[i].length; j++) {
                this.board[i][j] = TicTacToeTeams.NULL;
            }
        }
        this.players = new HashMap<>();
        this.currentPlayer = TicTacToeTeams.X;
        this.players.put(players[0], TicTacToeTeams.X);
        this.players.put(players[1], TicTacToeTeams.O);
        this.win = TicTacToeTeams.NULL;
    }

    protected TicTacToeTeams getWin() {
        return this.win;
    }

    private void nextPlayer() {
        this.currentPlayer = this.currentPlayer == TicTacToeTeams.X ? TicTacToeTeams.O : TicTacToeTeams.X;
    }

    protected void makeMove(Position position, User player) {
        if (this.board[position.getRow()][position.getColumn()] != TicTacToeTeams.NULL) {
            throw new TicTacToeException("The selected place is not empty");
        }
        if (this.currentPlayer != this.players.get(player)) {
            throw new TicTacToeException("This is not the current player");
        }
        this.board[position.getRow()][position.getColumn()] = this.currentPlayer;
        this.win = this.checkWin();
        this.nextPlayer();
    }

    protected Message getBoardAsMessage() {
        MessageBuilder message = new MessageBuilder();
        this.players.keySet().forEach(x -> message.mentionUsers(x.getIdLong()));
        if (this.win != TicTacToeTeams.NULL) {
            message.setContent("Winner: ");
            message.append(this.players.entrySet().stream().filter(x -> x.getValue() == this.win).toList().get(0).getKey().getAsMention());
        }
        else {
            message.setContent("Player: ");
            message.append(this.players.entrySet().stream().filter(x -> x.getValue() == this.currentPlayer).toList().get(0).getKey().getAsMention());
        }
        ActionRow row1 = ActionRow.of(Button.primary("0 0", board[0][0].toString()), Button.primary("0 1", board[0][1].toString()), Button.primary("0 2", board[0][2].toString()));
        ActionRow row2 = ActionRow.of(Button.primary("1 0", board[1][0].toString()), Button.primary("1 1", board[1][1].toString()), Button.primary("1 2", board[1][2].toString()));
        ActionRow row3 = ActionRow.of(Button.primary("2 0", board[2][0].toString()), Button.primary("2 1", board[2][1].toString()), Button.primary("2 2", board[2][2].toString()));
        message.setActionRows(row1, row2, row3);
        return message.build();
    }

    private TicTacToeTeams checkWin() {
        TicTacToeTeams[] column = new TicTacToeTeams[this.board.length];
		TicTacToeTeams[] diagonal1 = new TicTacToeTeams[this.board.length];
		TicTacToeTeams[] diagonal2 = new TicTacToeTeams[this.board.length];
		
		for (int i = 0; i < this.board.length; i++) {
			// Rows
			if (Arrays.stream(this.board[i]).allMatch(p -> p == TicTacToeTeams.X)) {
				return TicTacToeTeams.X;
			}
			if (Arrays.stream(this.board[i]).allMatch(p -> p == TicTacToeTeams.O)) {
				return TicTacToeTeams.O;
			}
			
			// Columns
			for (int j = 0; j < this.board.length; j++) {
				column[j] = this.board[j][i];
				if (Arrays.stream(column).allMatch(p -> p == TicTacToeTeams.X)) {
					return TicTacToeTeams.X;
				}
				if (Arrays.stream(column).allMatch(p -> p == TicTacToeTeams.O)) {
					return TicTacToeTeams.O;
				}
			}
			
			// Diagonals
			diagonal1[i] = this.board[i][i];
			if (Arrays.stream(diagonal1).allMatch(p -> p == TicTacToeTeams.X)) {
				return TicTacToeTeams.X;
			}
			if (Arrays.stream(diagonal1).allMatch(p -> p == TicTacToeTeams.O)) {
				return TicTacToeTeams.O;
			}
			diagonal2[i] = this.board[this.board.length - 1 - i][i];
			if (Arrays.stream(diagonal2).allMatch(p -> p == TicTacToeTeams.X)) {
				return TicTacToeTeams.X;
			}
			if (Arrays.stream(diagonal2).allMatch(p -> p == TicTacToeTeams.O)) {
				return TicTacToeTeams.O;
			}
			
		}
		return TicTacToeTeams.NULL;
    }
}
