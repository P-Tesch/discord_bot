package com.tesch.api.games.chess.pieces;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.tesch.api.games.Position;
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

        int nextRow = this.getChessPosition().getRow() + (this.getColor() == Color.BLACK ? 1 : -1);
        int secondRow = this.getChessPosition().getRow() + (this.getColor() == Color.BLACK ? 2 : -2);
        int nextColumn = this.getChessPosition().getColumn() + 1;
        int previousColumn = this.getChessPosition().getColumn() - 1;

        // Normal moves
        if (this.getChessBoard().positionExists(new Position(nextRow, this.getChessPosition().getColumn()))) {
            ChessPiece target = (ChessPiece) this.getChessBoard().getBoard()[nextRow][this.getChessPosition().getColumn()];
            possibleMoves[nextRow][this.getChessPosition().getColumn()] = target == null;
        }
        if (this.firstMove && this.getChessBoard().positionExists(new Position(secondRow, this.getChessPosition().getColumn()))) {
            ChessPiece target = (ChessPiece) this.getChessBoard().getBoard()[secondRow][this.getChessPosition().getColumn()];
            possibleMoves[secondRow][this.getPosition().getColumn()] = target == null;
        }

        // Capture
        if (this.getChessBoard().positionExists(new Position(nextRow, nextColumn))) {
            ChessPiece target = ((ChessPiece) this.getChessBoard().getBoard()[nextRow][nextColumn]);
            possibleMoves[nextRow][nextColumn] = target != null && target.getColor() != this.getColor();
        }
        if (this.getChessBoard().positionExists(new Position(nextRow, previousColumn))) {
            ChessPiece target = (ChessPiece) this.getChessBoard().getBoard()[nextRow][previousColumn];
            possibleMoves[nextRow][previousColumn] = target != null && target.getColor() != this.getColor();
        }

        return possibleMoves;
    }
}
