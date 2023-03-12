package com.tesch.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.tesch.exceptions.ChessException;
import com.tesch.exceptions.GameException;
import com.tesch.games.chess.ChessMatch;
import com.tesch.games.chess.ChessPosition;
import com.tesch.utils.DiscordUtils;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ChessManager {

    private List<ChessMatch> matches;
    
    public ChessManager() {
        this.matches = new ArrayList<>();
    }

    public List<ChessMatch> getMatches() {
        return this.matches;
    }

    public void onChessCommand(MessageReceivedEvent event) {
        User[] players;
        ChessMatch match;

        try {
            players = this.instantiatePlayers(event);
            match = this.createMatch(players, event.getChannel().asTextChannel());
            this.matches.add(match);
        }
        catch (GameException e) {
            DiscordUtils.sendMessage(e.getMessage(), event.getChannel().asTextChannel());
            return;
        }

        this.printBoard(event.getChannel().asTextChannel(), match);
    }

    public void onMoveCommand(MessageReceivedEvent event) {
        try {
            ChessMatch match = this.matches.stream().filter(x -> Arrays.asList(x.getPlayers()).contains(event.getAuthor())).toList().get(0);
            String message = event.getMessage().getContentRaw();
            if (message.equals("cancel")) {
                match.cancelMove(event.getAuthor());
            }
            else {
                ChessPosition position = new ChessPosition(Integer.parseInt(String.valueOf(message.charAt(1))), message.charAt(0));
                if (match.getSelectedPiece() == null) {
                    match.selectPiece(position, event.getAuthor());
                }
                else {
                    match.moveSelectedPiece(position, event.getAuthor());
                }
            }

            if (match.isPromoting()) return;
            this.printBoard(event.getChannel().asTextChannel(), match);
        }
        catch (ChessException e) {
            event.getChannel().sendMessage(e.getMessage()).queue();
        }
    }

    public void onChessButton(ButtonInteractionEvent event) {
        ChessMatch targetMatch = this.matches.stream().filter(match -> Arrays.asList(match.getPlayers()).contains(event.getUser())).toList().get(0);
        if (targetMatch != null) {
            targetMatch.onChessButton(event);
        }
        this.printBoard(event.getChannel().asTextChannel(), targetMatch);
    }

    public boolean userIsInMatch(User user) {
        return this.matches.stream().filter(x -> Arrays.asList(x.getPlayers()).contains(user)).toList().size() > 0 ? true : false;
    }

    public void endMatch(ChessMatch match) {
        matches.remove(match);
    }

    private User[] instantiatePlayers(MessageReceivedEvent event) {
        User[] players = new User[2];
        players[0] = event.getAuthor();
        players[1] = event.getMessage().getMentions().getUsers().get(0);
        if (players[0] == null || players[1] == null) {
            throw new GameException("TicTacToe must have two players");
        }
        if (players[0] == players[1]) {
            throw new GameException("You can't play with yourself");
        }
        return players;
    }

    private ChessMatch createMatch(User[] players, TextChannel channel) {
        List<ChessMatch> matchesWithPlayers = this.matches.stream().filter(x -> {
            for (int i = 0; i < x.getPlayers().length; i++) {
                if (Arrays.asList(x.getPlayers()).contains(players[i])) {
                    return true;
                };
            }
            return false;
        }).toList();
        if (matchesWithPlayers.size() > 0) {
            List<String> playersInMatchMentions = new ArrayList<>(); 
            matchesWithPlayers.forEach(
                x -> Arrays.asList(x.getPlayers()).stream().filter(
                    y -> Arrays.asList(players).contains(y)
                ).forEach(
                        z -> playersInMatchMentions.add(z.getAsMention())
                    )
            );
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("The following players are already in a match: ");
            playersInMatchMentions.forEach(stringBuilder::append);
            throw new GameException(stringBuilder.toString());
        }
        return new ChessMatch(players, this, channel);
    }

    private void printBoard(TextChannel channel, ChessMatch match) {
        channel.sendFile(match.getChessBoard().getBoardAsByteArray(match.getSelectedPiece() == null ? null : match.getSelectedPiece().possibleMoves()), "board.png").queue(x -> x.editMessage(match.getChessBoard().getBoardAsMessage()).queue());
    }
}
