package com.tesch.api.music.musicle;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.tesch.api.music.musicle.enums.MusicGenres;
import com.tesch.api.music.player.MusicManager;
import com.tesch.api.music.player.MusicPlayerSendHandler;
import com.tesch.api.utils.DiscordUtils;
import com.tesch.api.utils.MiscUtils;
import com.tesch.api.utils.TaskScheduler;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class MusicleManager {

    private MusicManager musicEventHandler;
    private TaskScheduler scheduler;
    private DiscordUtils discordUtils;
    private boolean titleMode;
    private boolean startMode;
    private StringBuilder stringBuilder;
    private User player;
    private String[] answers;
    private Integer answerIndex;
    private String answerName;
    private Map<User, MusicleScore> playerScore;

    public MusicleManager(MusicManager musicEventHandler) {
        this.musicEventHandler = musicEventHandler;
        this.discordUtils = this.musicEventHandler.getDiscordUtils();
        this.scheduler = new TaskScheduler();
        this.titleMode = false;
        this.startMode = false;
        this.player = null;
        this.answerIndex = null;
        this.answerName = null;
        this.playerScore = new HashMap<>();
    }

    public synchronized void onMusicleCommand(MessageReceivedEvent event) throws InterruptedException {
        this.discordUtils.buildFromMessageEvent(event);
        String[] message = event.getMessage().getContentRaw().split(" ");
        if (message.length == 1) {
            discordUtils.sendMessage("Must input a music genre. Possible genres:\n" + this.getPossibleGenres().toLowerCase());
            return;
        }
        if (message[1].equals("mode")) {
            this.changeTitleMode();
            discordUtils.sendMessage(this.titleMode ? "Set to title mode" : "Set to author mode");
            return;
        }
        if (message[1].equals("start")) {
            this.changeStartMode();
            discordUtils.sendMessage(this.startMode ? "Set to start mode" : "Set to random mode");
            return;
        }
        if (message[1].equals("score")) {
            discordUtils.sendMessage("Scoreboard:\n" + this.getScoreboard());
            return;
        }
        try {
            MusicGenres.valueOf(message[1].toUpperCase());
        }
        catch (IllegalArgumentException e) {
            discordUtils.sendMessage("Invalid music genre. Possible genres:\n" + this.getPossibleGenres());
            return;
        }
        if (this.musicEventHandler.getAudioPlayer().getPlayingTrack() != null) {
            discordUtils.sendMessage("Something is already playing, clear the queue to play");
            return;
        }

        this.scheduler.cancelAll();

        String url = null;
        for (MusicGenres genre : MusicGenres.values()) {
            if (genre == MusicGenres.valueOf(message[1].toUpperCase())) {
                url = genre.getUrl();
            }
        }
        this.player = event.getAuthor();
        this.setupPlayerScore();
        
        discordUtils.connectToVoice(new MusicPlayerSendHandler(this.musicEventHandler.getAudioPlayer()));

        MusicleResultHandler resultHandler = new MusicleResultHandler(event.getChannel().asTextChannel(), musicEventHandler.getQueue(), this);
        musicEventHandler.getPlayerManager().loadItem(url, resultHandler);
        this.wait();
        if (!this.startMode) {
            AudioTrack track = this.musicEventHandler.getAudioPlayer().getPlayingTrack();
            track.setPosition(MiscUtils.randomInt(0, (int) (3 * track.getDuration() / 4))); 
        }

        event.getChannel().sendMessage(this.stringBuilder).setActionRow(
            Button.primary("1", Emoji.fromUnicode("1️⃣")),
            Button.primary("2", Emoji.fromUnicode("2️⃣")),
            Button.primary("3", Emoji.fromUnicode("3️⃣")),
            Button.primary("4", Emoji.fromUnicode("4️⃣")),
            Button.primary("5", Emoji.fromUnicode("5️⃣"))
            )
        .queue();
        this.timeLimit();
    }

    public synchronized void generateAnswers(List<String> songAuthors, List<String> songTitles) {
        Set<String> possibleAnswers = new HashSet<>(this.titleMode ? songTitles : songAuthors);
        this.answerName = this.titleMode ? musicEventHandler.getAudioPlayer().getPlayingTrack().getInfo().title : musicEventHandler.getAudioPlayer().getPlayingTrack().getInfo().author;
        possibleAnswers.remove(this.answerName);
        this.answers = new String[5];
        for (int i = 0; i < 5; i++) {
            this.answers[i] = possibleAnswers.stream().collect(Collectors.toList()).get(MiscUtils.randomInt(0, possibleAnswers.size()));
            possibleAnswers.remove(answers[i]);
        }

        this.answerIndex = MiscUtils.randomInt(0, answers.length - 1);
        this.answers[this.answerIndex] = this.answerName;
        this.stringBuilder = new StringBuilder();
        for (int i = 0; i < this.answers.length; i++) {
            this.answers[i] = (i + 1 + ". " + this.answers[i]);
            stringBuilder.append(this.answers[i] + "\n");
        }
        this.notify();
    }

    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (this.player == null) {
            event.editMessage("Time's up").queue(msg -> event.getMessage().editMessageEmbeds().setActionRows().queue());
        }
        if (event.getUser() == this.player) {
            int selected = Integer.parseInt(event.getButton().getId());
            StringBuilder builder = new StringBuilder();
            if (selected == this.answerIndex + 1) {
                this.answers[this.answerIndex] = this.answers[this.answerIndex] + " ✅";
                Arrays.asList(this.answers).stream().forEach(x -> builder.append(x + "\n"));
                this.playerScore.get(this.player).addWin();
            }
            else {
                this.answers[this.answerIndex] = this.answers[this.answerIndex] + " ☑️";
                this.answers[selected - 1] = this.answers[selected - 1] + " ❌";
                Arrays.asList(this.answers).stream().forEach(x -> builder.append(x + "\n"));
                this.playerScore.get(this.player).addLoss();
            }
            builder.append("\nTitle: " + this.musicEventHandler.getAudioPlayer().getPlayingTrack().getInfo().title);
            event.editMessage(builder.toString()).queue(msg -> event.getMessage().editMessageEmbeds().setActionRows().queue());
            this.stop();
        }
    }

    private void setupPlayerScore() {
        if (!this.playerScore.keySet().contains(this.player)) {
            this.playerScore.put(this.player, new MusicleScore());
        }
    }

    private void stop() {
        this.stringBuilder = null;
        this.answerIndex = null;
        this.answerName = null;
        this.player = null;
        this.answers = null;
        Runnable disconnect = () -> this.musicEventHandler.onDisconnectCommand();;
        this.scheduler.schedule(disconnect, 60);
        this.musicEventHandler.getQueue().clearPlaylist();
    }

    private void changeTitleMode() {
        this.titleMode = !this.titleMode;
    }

    private void changeStartMode() {
        this.startMode = !this.startMode;
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

    private void timeLimit() {
        this.scheduler.schedule(() -> this.stop(), 45);
    }
}
