package com.tesch.api.games.chess;

import com.tesch.api.games.Position;

public class ChessPosition extends Position {

    private Character chessColumn;
    private Integer chessRow;

    public ChessPosition(Position position) {
        super(position.getRow(), position.getColumn());
        this.chessRow = 8 - position.getRow();
        this.chessColumn =  (char) ('a' + position.getColumn());
    }

    public ChessPosition(int chessRow, char chessColumn) {
        super(8 - chessRow, chessColumn - 'a');
        this.chessRow = chessRow;
        this.chessColumn = chessColumn;
    }

    public Character getChessColumn() {
        return this.chessColumn;
    }

    public Integer getChessRow() {
        return this.chessRow;
    }

    public void setChessColumn(Character chessColumn) {
        this.chessColumn = chessColumn;
        this.setColumn(this.chessColumn - 'a');
    }

    public void setChessRow(Integer chessRow) {
        this.chessRow = chessRow;
        this.setRow(this.chessRow - 8);
    }

    public void setAllFromPosition(Position position) {
        this.setColumn(position.getColumn());
        this.setRow(position.getRow());
    }

    @Override
    public void setColumn(Integer column){
        super.setColumn(column);
        this.setChessColumn((char) ('a' + this.getColumn()));
    }

    @Override
    public void setRow(Integer row) {
        super.setRow(row);
        this.setChessRow(8 - this.getRow());
    }

    @Override
    public String toString() {
        return "ChessPosition: " + this.chessRow + " " + this.chessColumn + "\nPosition: " + this.getRow() + " " + this.getColumn();
    }
}
