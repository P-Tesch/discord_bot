package com.tesch.api.games.chess.pieces;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.tesch.api.games.Position;
import com.tesch.api.games.chess.ChessBoard;
import com.tesch.api.games.chess.ChessPiece;
import com.tesch.api.games.chess.enums.Color;

public class Rook extends ChessPiece {

    public Rook(Color color, ChessBoard ChessBoard) {
        super(color, ChessBoard);
    }
    
    public BufferedImage getAsImage() {
        try {
            BufferedImage img = ImageIO.read(new File("src/main/resources/games/chess/rook_"+ this.getColor().toString() +".png"));
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

        //Up
		for (int i = this.getPosition().getRow() - 1; i >= 0; i--) {
			Position targetPosition = new Position(i, this.getPosition().getColumn());

			if (!this.canMove(targetPosition)) break;
			possibleMoves[targetPosition.getRow()][targetPosition.getColumn()] = true;
			if (this.isThereOpponentPiece(targetPosition)) break;
		}
		
		//Right
		for (int i = this.getPosition().getColumn() + 1; i < this.getChessBoard().getBoard().length; i++) {
			Position targetPosition = new Position(this.getPosition().getRow(), i);

			if (!this.canMove(targetPosition)) break;
			possibleMoves[targetPosition.getRow()][targetPosition.getColumn()] = true;
			if (this.isThereOpponentPiece(targetPosition)) break;
		}
		
		//Down
		for (int i = this.getPosition().getRow() + 1; i < this.getChessBoard().getBoard().length; i++) {
			Position targetPosition = new Position(i, this.getPosition().getColumn());

			if (!this.canMove(targetPosition)) break;
			possibleMoves[targetPosition.getRow()][targetPosition.getColumn()] = true;
			if (this.isThereOpponentPiece(targetPosition)) break;
		}
		
		//Left
		for (int i = this.getPosition().getColumn() - 1; i >= 0; i--) {
			Position targetPosition = new Position(this.getPosition().getRow(), i);
			
			if (!this.canMove(targetPosition)) break;
			possibleMoves[targetPosition.getRow()][targetPosition.getColumn()] = true;
			if (this.isThereOpponentPiece(targetPosition)) break;
		}

        return possibleMoves;
    }
}
