package com.tesch.api.games.chess.pieces;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.tesch.api.games.Position;
import com.tesch.api.games.chess.ChessBoard;
import com.tesch.api.games.chess.ChessPiece;
import com.tesch.api.games.chess.enums.Color;

public class Knight extends ChessPiece {

    public Knight(Color color, ChessBoard chessBoard) {
        super(color, chessBoard);
    }
    
    public BufferedImage getAsImage() {
        try {
            BufferedImage img = ImageIO.read(new File("src/main/resources/games/chess/knight_"+ this.getColor().toString() +".png"));
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

        for (int i = -2; i <= 2; i += 4) {
            for (int j = -1; j <= 1; j += 2) {
                Position targetPosition = new Position(this.getPosition().getRow() + i, this.getPosition().getColumn() + j);
                if (this.getChessBoard().positionExists(targetPosition)) {
                    possibleMoves[targetPosition.getRow()][targetPosition.getColumn()] = this.canMove(targetPosition);
                }

                targetPosition.setRow(this.getPosition().getRow() + j);
                targetPosition.setColumn(this.getPosition().getColumn() + i);
                if (this.getChessBoard().positionExists(targetPosition)) {
                    possibleMoves[targetPosition.getRow()][targetPosition.getColumn()] = this.canMove(targetPosition);
                }
            }
        }
				
		return possibleMoves;
    }

    
}
