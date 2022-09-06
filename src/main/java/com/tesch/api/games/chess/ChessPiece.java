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
    
    public ChessPiece(Color color, ChessBoard chessBoard) {
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
        this.setPosition(chessPosition.getRow(), chessPosition.getColumn());
    }

    @Override
    public void setPosition(Position position) {
        this.setPosition(position);
        this.chessPosition.setAllFromPosition(position);
    }

    public Color getColor() {
        return this.color;
    }

    public void MakeMove(ChessPosition chessPosition) {
        if (!this.getChessBoard().positionExists(chessPosition)) throw new ChessException("Position does not exist");
        if (!this.possibleMoves()[chessPosition.getRow()][chessPosition.getColumn()]) throw new ChessException("Move not possible");
        this.getChessBoard().getBoard()[this.getPosition().getRow()][this.getPosition().getColumn()] = null;
        this.setChessPosition(chessPosition);
        this.getChessBoard().getBoard()[this.getPosition().getRow()][this.getPosition().getColumn()] = this;
        if (this instanceof Pawn) {
            ((Pawn) this).setFirstMove(false);
        }
    }

    public abstract BufferedImage getAsImage();
    public abstract boolean[][] possibleMoves();
}
