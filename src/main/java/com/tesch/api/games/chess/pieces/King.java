package com.tesch.api.games.chess.pieces;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.tesch.api.games.Position;
import com.tesch.api.games.chess.ChessBoard;
import com.tesch.api.games.chess.ChessPiece;
import com.tesch.api.games.chess.enums.Color;

public class King extends ChessPiece {

    public King(Color color, ChessBoard chessBoard) {
        super(color, chessBoard);
    }
    
    public BufferedImage getAsImage() {
        try {
            BufferedImage img = ImageIO.read(new File("src/main/resources/games/chess/king_"+ this.getColor().toString() +".png"));
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
		
		// Normal moves
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (this.getChessBoard().positionExists(new Position((this.getChessPosition().getRow() + i), (this.getChessPosition().getColumn() + j)))) {
                    ChessPiece target = (ChessPiece) this.getChessBoard().getBoard()[this.getChessPosition().getRow() + i][this.getChessPosition().getColumn() + j];
					possibleMoves[this.getChessPosition().getRow() + i][this.getChessPosition().getColumn() + j] = target == null || target.getColor() != this.getColor();
				}
			}
		}

        return possibleMoves;
    }

    
    
}
