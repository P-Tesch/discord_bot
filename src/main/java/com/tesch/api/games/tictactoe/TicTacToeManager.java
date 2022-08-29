package com.tesch.api.games.tictactoe;

import com.tesch.api.games.Position;
import com.tesch.api.games.tictactoe.exceptions.TicTacToeException;
import com.tesch.api.utils.DiscordUtils;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class TicTacToeManager {

    private User[] players;
    private DiscordUtils discordUtils;
    private TicTacToeBoard board;
    private boolean inGame;
    
    public TicTacToeManager() {
        this.players = new User[2];
        this.discordUtils = new DiscordUtils();
        this.inGame = false;
    }

    public boolean isInGame() {
        return this.inGame;
    }

    public void onTicTacToeCommand(MessageReceivedEvent event) {
        this.discordUtils.buildFromMessageEvent(event);

        if (!this.instantiatePlayers(event)) return;
        if (this.inGame) {
            this.discordUtils.sendMessage("There is already a game running");
            return;
        }

        this.board = new TicTacToeBoard(this.players);

        this.inGame = true;
        event.getChannel().sendMessage(this.board.getBoardAsMessage()).queue();
    }

    public void onButtonInteraction(ButtonInteractionEvent event) {
        try {
            this.board.makeMove(new Position(event.getButton().getId()), event.getUser());
            event.editMessage(this.board.getBoardAsMessage()).queue();
            if (this.board.getFinished() == true) {
                this.clear();
            }
        }
        catch (TicTacToeException e) {
            event.reply(e.getMessage()).queue();
        }
        catch (IllegalStateException e) {
            e.getMessage();
        }
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

    private void clear() {
        this.inGame = false;
        this.board = null;
        this.players[0] = null;
        this.players[1] = null;
    }
}
