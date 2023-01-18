package com.tesch.api.games.chess.pieces;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.tesch.api.games.Position;
import com.tesch.api.games.chess.ChessBoard;
import com.tesch.api.games.chess.ChessMove;
import com.tesch.api.games.chess.ChessPiece;
import com.tesch.api.games.chess.enums.Color;

public class Pawn extends ChessPiece {

    public Pawn(Color color, ChessBoard chessBoard) {
        super(color, chessBoard);
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

        ChessPiece enPassantPiece;
        Position targetPosition;
        int nextRow = this.getChessPosition().getRow() + (this.getColor() == Color.BLACK ? 1 : -1);
        int secondRow = this.getChessPosition().getRow() + (this.getColor() == Color.BLACK ? 2 : -2);
        int nextColumn = this.getChessPosition().getColumn() + 1;
        int previousColumn = this.getChessPosition().getColumn() - 1;

        // Normal moves
        targetPosition = new Position(nextRow, this.getPosition().getColumn());
        if (this.getChessBoard().positionExists(targetPosition)) {
            possibleMoves[targetPosition.getRow()][targetPosition.getColumn()] = this.canMove(targetPosition) && !this.isThereOpponentPiece(targetPosition);
        }

        targetPosition.setRow(secondRow);
        if (this.isFirstMove() && this.getChessBoard().positionExists(targetPosition)) {
            possibleMoves[targetPosition.getRow()][targetPosition.getColumn()] = this.canMove(targetPosition) && !this.isThereOpponentPiece(targetPosition);
        }

        // Capture
        targetPosition.setRow(nextRow);
        targetPosition.setColumn(nextColumn);
        if (this.getChessBoard().positionExists(targetPosition)) {
            possibleMoves[targetPosition.getRow()][targetPosition.getColumn()] = this.isThereOpponentPiece(targetPosition);
        }

        targetPosition.setRow(nextRow);
        targetPosition.setColumn(previousColumn);
        if (this.getChessBoard().positionExists(targetPosition)) {
            possibleMoves[targetPosition.getRow()][targetPosition.getColumn()] = this.isThereOpponentPiece(targetPosition);
        }

        // En Passant
        targetPosition.setRow(nextRow);
        targetPosition.setColumn(nextColumn);
        if (this.getChessBoard().positionExists(targetPosition)) {
            enPassantPiece = this.getChessBoard().getPieceAt(new Position(this.getPosition().getRow(), targetPosition.getColumn()));
            if (enPassantPiece != null) { 
                possibleMoves[targetPosition.getRow()][targetPosition.getColumn()] = 
                    this.isThereOpponentPiece(enPassantPiece.getPosition()) 
                    && enPassantPiece instanceof Pawn
                    && ((Pawn) enPassantPiece).isEnPassantVulnerable();
            }
        }

        targetPosition.setRow(nextRow);
        targetPosition.setColumn(previousColumn);
        if (this.getChessBoard().positionExists(targetPosition)) {
            enPassantPiece = this.getChessBoard().getPieceAt(new Position(this.getPosition().getRow(), targetPosition.getColumn()));
            if (enPassantPiece != null) { 
                possibleMoves[targetPosition.getRow()][targetPosition.getColumn()] = 
                    this.isThereOpponentPiece(enPassantPiece.getPosition()) 
                    && enPassantPiece instanceof Pawn
                    && ((Pawn) enPassantPiece).isEnPassantVulnerable();
            }
        }

        return possibleMoves;
    }

    public boolean isEnPassantVulnerable() {
        ChessMove lastMove = this.getChessBoard().getMoveHistory().get(this.getChessBoard().getTurn() - 1);
        return this.getChessBoard().getTurn() > 0
            && lastMove.getTo().equals(this.getPosition())
            && (lastMove.getFrom().getRow() == this.getPosition().getRow() + 2 
                || lastMove.getFrom().getRow() == this.getPosition().getRow() - 2);
    }
}
