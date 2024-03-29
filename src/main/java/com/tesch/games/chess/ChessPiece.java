package com.tesch.games.chess;

import java.awt.image.BufferedImage;

import com.tesch.exceptions.ChessException;
import com.tesch.games.Piece;
import com.tesch.games.Position;
import com.tesch.games.chess.enums.Color;
import com.tesch.games.chess.pieces.King;
import com.tesch.games.chess.pieces.Pawn;

public abstract class ChessPiece extends Piece {

    private Color color;
    private ChessPosition chessPosition;
    private ChessBoard chessBoard;
    private boolean firstMove;
    
    protected ChessPiece(Color color, ChessBoard chessBoard) {
        super(color.getTeam());
        this.color = color;
        this.chessBoard = chessBoard;
        this.firstMove = true;
    }

    public ChessBoard getChessBoard() {
        return this.chessBoard;
    }

    public ChessPosition getChessPosition() {
        return this.chessPosition;
    }

    public void setChessPosition(ChessPosition chessPosition) {
        this.chessPosition = chessPosition;
        super.setPosition(chessPosition.getRow(), chessPosition.getColumn());
    }

    @Override
    public void setPosition(Position position) {
        super.setPosition(position);
        if (this.chessPosition == null) {
            this.chessPosition = new ChessPosition(position);
        }
        else {
            this.chessPosition.setAllFromPosition(position);
        }
    }

    public Color getColor() {
        return this.color;
    }

    public boolean isFirstMove() {
        return this.firstMove;
    }

    protected boolean canMove(Position position) {
        if (!this.getChessBoard().positionExists(position)) throw new IllegalArgumentException("Position does not exist");

        ChessPiece target = this.getChessBoard().getPieceAt(position);
        return target == null || this.isThereOpponentPiece(position);
    }

    protected boolean isThereOpponentPiece(Position position) {
        if (!this.getChessBoard().positionExists(position)) throw new IllegalArgumentException("Position does not exist");

        ChessPiece target = this.getChessBoard().getPieceAt(position);
        return target != null && target.getColor() != this.getColor();
    }

    public void makeMove(Position position) {
        if (!this.getChessBoard().positionExists(position)) throw new ChessException("Position does not exist");
        if (!this.possibleMoves()[position.getRow()][position.getColumn()]) throw new ChessException("Move not possible");

        if (this instanceof King) { 
            if (position.getColumn() - 2 == this.getPosition().getColumn()) {
                ((ChessPiece) this.getChessBoard().getBoard()[this.getPosition().getRow()][this.getPosition().getColumn() + 3]).makeMove(new Position(this.getPosition().getRow(), this.getPosition().getColumn() + 1));
            } 
            else if (position.getColumn() + 2 == this.getPosition().getColumn()) {
                ((ChessPiece) this.getChessBoard().getBoard()[this.getPosition().getRow()][this.getPosition().getColumn() - 4]).makeMove(new Position(this.getPosition().getRow(), this.getPosition().getColumn() - 1));
            }
        }

        this.getChessBoard().makeMove(this.getPosition(), position);
        this.firstMove = false;
        if (((King) this.getChessBoard().getKing(this.getColor())).isInCheck()) {
            this.getChessBoard().undoMove(this.getChessBoard().getTurn());
            throw new ChessException("Your king is in check");
        }

        if (this instanceof Pawn && (this.color == Color.WHITE && this.getPosition().getRow() == 0) || (this.color == Color.BLACK && this.getPosition().getRow() == 7)) {
            this.getChessBoard().getMatch().promotionSelect(this);
        }
    }

    public abstract BufferedImage getAsImage();
    public abstract boolean[][] possibleMoves();
}
