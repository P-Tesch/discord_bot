package com.tesch.api.games.chess;

import com.tesch.api.games.Position;

public class ChessMove {
    
    private ChessPiece capturePiece;
    private Position from;
    private Position to;
    
    public ChessMove(ChessPiece capturePiece, Position from, Position to) {
        this.capturePiece = capturePiece;
        this.from = from;
        this.to = to;
    }

    public ChessPiece getCapturePiece() {
        return capturePiece;
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }  
}
