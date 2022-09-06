package com.tesch.api.games.chess.pieces;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.tesch.api.games.chess.ChessBoard;
import com.tesch.api.games.chess.ChessPiece;
import com.tesch.api.games.chess.enums.Color;

public class Pawn extends ChessPiece {

    private boolean firstMove;

    public Pawn(Color color, ChessBoard chessBoard) {
        super(color, chessBoard);
        this.firstMove = true;
    }

    public void setFirstMove(boolean firstMove) {
        this.firstMove = firstMove;
    }
    
    public BufferedImage getAsImage() {
        try {
            BufferedImage img = ImageIO.read(new File("src/main/resources/games/chess/pawn_"+ this.getColor().toString() +".png"));
            return img;
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] possibleMoves = new boolean[this.getChessBoard().getBoard().length][this.getChessBoard().getBoard().length];
        possibleMoves[this.getPosition().getRow() + (this.getColor() == Color.BLACK ? 1 : -1)][this.getPosition().getColumn()] = true;
        if (this.firstMove) possibleMoves[this.getPosition().getRow() + (this.getColor() == Color.BLACK ? 2 : -2)][this.getPosition().getColumn()] = true;
        return possibleMoves;
    }
}
