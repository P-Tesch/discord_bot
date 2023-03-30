package com.tesch.games.tictactoe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.tesch.exceptions.TicTacToeException;
import com.tesch.games.Board;
import com.tesch.games.Piece;
import com.tesch.games.Position;
import com.tesch.games.Teams;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class TicTacToeBoard extends Board {

    private List<String> winLine;
    private boolean finished;
    private Teams win;
    
    public TicTacToeBoard(User[] players) {
        super(3, players);
        this.winLine = new ArrayList<>();
    }

    public void makeMove(Position position, User player) {
        if (this.getBoard()[position.getRow()][position.getColumn()] != null) {
            throw new TicTacToeException("The selected place is not empty");
        }
        if (this.getCurrentPlayer() != this.getPlayers().get(player)) {
            throw new TicTacToeException("This is not the current player");
        }
        this.getBoard()[position.getRow()][position.getColumn()] = new TicTacToePiece(this.getCurrentPlayer());
        this.checkWin();
        if (this.getWin() != null) this.setFinished(true);
        this.nextPlayer();
    }

    public boolean getFinished() {
        return this.finished;
    }

    public Teams getWin() {
        return this.win;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    @Override
    public MessageCreateData getBoardAsMessageCreateData() {
        List<Button> buttons = new ArrayList<>();
        for (int i = 0; i < this.getBoard().length; i++) {
            for (int j = 0; j < this.getBoard().length; j++) {
                if (winLine.contains(i + " " + j)) {
                    buttons.add(Button.success("tictactoe_" + i + " " + j, this.getBoard()[i][j] != null ? this.getBoard()[i][j].toString() : "‎")); 
                }
                else {
                    buttons.add(Button.primary("tictactoe_" + i + " " + j, this.getBoard()[i][j] != null ? this.getBoard()[i][j].toString() : "‎"));
                }
            }
        }
        MessageCreateBuilder message = new MessageCreateBuilder();
        this.getPlayers().keySet().forEach(x -> message.mentionUsers(x.getIdLong()));
        if (this.getWin() != null) {
            message.setContent("Winner: ");
            message.addContent(this.getPlayers().entrySet().stream().filter(x -> x.getValue() == this.getWin()).toList().get(0).getKey().getAsMention());
        }
        else {
            message.setContent("Player: ");
            message.addContent(this.getPlayers().entrySet().stream().filter(x -> x.getValue() == this.getCurrentPlayer()).toList().get(0).getKey().getAsMention());
        }
        
        message.setComponents(
            ActionRow.of(buttons.stream().filter(x -> Integer.parseInt(x.getId().split("_")[1].split(" ")[0]) == 0).toList()),
            ActionRow.of(buttons.stream().filter(x -> Integer.parseInt(x.getId().split("_")[1].split(" ")[0]) == 1).toList()),
            ActionRow.of(buttons.stream().filter(x -> Integer.parseInt(x.getId().split("_")[1].split(" ")[0]) == 2).toList())
        );
        return message.build();
    }

    protected void checkWin() {
        Piece[] column;
		Piece[] diagonal1 = new Piece[this.getBoard().length];
		Piece[] diagonal2 = new Piece[this.getBoard().length];
		
		for (int i = 0; i < this.getBoard().length; i++) {
			// Rows
			if (Arrays.stream(this.getBoard()[i]).allMatch(p -> p != null && p.getOwner() == this.getCurrentPlayer())) {
                for (int l = 0; l < this.getBoard().length; l++) {
                    this.winLine.add(i + " " + l);
                }
				this.win = this.getCurrentPlayer();
            }
			
			// Columns
            column = new Piece[this.getBoard().length];
			for (int j = 0; j < this.getBoard().length; j++) {
				column[j] = this.getBoard()[j][i];
				if (Arrays.stream(column).allMatch(p -> p != null && p.getOwner() == this.getCurrentPlayer())) {
                    for (int l = 0; l < this.getBoard().length; l++) {
                        this.winLine.add(l + " " + i);
                    }
					this.win = this.getCurrentPlayer();
				}
			}
			
			// Diagonals
			diagonal1[i] = this.getBoard()[i][i];
			if (Arrays.stream(diagonal1).allMatch(p -> p != null && p.getOwner() == this.getCurrentPlayer())) {
                for (int l = 0; l < this.getBoard().length; l++) {
                    this.winLine.add(l + " " + l);
                }
				this.win = this.getCurrentPlayer();
			}
			
			diagonal2[i] = this.getBoard()[this.getBoard().length - 1 - i][i];
			if (Arrays.stream(diagonal2).allMatch(p -> p != null && p.getOwner() == this.getCurrentPlayer())) {
                for (int l = 0; l < this.getBoard().length; l++) {
                    this.winLine.add(this.getBoard().length - 1 - l + " " + l);
                }
				this.win = this.getCurrentPlayer();
			}
		}
		this.win = null;
    }
}
