package com.tesch.games.quiz;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.tesch.db.BotuserRequester;
import com.tesch.games.quiz.musicle.MusicleManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

public class QuizLobby {

    private QuizManager manager;
    private QuizQuestion question;
    private TextChannel textChannel;
    private Message embedMessage;
    private EmbedBuilder embedBuilder;
    private Map<User, Integer> playersAndSelections;
    private String prefix;
    private List<String> answers;

    protected QuizLobby(QuizQuestion question, TextChannel textChannel, Set<User> players, String prefix, QuizManager manager) {
        this.embedBuilder = new EmbedBuilder();
        this.textChannel = textChannel;
        this.playersAndSelections = new HashMap<>();
        players.forEach(player -> this.playersAndSelections.put(player, null));
        this.question = question;
        this.prefix = prefix;
        this.manager = manager;
    }

    public Set<User> getPlayers() {
        return this.playersAndSelections.keySet();
    }

    public void buildMessage(String title, Color color) {
        this.embedBuilder.setTitle(title);
        this.embedBuilder.setColor(color);

        StringBuilder stringBuilder = this.embedBuilder.getDescriptionBuilder();
        stringBuilder.append(this.question.getQuestion() + "\n\n");
        this.answers = new ArrayList<>(this.question.getAnswers().keySet());
        Collections.shuffle(this.answers);
        for (int i = 0; i < this.answers.size(); i++) {
            stringBuilder.append((char)('A' + i) + ". " + this.answers.get(i) + "\n");
        }

        MessageEmbed embed = embedBuilder.build();

        MessageCreateBuilder messageBuilder = new MessageCreateBuilder();
        messageBuilder.setEmbeds(embed);
        messageBuilder.setComponents(this.buildButtons(this.answers.size()));

        this.embedMessage = textChannel.sendMessage(messageBuilder.build()).complete();
        this.updateMessage();
    }

    public void updateMessage() {
        StringBuilder remainingPlayers = new StringBuilder();
        remainingPlayers.append("Restantes: ");
        playersAndSelections.forEach((k, v) -> {
            if (v == null) {
                remainingPlayers.append(k.getName() + " ");
            }
        });
        this.embedBuilder.setFooter(remainingPlayers.toString());

        this.embedMessage.editMessageEmbeds(this.embedBuilder.build()).complete();
    }

    public void onButtonInteraction(ButtonInteractionEvent event) {
        this.playersAndSelections.put(event.getUser(), Integer.parseInt(event.getButton().getId().split("_")[1]));
        this.updateMessage();
        if (!this.playersAndSelections.values().contains(null)) {
            this.finish();
        }
    }

    private ActionRow buildButtons(Integer amount) {
        List<Button> buttons = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            char character = (char)('A' + i);
            buttons.add(Button.primary(prefix + "_" + i, "" + character));
        }
        return ActionRow.of(buttons);
    }

    public void finish() {
        this.embedMessage.editMessageComponents().queue();
        this.embedBuilder.setColor(Color.red);

        String answer = "";
        for (Entry<String, Boolean> entry : this.question.getAnswers().entrySet()) {
            if (entry.getValue() == true) {
                answer = entry.getKey();
            }
        }

        StringBuilder stringBuilder = this.embedBuilder.getDescriptionBuilder();
        stringBuilder.delete(0, stringBuilder.length());
        stringBuilder.append(this.question.getQuestion() + "\n\n");
        for (int i = 0; i < this.answers.size(); i++) {
            stringBuilder.append((char)('A' + i) + ". " + this.answers.get(i));
            for (Entry<User, Integer> entry : this.playersAndSelections.entrySet()) {
                if (entry.getValue() == i) {
                    boolean win = answers.get(entry.getValue()) == answer;
                    BotuserRequester.updateUserScore(entry.getKey().getIdLong(), win, this.manager.getClass());
                    if (win) {
                        BotuserRequester.updateUserCurrency(entry.getKey().getIdLong(), 10);
                    }
                    stringBuilder.append(" " + entry.getKey().getAsMention());
                }
            }
            stringBuilder.append("\n");
        }

        this.embedBuilder.setFooter("Resposta: " + answer);

        this.embedMessage.editMessageEmbeds(this.embedBuilder.build()).complete();
        this.manager.endLobby(this);
        this.clearAudio();
    }

    private void clearAudio() {
        if (this.manager instanceof MusicleManager) {
            ((MusicleManager) this.manager).stop();
        }
    }
}
