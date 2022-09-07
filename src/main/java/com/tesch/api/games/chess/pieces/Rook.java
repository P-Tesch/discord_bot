package com.tesch.api.games.chess.pieces;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

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
			ChessPiece piece = (ChessPiece) this.getChessBoard().getBoard()[i][this.getChessPosition().getColumn()];
			if (piece == null) {
				possibleMoves[i][this.getPosition().getColumn()] = true;
			}
			else if (piece.getColor() != this.getColor()) {
				possibleMoves[i][this.getPosition().getColumn()] = true;
				break;
			}
			else {
				break;
			}
		}
		
		//Right
		for (int i = this.getPosition().getColumn() + 1; i < this.getChessBoard().getBoard().length; i++) {
			ChessPiece piece = (ChessPiece) this.getChessBoard().getBoard()[this.getChessPosition().getRow()][i];
			if (piece == null) {
				possibleMoves[this.getPosition().getRow()][i] = true;
			}
			else if (piece.getColor() != this.getColor()) {
				possibleMoves[this.getPosition().getRow()][i] = true;
				break;
			}
			else {
				break;
			}
		}
		
		//Down
		for (int i = this.getPosition().getRow() + 1; i < this.getChessBoard().getBoard().length; i++) {
			ChessPiece piece = (ChessPiece) this.getChessBoard().getBoard()[i][this.getChessPosition().getColumn()];
			if (piece == null) {
				possibleMoves[i][this.getPosition().getColumn()] = true;
			}
			else if (piece.getColor() != this.getColor()) {
				possibleMoves[i][this.getPosition().getColumn()] = true;
				break;
			}
			else {
				break;
			}
		}
		
		//Left
		for (int i = this.getPosition().getColumn() - 1; i >= 0; i--) {
			ChessPiece piece = (ChessPiece) this.getChessBoard().getBoard()[this.getChessPosition().getRow()][i];
			if (piece == null) {
				possibleMoves[this.getPosition().getRow()][i] = true;
			}
			else if (piece.getColor() != this.getColor()) {
				possibleMoves[this.getPosition().getRow()][i] = true;
				break;
			}
			else {
				break;
			}
		}

        return possibleMoves;
    }
}
