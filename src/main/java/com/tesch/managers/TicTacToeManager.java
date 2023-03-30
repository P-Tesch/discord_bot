package com.tesch.managers;

import com.tesch.exceptions.TicTacToeException;
import com.tesch.games.Position;
import com.tesch.games.tictactoe.TicTacToeBoard;
import com.tesch.utils.DiscordUtils;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public class TicTacToeManager {

    private User[] players;
    private TicTacToeBoard board;
    private boolean inGame;
    
    public TicTacToeManager() {
        this.players = new User[2];
        this.inGame = false;
    }

    public boolean isInGame() {
        return this.inGame;
    }

    public void onTicTacToeCommand(MessageReceivedEvent event) {
        TextChannel text = event.getChannel().asTextChannel();

        if (!this.instantiatePlayers(event)) return;
        if (this.inGame) {
            DiscordUtils.sendMessage("There is already a game running", text);
            return;
        }

        this.board = new TicTacToeBoard(this.players);

        this.inGame = true;
        event.getChannel().sendMessage(this.board.getBoardAsMessageCreateData()).queue();
    }

    public void onButtonInteraction(ButtonInteractionEvent event) {
        try {
            this.board.makeMove(new Position(event.getButton().getId().split("_")[1]), event.getUser());
            event.editMessage(MessageEditData.fromCreateData(this.board.getBoardAsMessageCreateData())).queue();
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
        TextChannel text = event.getChannel().asTextChannel();
        this.players[0] = event.getAuthor();
        this.players[1] = event.getMessage().getMentions().getUsers().get(0);
        if (this.players[0] == null || this.players[1] == null) {
            DiscordUtils.sendMessage("TicTacToe must have two players", text);
            return false;
        }
        if (this.players[0] == this.players[1]) {
            DiscordUtils.sendMessage("You can't play with yourself", text);
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
