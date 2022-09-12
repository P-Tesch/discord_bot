package com.tesch.api.games.chess;

import java.awt.image.BufferedImage;

import com.tesch.api.games.Piece;
import com.tesch.api.games.Position;
import com.tesch.api.games.chess.enums.Color;
import com.tesch.api.games.chess.exceptions.ChessException;
import com.tesch.api.games.chess.pieces.Pawn;

public abstract class ChessPiece extends Piece {

    private Color color;
    private ChessPosition chessPosition;
    private ChessBoard chessBoard;
    
    protected ChessPiece(Color color, ChessBoard chessBoard) {
        super(color.getTeam());
        this.color = color;
        this.chessBoard = chessBoard;
    }

    public ChessBoard getChessBoard() {
        return this.chessBoard;
    }

    public ChessPosition getChessPosition() {
        return this.chessPosition;
    }

    public void setChessPosition(ChessPosition chessPosition) {
        this.chessPosition = chessPosition;
        super.setPosition(chessPosition.getRow(), chessPosition.getColumn());
    }

    @Override
    public void setPosition(Position position) {
        super.setPosition(position);
        if (this.chessPosition == null) {
            this.chessPosition = new ChessPosition(position);
        }
        else {
            this.chessPosition.setAllFromPosition(position);
        }
    }

    public Color getColor() {
        return this.color;
    }

    protected boolean canMove(Position position) {
        if (!this.getChessBoard().positionExists(position)) throw new IllegalArgumentException("Position does not exist");

        ChessPiece target = this.getChessBoard().getPieceAt(position);
        return target == null || this.isThereOpponentPiece(position);
    }

    protected boolean isThereOpponentPiece(Position position) {
        if (!this.getChessBoard().positionExists(position)) throw new IllegalArgumentException("Position does not exist");

        ChessPiece target = this.getChessBoard().getPieceAt(position);
        return target != null && target.getColor() != this.getColor();
    }

    public void MakeMove(Position position) {
        if (!this.getChessBoard().positionExists(position)) throw new ChessException("Position does not exist");
        if (!this.possibleMoves()[position.getRow()][position.getColumn()]) throw new ChessException("Move not possible");

        this.getChessBoard().makeMove(this.getPosition(), position);
        if (this instanceof Pawn) {
            ((Pawn) this).setFirstMove(false);
        }
    }

    public abstract BufferedImage getAsImage();
    public abstract boolean[][] possibleMoves();
}
