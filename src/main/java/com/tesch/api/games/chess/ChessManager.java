package com.tesch.api.games.chess;

import com.tesch.api.utils.DiscordUtils;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ChessManager {

    private User[] players;
    private ChessBoard board;
    private DiscordUtils discordUtils;
    
    public ChessManager() {
        this.players = new User[2];
        this.discordUtils = new DiscordUtils();
    }

    public void onChessCommand(MessageReceivedEvent event) {
        this.discordUtils.buildFromMessageEvent(event);

        if (!this.instantiatePlayers(event)) return;
        
        this.board = new ChessBoard(8, players);
        event.getChannel().sendFile(this.board.getBoardAsByteArray(), "board.png").queue(x -> x.editMessage(this.board.getBoardAsMessage()).queue());
    }

    private boolean instantiatePlayers(MessageReceivedEvent event) {
        this.players[0] = event.getAuthor();
        this.players[1] = event.getMessage().getMentions().getUsers().get(0);
        if (this.players[0] == null || this.players[1] == null) {
            this.discordUtils.sendMessage("TicTacToe must have two players");
            return false;
        }
        if (this.players[0] == this.players[1]) {
            this.discordUtils.sendMessage("You can't play with yourself");
            return false;
        }
        return true;
    }
}
