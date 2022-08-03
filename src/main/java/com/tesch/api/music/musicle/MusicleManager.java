package com.tesch.api.music.musicle;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.tesch.api.music.musicle.enums.MusicGenres;
import com.tesch.api.music.player.MusicEventHandler;
import com.tesch.api.music.player.MusicPlayerSendHandler;

import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.managers.AudioManager;

public class MusicleManager {

    private MusicEventHandler musicEventHandler;
    private boolean titleMode;
    private StringBuilder stringBuilder;
    private User player;
    private Integer answerIndex;
    private String answerName;
    private Map<User, MusicleScore> playerScore;

    public MusicleManager(MusicEventHandler musicEventHandler) {
        this.musicEventHandler = musicEventHandler;
        this.titleMode = false;
        this.player = null;
        this.answerIndex = null;
        this.answerName = null;
        this.playerScore = new HashMap<>();
    }

    public synchronized void onMusicleCommand(MessageReceivedEvent event) throws InterruptedException {
        TextChannel textChannel = event.getChannel().asTextChannel();
        String[] message = event.getMessage().getContentRaw().split(" ");
        if (message.length == 1) {
            textChannel.sendMessage("Must input a music genre. Possible genres:\n" + this.getPossibleGenres()).queue();
            return;
        }
        if (message[1].equals("mode")) {
            this.changeMode();
            textChannel.sendMessage(this.titleMode ? "Set to title mode" : "Set to author mode").queue();;
            return;
        }
        if (message[1].equals("score")) {
            textChannel.sendMessage("Scoreboard:\n" + this.getScoreboard()).queue();;
            return;
        }
        if (!Arrays.asList(MusicGenres.values()).contains(MusicGenres.valueOf(message[1].toUpperCase()))) {
            textChannel.sendMessage("Invalid music genre. Possible genres:\n" + this.getPossibleGenres()).queue();
            return;
        }
        if (this.musicEventHandler.getAudioPlayer().getPlayingTrack() != null) {
            textChannel.sendMessage("Something is already playing, clear the queue to play").queue();
            return;
        }
        String url = null;
        for (MusicGenres genre : MusicGenres.values()) {
            if (genre == MusicGenres.valueOf(message[1].toUpperCase())) {
                url = genre.getUrl();
            }
        }
        this.player = event.getAuthor();
        this.setupPlayerScore();
        AudioChannel voiceChannel = event.getMember().getVoiceState().getChannel();
        
        AudioManager audioManager = event.getGuild().getAudioManager();
        audioManager.setSendingHandler(new MusicPlayerSendHandler(this.musicEventHandler.getAudioPlayer()));
        audioManager.openAudioConnection(voiceChannel);

        MusicleResultHandler resultHandler = new MusicleResultHandler(event.getChannel().asTextChannel(), musicEventHandler.getQueue(), this);
        musicEventHandler.getPlayerManager().loadItem(url, resultHandler);
        this.wait();

        textChannel.sendMessage(this.stringBuilder).setActionRow(
            Button.primary("1", Emoji.fromUnicode("1️⃣")),
            Button.primary("2", Emoji.fromUnicode("2️⃣")),
            Button.primary("3", Emoji.fromUnicode("3️⃣")),
            Button.primary("4", Emoji.fromUnicode("4️⃣")),
            Button.primary("5", Emoji.fromUnicode("5️⃣"))
            )
        .queue();;
    }

    public synchronized void generateAnswers(List<String> songAuthors, List<String> songTitles) {
        Set<String> possibleAnswers = new HashSet<>(this.titleMode ? songTitles : songAuthors);
        String[] answers = new String[5];
        for (int i = 0; i < 5; i++) {
            answers[i] = possibleAnswers.stream().collect(Collectors.toList()).get((int) Math.floor(Math.random()*(possibleAnswers.size())));
            possibleAnswers.remove(answers[i]);
        }

        this.answerIndex = (int) Math.floor(Math.random()*(answers.length - 1));
        this.answerName = this.titleMode ? musicEventHandler.getAudioPlayer().getPlayingTrack().getInfo().title : musicEventHandler.getAudioPlayer().getPlayingTrack().getInfo().author;

        answers[this.answerIndex] = this.answerName;

        StringBuilder stringBuilder = new StringBuilder();
        Arrays.asList(answers).stream().forEach(x -> stringBuilder.append(x + "\n"));
        this.stringBuilder = stringBuilder;
        this.notify();
    }

    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getUser() == this.player) {
            if (Integer.parseInt(event.getButton().getId()) == this.answerIndex + 1) {
                this.playerScore.get(this.player).addWin();
                event.editMessage("Correct answer").queue();
            }
            else {
                this.playerScore.get(this.player).addLoss();
                event.editMessage("Wrong answer. Correct answer: " + this.answerName).queue();
            }
            event.getMessage().editMessageEmbeds().setActionRows().queue();
            this.stop(event);
        }
    }

    private void setupPlayerScore() {
        if (!this.playerScore.keySet().contains(this.player)) {
            this.playerScore.put(this.player, new MusicleScore());
        }
    }

    private void stop(ButtonInteractionEvent event) {
        this.stringBuilder = null;
        this.answerIndex = null;
        this.answerName = null;
        this.player = null;
        event.getGuild().getAudioManager().closeAudioConnection();
        this.musicEventHandler.getQueue().clearPlaylist();
    }

    private void changeMode() {
        this.titleMode = !this.titleMode;
    }

    private String getScoreboard() {
        StringBuilder builder = new StringBuilder();
        for (var entry : this.playerScore.entrySet()) {
            builder.append(entry.getKey().getName() + " = " + entry.getValue().getWinCount() + "W / " + entry.getValue().getLossCount() + "L\n");
        }
        return builder.toString();
    }

    private String getPossibleGenres() {
        StringBuilder stringBuilder = new StringBuilder();
        Arrays.asList(MusicGenres.values()).stream().forEach(x -> stringBuilder.append(x + "\n"));
        return stringBuilder.toString();
    }
}