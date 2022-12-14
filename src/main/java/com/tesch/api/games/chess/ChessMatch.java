package com.tesch.api.games.chess;

import java.util.HashMap;
import java.util.Map;

import com.tesch.api.games.Position;
import com.tesch.api.games.chess.enums.Color;
import com.tesch.api.games.chess.exceptions.ChessException;

import net.dv8tion.jda.api.entities.User;

public class ChessMatch {
    
    private ChessManager manager;
    private Map<Color, User> players;
    private ChessBoard chessBoard;
    private ChessPiece selectedPiece;
    private Color currentPlayer;
    private Color winner;
    private Integer turn;
    
    public ChessMatch(User[] players, ChessManager manager) {
        this.manager = manager;
        this.players = new HashMap<>();
        this.players.put(Color.WHITE, players[0]);
        this.players.put(Color.BLACK, players[1]);
        this.chessBoard = new ChessBoard(8, this.getPlayers(), this);
        this.selectedPiece = null;
        this.currentPlayer = Color.WHITE;
        this.winner = null;
        this.turn = 0;
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

    public void selectPiece(ChessPosition chessPosition, User player) {
        if (!player.equals(this.players.get(this.currentPlayer))) throw new ChessException("You are not the current player");

        ChessPiece piece = (ChessPiece) this.getChessBoard().getBoard()[chessPosition.getRow()][chessPosition.getColumn()];

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
            this.nextPlayer();
            this.turn++;
        }
    }

    public void cancelMove(User player) {
        if (!player.equals(this.players.get(this.currentPlayer))) throw new ChessException("You are not the current player");
        this.selectedPiece = null;
    }

    private void nextPlayer() {
        this.currentPlayer = this.currentPlayer == Color.WHITE ? Color.BLACK : Color.WHITE;
    }
}
