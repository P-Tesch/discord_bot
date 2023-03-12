package com.tesch.games.chess.pieces;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.tesch.games.Position;
import com.tesch.games.chess.ChessBoard;
import com.tesch.games.chess.ChessPiece;
import com.tesch.games.chess.enums.Color;

public class Bishop extends ChessPiece {

    public Bishop(Color color, ChessBoard chessBoard) {
        super(color, chessBoard);
    }
    
    public BufferedImage getAsImage() {
        try {
            BufferedImage img = ImageIO.read(new File("src/main/resources/games/chess/bishop_"+ this.getColor().toString() +".png"));
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
		
		
		//Up - Right
		for (int i = this.getPosition().getRow() - 1, j = this.getPosition().getColumn() + 1; i >= 0 && j < this.getChessBoard().getBoard().length; i--, j++) {
			Position targetPosition = new Position(i, j);

			if (!this.canMove(targetPosition)) break;
			possibleMoves[targetPosition.getRow()][targetPosition.getColumn()] = true;
			if (this.isThereOpponentPiece(targetPosition)) break;
		}
		
		//Down - Right
		for (int i = this.getPosition().getRow() + 1, j = this.getPosition().getColumn() + 1; i < this.getChessBoard().getBoard().length && j < this.getChessBoard().getBoard().length; i++, j++) {
			Position targetPosition = new Position(i, j);

			if (!this.canMove(targetPosition)) break;
			possibleMoves[targetPosition.getRow()][targetPosition.getColumn()] = true;
			if (this.isThereOpponentPiece(targetPosition)) break;
		}
		
		//Down - Left
		for (int i = this.getPosition().getRow() + 1, j = this.getPosition().getColumn() - 1; i < this.getChessBoard().getBoard().length && j >= 0; i++, j--) {
			Position targetPosition = new Position(i, j);

			if (!this.canMove(targetPosition)) break;
			possibleMoves[targetPosition.getRow()][targetPosition.getColumn()] = true;
			if (this.isThereOpponentPiece(targetPosition)) break;
		}
		
		//Up - Left
		for (int i = this.getPosition().getRow() - 1, j = this.getPosition().getColumn() - 1; i >= 0 && j >= 0; i--, j--) {
			Position targetPosition = new Position(i, j);

			if (!this.canMove(targetPosition)) break;
			possibleMoves[targetPosition.getRow()][targetPosition.getColumn()] = true;
			if (this.isThereOpponentPiece(targetPosition)) break;
		}
		
		return possibleMoves;
    }
}
