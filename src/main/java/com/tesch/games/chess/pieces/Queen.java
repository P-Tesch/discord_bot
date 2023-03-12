package com.tesch.games.chess.pieces;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.tesch.games.chess.ChessBoard;
import com.tesch.games.chess.ChessPiece;
import com.tesch.games.chess.enums.Color;

public class Queen extends ChessPiece {

    public Queen(Color color, ChessBoard chessBoard) {
        super(color, chessBoard);
    }
    
    public BufferedImage getAsImage() {
        try {
            BufferedImage img = ImageIO.read(new File("src/main/resources/games/chess/queen_"+ this.getColor().toString() +".png"));
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

        ChessPiece rook = new Rook(this.getColor(), this.getChessBoard());
        ChessPiece bishop = new Bishop(this.getColor(), this.getChessBoard());

        rook.setChessPosition(this.getChessPosition());
        bishop.setChessPosition(this.getChessPosition());

        boolean[][] rookPossibleMoves = rook.possibleMoves();
        boolean[][] bishopPossibleMoves = bishop.possibleMoves();

        for (int i = 0; i < possibleMoves.length; i++) {
            for (int j = 0; j < possibleMoves.length; j++) {
                possibleMoves[i][j] = rookPossibleMoves[i][j] || bishopPossibleMoves[i][j];
            }
        }

        return possibleMoves;
    }

    
}
