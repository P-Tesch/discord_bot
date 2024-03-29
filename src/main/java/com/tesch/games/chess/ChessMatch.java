package com.tesch.games.chess;

import java.util.HashMap;
import java.util.Map;

import com.tesch.exceptions.ChessException;
import com.tesch.games.Position;
import com.tesch.games.chess.enums.Color;
import com.tesch.games.chess.pieces.Bishop;
import com.tesch.games.chess.pieces.Knight;
import com.tesch.games.chess.pieces.Queen;
import com.tesch.games.chess.pieces.Rook;
import com.tesch.managers.ChessManager;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class ChessMatch {
    
    private ChessManager manager;
    private Map<Color, User> players;
    private ChessBoard chessBoard;
    private ChessPiece selectedPiece;
    private Color currentPlayer;
    private Color winner;
    private Integer turn;
    private TextChannel channel;
    private ChessPiece toPromote;
    
    public ChessMatch(User[] players, ChessManager manager, TextChannel channel) {
        this.manager = manager;
        this.players = new HashMap<>();
        this.players.put(Color.WHITE, players[0]);
        this.players.put(Color.BLACK, players[1]);
        this.chessBoard = new ChessBoard(8, this.getPlayers(), this);
        this.selectedPiece = null;
        this.currentPlayer = Color.WHITE;
        this.winner = null;
        this.turn = 0;
        this.channel = channel;
        this.toPromote = null;
    }

    public User[] getPlayers() {
        User[] playerArray = new User[2];
        playerArray[0] = this.players.get(Color.WHITE);
        playerArray[1] = this.players.get(Color.BLACK);
        return playerArray;
    }

    public ChessBoard getChessBoard() {
        return this.chessBoard;
    }

    public ChessPiece getSelectedPiece() {
        return this.selectedPiece;
    }

    public String getCurrentPlayerAsMention() {
        return this.players.get(this.currentPlayer).getAsMention();
    }

    public Color getWinner() {
        return this.winner;
    }

    public Integer getTurn() {
        return this.turn;
    }

    public boolean isPromoting() {
        return this.toPromote != null;
    }

    public void selectPiece(ChessPosition chessPosition, User player) {
        if (!player.equals(this.players.get(this.currentPlayer))) throw new ChessException("You are not the current player");

        ChessPiece piece = (ChessPiece) this.getChessBoard().getBoard()[chessPosition.getRow()][chessPosition.getColumn()];

        if (piece == null) throw new ChessException("There is no piece in position " + chessPosition);
        if (piece.getColor() != this.currentPlayer) throw new ChessException("This piece is not yours");
        
        this.selectedPiece = piece;
    }

    public void moveSelectedPiece(ChessPosition chessPosition, User player) {
        if (!player.equals(this.players.get(this.currentPlayer))) throw new ChessException("You are not the current player");
        if (!this.chessBoard.positionExists(new Position(chessPosition.getRow(), chessPosition.getColumn()))) throw new ChessException("Position doesn't exist");
        if (!this.selectedPiece.possibleMoves()[chessPosition.getRow()][chessPosition.getColumn()]) throw new ChessException("Move not possible");

        this.selectedPiece.makeMove(chessPosition);
        this.selectedPiece = null;
        if (this.chessBoard.isCheckMate(this.currentPlayer == Color.BLACK ? Color.WHITE : Color.BLACK)) {
            this.winner = this.currentPlayer;
            this.manager.endMatch(this);
        }
        else {
            if (!this.isPromoting()) {
                this.nextPlayer();
                this.turn++;
            }
        }
    }

    public void cancelMove(User player) {
        if (!player.equals(this.players.get(this.currentPlayer))) throw new ChessException("You are not the current player");
        this.selectedPiece = null;
    }

    public void promotionSelect(ChessPiece toPromote) {
        this.toPromote = toPromote;
        this.channel.sendMessage("Select promotion").setActionRow(
            Button.primary("chess_queen", "♕"),
            Button.primary("chess_rook", "♖"),
            Button.primary("chess_bishop", "♗"),
            Button.primary("chess_knight", "♘")
            )
        .queue();
    }

    public void onChessButton(ButtonInteractionEvent event) {
        if (event.getUser() == this.players.get(this.currentPlayer)) {
            switch(event.getButton().getId()) {
                case("chess_queen"):
                    this.chessBoard.promote(this.toPromote, new Queen(this.currentPlayer, this.chessBoard));
                    break;
                case("chess_rook"):
                    this.chessBoard.promote(this.toPromote, new Rook(this.currentPlayer, this.chessBoard));
                    break;
                case("chess_bishop"):
                    this.chessBoard.promote(this.toPromote, new Bishop(this.currentPlayer, this.chessBoard));
                    break;
                case("chess_knight"):
                    this.chessBoard.promote(this.toPromote, new Knight(this.currentPlayer, this.chessBoard));
                    break;
            }
            this.toPromote = null;
            this.nextPlayer();
            this.turn++;
            event.getMessage().delete().queue();
        }
    }

    private void nextPlayer() {
        this.currentPlayer = this.currentPlayer == Color.WHITE ? Color.BLACK : Color.WHITE;
    }
}
