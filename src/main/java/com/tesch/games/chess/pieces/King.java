package com.tesch.games.chess.pieces;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.tesch.games.Position;
import com.tesch.games.chess.ChessBoard;
import com.tesch.games.chess.ChessPiece;
import com.tesch.games.chess.enums.Color;

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

    public boolean isInCheck() {
        for (ChessPiece piece : this.getChessBoard().getPiecesOnBoard().stream().filter(x -> x.getColor() != this.getColor() && !(x instanceof King)).toList()) {
            if (piece.possibleMoves()[this.getPosition().getRow()][this.getPosition().getColumn()] 
                && !(piece instanceof Pawn 
                && this.getPosition().getColumn() == piece.getPosition().getColumn())
            ) {
                return true;
            }
        }
        return false;
    }

    public boolean isInCheck(Position futurePosition) {
        this.getChessBoard().makeMove(this.getPosition(), futurePosition);
        for (ChessPiece piece : this.getChessBoard().getPiecesOnBoard().stream().filter(x -> x.getColor() != this.getColor()).toList()) {
            if (!(piece instanceof King)
                && piece.possibleMoves()[this.getPosition().getRow()][this.getPosition().getColumn()] 
                && !(piece instanceof Pawn
                && this.getPosition().getColumn() == piece.getPosition().getColumn())
            ) {
                this.getChessBoard().undoMove(this.getChessBoard().getTurn());
                return true;
            }
            if (piece instanceof King && 
                ((King) piece).possibleMovesIgnoreCheck()[this.getPosition().getRow()][this.getPosition().getColumn()]
            ) {
                this.getChessBoard().undoMove(this.getChessBoard().getTurn());
                return true;
            }
        }
        this.getChessBoard().undoMove(this.getChessBoard().getTurn());
        return false;
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] possibleMoves = new boolean[this.getChessBoard().getBoard().length][this.getChessBoard().getBoard().length];

        if (this.isFirstMove()) {
            // King side castle
            ChessPiece kingSideRook = (ChessPiece) this.getChessBoard().getBoard()[this.getPosition().getRow()][this.getPosition().getColumn() + 3];
            if (kingSideRook instanceof Rook 
                && kingSideRook.isFirstMove()
                && this.getChessBoard().getBoard()[this.getPosition().getRow()][this.getPosition().getColumn() + 1] == null
                && this.getChessBoard().getBoard()[this.getPosition().getRow()][this.getPosition().getColumn() + 2] == null
                && !this.isInCheck(new Position(this.getPosition().getRow(), this.getPosition().getColumn() + 1))
                && !this.isInCheck(new Position(this.getPosition().getRow(), this.getPosition().getColumn() + 2))
            ) {
                possibleMoves[this.getPosition().getRow()][this.getPosition().getColumn() + 2] = true;
            }

            // Queen sidee castle
            ChessPiece queenSideRook = (ChessPiece) this.getChessBoard().getBoard()[this.getPosition().getRow()][this.getPosition().getColumn() - 4];
            if (queenSideRook instanceof Rook 
                && queenSideRook.isFirstMove()
                && this.getChessBoard().getBoard()[this.getPosition().getRow()][this.getPosition().getColumn() - 1] == null
                && this.getChessBoard().getBoard()[this.getPosition().getRow()][this.getPosition().getColumn() - 2] == null
                && this.getChessBoard().getBoard()[this.getPosition().getRow()][this.getPosition().getColumn() - 3] == null
                && !this.isInCheck(new Position(this.getPosition().getRow(), this.getPosition().getColumn() - 1))
                && !this.isInCheck(new Position(this.getPosition().getRow(), this.getPosition().getColumn() - 2))
            ) {
                possibleMoves[this.getPosition().getRow()][this.getPosition().getColumn() - 2] = true;
            }
        }
		
		// Normal moves
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
                if (!(i == 0 && j == 0)) {
                    Position targetPosition = new Position(this.getPosition().getRow() + i, this.getPosition().getColumn() + j);
                    if (this.getChessBoard().positionExists(targetPosition)) {
                        if (!this.isInCheck(targetPosition)) {
                            possibleMoves[targetPosition.getRow()][targetPosition.getColumn()] = this.canMove(targetPosition);
                        }
                    }
                }
			}
		}
        return possibleMoves;
    } 

    protected boolean[][] possibleMovesIgnoreCheck() {
        boolean[][] possibleMoves = new boolean[this.getChessBoard().getBoard().length][this.getChessBoard().getBoard().length];
		
		// Normal moves
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
                if (!(i == 0 && j == 0)) {
                    Position targetPosition = new Position(this.getPosition().getRow() + i, this.getPosition().getColumn() + j);
                    if (this.getChessBoard().positionExists(targetPosition)) {
                        possibleMoves[targetPosition.getRow()][targetPosition.getColumn()] = this.canMove(targetPosition);
                    }
                }
			}
		}
        return possibleMoves;
    }
}
