package com.tesch.games.quiz;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class QuizManager {

    private List<QuizLobby> lobbies;
    private Color color;
    private String title;
    
    public QuizManager(Color color, String title) {
        this.lobbies = new ArrayList<>();
        this.color = color;
        this.title = title;
    }

    protected QuizLobby createLobby(QuizQuestion question, TextChannel textChannel, Set<User> players) {
        QuizLobby lobby = new QuizLobby(question, textChannel, players, title.toLowerCase(), this);
        lobby.buildMessage(this.title, this.color);
        this.lobbies.add(lobby);
        return lobby;
    }

    public void onButtonInteraction(ButtonInteractionEvent event) {
        QuizLobby lobby = lobbies.stream().filter(l -> l.getPlayers().contains(event.getUser())).toList().get(0);
        lobby.onButtonInteraction(event);
    }

    public void endLobby(QuizLobby lobby) {
        this.lobbies.remove(lobby);
    }
}
