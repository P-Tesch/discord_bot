package com.tesch.api.games.chess.pieces;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.tesch.api.games.chess.ChessBoard;
import com.tesch.api.games.chess.ChessPiece;
import com.tesch.api.games.chess.enums.Color;

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
			ChessPiece piece = (ChessPiece) this.getChessBoard().getBoard()[i][j];
			if (piece == null) {
				possibleMoves[i][j] = true;
			}
			else if (piece.getColor() != this.getColor()) {
				possibleMoves[i][j] = true;
				break;
			}
			else {
				break;
			}
		}
		
		//Down - Right
		for (int i = this.getPosition().getRow() + 1, j = this.getPosition().getColumn() + 1; i < this.getChessBoard().getBoard().length && j < this.getChessBoard().getBoard().length; i++, j++) {
			ChessPiece piece = (ChessPiece) this.getChessBoard().getBoard()[i][j];
			if (piece == null) {
				possibleMoves[i][j] = true;
			}
			else if (piece.getColor() != this.getColor()) {
				possibleMoves[i][j] = true;
				break;
			}
			else {
				break;
			}
		}
		
		//Down - Left
		for (int i = this.getPosition().getRow() + 1, j = this.getPosition().getColumn() - 1; i < this.getChessBoard().getBoard().length && j >= 0; i++, j--) {
			ChessPiece piece = (ChessPiece) this.getChessBoard().getBoard()[i][j];
			if (piece == null) {
				possibleMoves[i][j] = true;
			}
			else if (piece.getColor() != this.getColor()) {
				possibleMoves[i][j] = true;
				break;
			}
			else {
				break;
			}
		}
		
		//Up - Left
		for (int i = this.getPosition().getRow() - 1, j = this.getPosition().getColumn() - 1; i >= 0 && j >= 0; i--, j--) {
			ChessPiece piece = (ChessPiece) this.getChessBoard().getBoard()[i][j];
			if (piece == null) {
				possibleMoves[i][j] = true;
			}
			else if (piece.getColor() != this.getColor()) {
				possibleMoves[i][j] = true;
				break;
			}
			else {
				break;
			}
		}
		
		return possibleMoves;
    }
}
