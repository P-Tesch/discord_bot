package com.tesch.games.musicle;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.tesch.managers.MusicleManager;
import com.tesch.utils.DiscordUtils;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MusicleLobby {

    private MusicleManager manager;
    private MessageReceivedEvent event;
    private Set<User> players;
    private Map<User, Integer> selections;

    public MusicleLobby(MusicleManager manager, MessageReceivedEvent event) {
        this.manager = manager;
        this.event = event;
        this.players = new HashSet<>();
        this.selections = new HashMap<>();
    }
    
    public void setup(String url) throws InterruptedException {
        this.manager.getScheduler().cancelAll();
        this.manager.getMusicPlayer().setMusicleMode(true);

        players.add(event.getAuthor());
        event.getMessage().getMentions().getUsers().forEach(players::add);
        if (this.players.size() < 2) {
            DiscordUtils.sendMessage("Musicle lobby min players is 2", event.getChannel().asTextChannel());
            this.manager.stop();
            return;
        }
        if (this.players.size() + 1 > 9) {
            DiscordUtils.sendMessage("Musicle lobby max players is 9", event.getChannel().asTextChannel());
            this.manager.stop();
            return;
        }
        players.forEach(player -> this.manager.setupPlayerScore(player));
    }

    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!this.players.contains(event.getUser())) {
            return;
        }
        if (this.players.size() == 0) {
            event.editMessage("Time's up").queue(msg -> event.getMessage().editMessageEmbeds().setActionRows().queue());
        }
        if (this.selections.keySet().contains(event.getUser())) {
            return;
        }
        this.selections.putIfAbsent(event.getUser(), Integer.parseInt(event.getButton().getId().split("_")[1]));
    
        if (this.selections.keySet().containsAll(this.players)) {
            this.stop(event);
        }
        event.getMessage().reply(event.getUser().getAsMention() + " Voted").queue();
    }

    private void stop(ButtonInteractionEvent event) {
        StringBuilder builder = new StringBuilder();
        this.manager.getAnswers()[this.manager.getAnswerIndex()] = this.manager.getAnswers()[this.manager.getAnswerIndex()] + " ✅";
        this.selections.forEach((player, selection) -> {
            this.manager.getAnswers()[selection - 1] = this.manager.getAnswers()[selection - 1] + " " + player.getAsMention();
            if (selection == this.manager.getAnswerIndex() + 1) {
                this.manager.getPlayerScore().get(player).addWin();
            }
            else {
                this.manager.getPlayerScore().get(player).addLoss();
            }
        });
        Arrays.asList(this.manager.getAnswers()).forEach(x -> builder.append(x + "\n"));
        builder.append("\nTitle: " + this.manager.getMusicPlayer().getAudioPlayer().getPlayingTrack().getInfo().title);
        event.editMessage(builder.toString()).queue(msg -> event.getMessage().editMessageEmbeds().setActionRows().queue());
        this.manager.stop();
    }
}
