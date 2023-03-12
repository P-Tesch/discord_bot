package com.tesch.api.games.chess;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.tesch.api.games.Board;
import com.tesch.api.games.Position;
import com.tesch.api.games.chess.enums.Color;
import com.tesch.api.games.chess.exceptions.ChessException;
import com.tesch.api.games.chess.pieces.Bishop;
import com.tesch.api.games.chess.pieces.King;
import com.tesch.api.games.chess.pieces.Knight;
import com.tesch.api.games.chess.pieces.Pawn;
import com.tesch.api.games.chess.pieces.Queen;
import com.tesch.api.games.chess.pieces.Rook;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

public class ChessBoard extends Board {

    private ChessMatch match;
    private List<ChessPiece> piecesOnBoard;
    private Map<Integer, ChessMove> moveHistory;

    protected ChessBoard(Integer boardSize, User[] players, ChessMatch match) {
        super(boardSize, players);
        this.piecesOnBoard = new ArrayList<>();
        this.moveHistory = new HashMap<>();
        this.buildBoard();
        this.match = match;
    }

    public List<ChessPiece> getPiecesOnBoard() {
        return this.piecesOnBoard;
    }

    public Integer getTurn() {
        return this.match.getTurn();
    }

    public Map<Integer, ChessMove> getMoveHistory() {
        return this.moveHistory;
    }

    protected ChessMatch getMatch() {
        return this.match;
    }

    @Override
    public Message getBoardAsMessage() {
        MessageBuilder message = new MessageBuilder();
        this.getPlayers().keySet().forEach(x -> message.mentionUsers(x.getIdLong()));
        if (this.match.getWinner() != null) {
            message.setContent("Winner: ");
            message.append(this.getPlayers().entrySet().stream().filter(x -> x.getValue() == this.match.getWinner().getTeam()).toList().get(0).getKey().getAsMention());
        }
        else {
            message.setContent("Player: ");
            message.append(match.getCurrentPlayerAsMention());
        }

        return message.build();
    }

    public byte[] getBoardAsByteArray(boolean[][] possibleMoves) {
        try {
            BufferedImage boardImage = ImageIO.read(new File("src/main/resources/games/chess/board.png"));
            Graphics2D graphics = boardImage.createGraphics();
            Arrays.asList(this.getBoard()).forEach(
                row -> Arrays.asList(row).forEach(
                    piece -> {
                        if (piece != null) {
                            graphics.drawImage(((ChessPiece)piece).getAsImage(), null, 128 * piece.getPosition().getColumn(), 128 * piece.getPosition().getRow());
                        }
                    }
                )
            );
            if (possibleMoves != null) {
                BufferedImage moveMarker = ImageIO.read(new File("src/main/resources/games/chess/move_mark.png"));
                for (int i = 0; i < possibleMoves.length; i++) {
                    for (int j = 0; j < possibleMoves[i].length; j++) {
                        if (possibleMoves[i][j]) {
                            graphics.drawImage(moveMarker, null, 128 * j, 128 * i);
                        }
                    }
                }
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(boardImage, "png", baos);
            return baos.toByteArray();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean positionExists(Position position) {
        if (position.getColumn() < this.getBoard().length 
            && position.getRow() < this.getBoard().length
            && position.getColumn() >= 0
            && position.getRow() >= 0
        ) {
            return true;
        }
        return false;
    }

    public ChessPiece getPieceAt(Position position) {
        return (ChessPiece) this.getBoard()[position.getRow()][position.getColumn()];
    }

    public void makeMove(Position from, Position to) {
        if (!(this.positionExists(from) || this.positionExists(to))) throw new ChessException("Position does not exist");

        ChessPiece removedPiece = this.removePiece(to);
        this.placePiece(this.removePiece(from), to);
        this.moveHistory.put(this.match.getTurn(), new ChessMove(removedPiece, from, to));
    }

    public void undoMove(Integer moveKey) {
        ChessMove move = this.moveHistory.get(moveKey);
        this.placePiece(this.removePiece(move.getTo()), move.getFrom());
        if (move.getCapturePiece() != null) {
            this.placePiece(move.getCapturePiece(), move.getTo());
        }
        this.moveHistory.remove(moveKey);
    }

    protected void placePiece(ChessPiece piece, Position position) {
        piece.setPosition(position);
        this.getBoard()[position.getRow()][position.getColumn()] = piece;
        if (!this.piecesOnBoard.contains(piece)) {
            this.piecesOnBoard.add(piece);
        }
    }

    protected ChessPiece removePiece(Position position) {
        ChessPiece removed = (ChessPiece) this.getBoard()[position.getRow()][position.getColumn()];
        this.getBoard()[position.getRow()][position.getColumn()] = null;
        this.piecesOnBoard.remove(removed);
        return removed;
    }

    protected void promote(ChessPiece toPromote, ChessPiece selectedPromotion) {
        this.removePiece(toPromote.getPosition());
        selectedPromotion.setPosition(toPromote.getPosition());
        this.placePiece(selectedPromotion, selectedPromotion.getPosition());
    }

    protected boolean isCheckMate(Color color) {
        King king = (King) this.getKing(color);
        if (king.isInCheck()) {
            for (ChessPiece piece : this.getPiecesOnBoard().stream().filter(x -> x.getColor() == king.getColor()).toList()) {
                boolean[][] possibleMoves = piece.possibleMoves();
                for (int i = 0; i < possibleMoves.length; i++) {
                    for (int j = 0; j < possibleMoves[i].length; j++) {
                        if (possibleMoves[i][j]) {
                            this.makeMove(piece.getPosition(), new Position(i, j));
                            if (!king.isInCheck()) {
                                System.out.println(piece.getChessPosition() + "\n\n" + new ChessPosition(new Position(i, j)));
                                this.undoMove(this.getTurn());
                                return false;
                            }
                            this.undoMove(this.getTurn());
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    protected ChessPiece getKing(Color color) {
        return this.piecesOnBoard.stream().filter(piece -> piece.getColor() == color && piece instanceof King).findFirst().get();
    }

    private void buildBoard() {
        for (int i = 0; i <= 7; i += 7) {
            Color color = i == 0 ? Color.BLACK : Color.WHITE;

            this.placePiece(new Rook(color, this), new Position(i, 0));
            this.placePiece(new Rook(color, this), new Position(i, 7));
            this.placePiece(new Knight(color, this), new Position(i, 1));
            this.placePiece(new Knight(color, this), new Position(i, 6));
            this.placePiece(new Bishop(color, this), new Position(i, 2));
            this.placePiece(new Bishop(color, this), new Position(i, 5));
            this.placePiece(new Queen(color, this), new Position(i, 3));
            this.placePiece(new King(color, this), new Position(i, 4));

            for (int j = 0; j < this.getBoard().length; j++) {
                this.placePiece(new Pawn(color, this), new Position(color == Color.BLACK ? i + 1 : i - 1, j));
            }
        }
    }
}
