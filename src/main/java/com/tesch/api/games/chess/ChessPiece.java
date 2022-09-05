package com.tesch.api.games.chess;

import java.awt.image.BufferedImage;

import com.tesch.api.games.Piece;
import com.tesch.api.games.Position;
import com.tesch.api.games.chess.enums.Color;

public abstract class ChessPiece extends Piece {

    private Color color;
    private ChessPosition chessPosition;
    
    public ChessPiece(Color color) {
        super(color.getTeam());
        this.color = color;
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

    public abstract BufferedImage getAsImage();
}
