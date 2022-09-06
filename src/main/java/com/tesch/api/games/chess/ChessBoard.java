package com.tesch.api.games.chess;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import com.tesch.api.games.Board;
import com.tesch.api.games.Position;
import com.tesch.api.games.Teams;
import com.tesch.api.games.chess.enums.Color;
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

    protected ChessBoard(Integer boardSize, User[] players, ChessMatch match) {
        super(boardSize, players);
        this.buildBoard();
        this.match = match;
    }

    @Override
    protected Message getBoardAsMessage() {
        MessageBuilder message = new MessageBuilder();
        this.getPlayers().keySet().forEach(x -> message.mentionUsers(x.getIdLong()));
        if (this.getWin() != null) {
            message.setContent("Winner: ");
            message.append(this.getPlayers().entrySet().stream().filter(x -> x.getValue() == this.getWin()).toList().get(0).getKey().getAsMention());
        }
        else {
            message.setContent("Player: ");
            message.append(match.getCurrentPlayerAsMention());
        }

        return message.build();
    }

    protected byte[] getBoardAsByteArray(boolean[][] possibleMoves) {
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

    @Override
    protected void makeMove(Position position, User player) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected Teams checkWinImpl() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean positionExists(Position position) {
        if (position.getColumn() < this.getBoard().length && position.getRow() < this.getBoard().length) {
            return true;
        }
        return false;
    }

    private void buildBoard() {
        // Pawns
        for (int i = 1; i <= 6; i += 5) {
            for (int j = 0; j < 8; j++) {
                Pawn pawn = new Pawn(i == 1 ? Color.BLACK : Color.WHITE, this);
                pawn.setChessPosition(new ChessPosition(new Position(i, j)));
                this.getBoard()[i][j] = pawn;
            }
        }

        // Other Pieces
        for (int i = 0; i <= 7; i += 7) {
            Color color = i == 0 ? Color.BLACK : Color.WHITE;
            Rook rook1 = new Rook(color, this);
            Rook rook2 = new Rook(color, this);
            Knight knight1 = new Knight(color, this);
            Knight knight2 = new Knight(color, this);
            Bishop bishop1 = new Bishop(color, this);
            Bishop bishop2 = new Bishop(color, this);
            Queen queen = new Queen(color, this);
            King king = new King(color, this);

            rook1.setChessPosition(new ChessPosition(new Position(i, 0)));
            rook2.setChessPosition(new ChessPosition(new Position(i, 7)));
            knight1.setChessPosition(new ChessPosition(new Position(i, 1)));
            knight2.setChessPosition(new ChessPosition(new Position(i, 6)));
            bishop1.setChessPosition(new ChessPosition(new Position(i, 2)));
            bishop2.setChessPosition(new ChessPosition(new Position(i, 5)));
            queen.setChessPosition(new ChessPosition(new Position(i, 3)));
            king.setChessPosition(new ChessPosition(new Position(i, 4)));

            this.getBoard()[i][0] = rook1;
            this.getBoard()[i][7] = rook2;
            this.getBoard()[i][1] = knight1;
            this.getBoard()[i][6] = knight2;
            this.getBoard()[i][2] = bishop1;
            this.getBoard()[i][5] = bishop2;
            this.getBoard()[i][3] = queen;
            this.getBoard()[i][4] = king;
        }
    }
    
}
