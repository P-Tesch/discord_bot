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
		
		//Up - Left
		if (this.getChessBoard().positionExists(new Position(this.getPosition().getRow() - 2, this.getPosition().getColumn() - 1))) {
            ChessPiece target = (ChessPiece) this.getChessBoard().getBoard()[this.getChessPosition().getRow() - 2][this.getChessPosition().getColumn() - 1];
			possibleMoves[this.getPosition().getRow() - 2][this.getPosition().getColumn() - 1] = target == null || target.getColor() != this.getColor();
		}
		
		//Up - Right
        if (this.getChessBoard().positionExists(new Position(this.getPosition().getRow() - 2, this.getPosition().getColumn() + 1))) {
            ChessPiece target = (ChessPiece) this.getChessBoard().getBoard()[this.getChessPosition().getRow() - 2][this.getChessPosition().getColumn() + 1];
			possibleMoves[this.getPosition().getRow() - 2][this.getPosition().getColumn() + 1] = target == null || target.getColor() != this.getColor();
		}
		
		//Right - Up
        if (this.getChessBoard().positionExists(new Position(this.getPosition().getRow() - 1, this.getPosition().getColumn() + 2))) {
            ChessPiece target = (ChessPiece) this.getChessBoard().getBoard()[this.getChessPosition().getRow() - 1][this.getChessPosition().getColumn() + 2];
			possibleMoves[this.getPosition().getRow() - 1][this.getPosition().getColumn() + 2] = target == null || target.getColor() != this.getColor();
		}
		
		//Right - Down
        if (this.getChessBoard().positionExists(new Position(this.getPosition().getRow() + 1, this.getPosition().getColumn() + 2))) {
            ChessPiece target = (ChessPiece) this.getChessBoard().getBoard()[this.getChessPosition().getRow() + 1][this.getChessPosition().getColumn() + 2];
			possibleMoves[this.getPosition().getRow() + 1][this.getPosition().getColumn() + 2] = target == null || target.getColor() != this.getColor();
		}
		
		//Down - Right
        if (this.getChessBoard().positionExists(new Position(this.getPosition().getRow() + 2, this.getPosition().getColumn() + 1))) {
            ChessPiece target = (ChessPiece) this.getChessBoard().getBoard()[this.getChessPosition().getRow() + 2][this.getChessPosition().getColumn() + 1];
			possibleMoves[this.getPosition().getRow() + 2][this.getPosition().getColumn() + 1] = target == null || target.getColor() != this.getColor();
		}
		
		//Down - Left
        if (this.getChessBoard().positionExists(new Position(this.getPosition().getRow() + 2, this.getPosition().getColumn() - 1))) {
            ChessPiece target = (ChessPiece) this.getChessBoard().getBoard()[this.getChessPosition().getRow() + 2][this.getChessPosition().getColumn() - 1];
			possibleMoves[this.getPosition().getRow() + 2][this.getPosition().getColumn() - 1] = target == null || target.getColor() != this.getColor();
		}
		
		//Left - Down
        if (this.getChessBoard().positionExists(new Position(this.getPosition().getRow() + 1, this.getPosition().getColumn() - 2))) {
            ChessPiece target = (ChessPiece) this.getChessBoard().getBoard()[this.getChessPosition().getRow() + 1][this.getChessPosition().getColumn() - 2];
			possibleMoves[this.getPosition().getRow() + 1][this.getPosition().getColumn() - 2] = target == null || target.getColor() != this.getColor();
		}
		
		//Left - Up
        if (this.getChessBoard().positionExists(new Position(this.getPosition().getRow() -1, this.getPosition().getColumn() - 2))) {
            ChessPiece target = (ChessPiece) this.getChessBoard().getBoard()[this.getChessPosition().getRow() - 1][this.getChessPosition().getColumn() - 2];
			possibleMoves[this.getPosition().getRow() - 1][this.getPosition().getColumn() - 2] = target == null || target.getColor() != this.getColor();
		}
				
		return possibleMoves;
    }

    
}
