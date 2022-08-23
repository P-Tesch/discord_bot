package com.tesch.api.games.musicle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.tesch.api.games.musicle.enums.MusicGenres;
import com.tesch.api.music.MusicManager;
import com.tesch.api.music.MusicPlayerSendHandler;
import com.tesch.api.utils.DiscordUtils;
import com.tesch.api.utils.MiscUtils;
import com.tesch.api.utils.TaskScheduler;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class MusicleManager {

    private MusicManager musicManager;
    private MusicleLobby lobby;
    private TaskScheduler scheduler;
    private DiscordUtils discordUtils;
    private boolean titleMode;
    private boolean startMode;
    private boolean lobbyMode;
    private StringBuilder stringBuilder;
    private User player;
    private String[] answers;
    private Integer answerIndex;
    private String answerName;
    private Map<User, MusicleScore> playerScore;
    private boolean inGame;

    public MusicleManager(MusicManager musicEventHandler) {
        this.musicManager = musicEventHandler;
        this.lobby = null;
        this.discordUtils = this.musicManager.getDiscordUtils();
        this.scheduler = new TaskScheduler();
        this.titleMode = false;
        this.startMode = false;
        this.lobbyMode = false;
        this.player = null;
        this.answerIndex = null;
        this.answerName = null;
        this.playerScore = new HashMap<>();
        this.inGame = false;
    }

    protected TaskScheduler getScheduler() {
        return this.scheduler;
    }

    protected MusicManager getMusicManager() {
        return this.musicManager;
    }

    protected DiscordUtils getDiscordUtils() {
        return this.discordUtils;
    }

    protected boolean getStartMode() {
        return this.startMode;
    }

    protected StringBuilder getStringBuilder() {
        return this.stringBuilder;
    }

    protected String[] getAnswers() {
        return this.answers;
    }

    protected Integer getAnswerIndex() {
        return this.answerIndex;
    }

    protected String getAnswerName() {
        return this.answerName;
    } 

    protected Map<User, MusicleScore> getPlayerScore() {
        return this.playerScore;
    }

    protected MusicleLobby getMusicleLobby() {
        return this.lobby;
    }

    public boolean isInGame() {
        return this.inGame;
    }

    public void onMusicleCommand(MessageReceivedEvent event) throws InterruptedException {
        this.discordUtils.buildFromMessageEvent(event);
        String[] message = event.getMessage().getContentRaw().split(" ");
        String url = null;
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
        if (this.musicManager.getAudioPlayer().getPlayingTrack() != null) {
            discordUtils.sendMessage("Something is already playing, clear the queue to play");
            return;
        }
        if (message[1].equals("random")) {
            url = MusicGenres.values()[MiscUtils.randomInt(0, MusicGenres.values().length - 1)].getUrl();
        }
        else {
            try {
                url = MusicGenres.valueOf(message[1].toUpperCase()).getUrl();
            }
            catch (IllegalArgumentException e) {
                discordUtils.sendMessage("Invalid music genre. Possible genres:\n" + this.getPossibleGenres());
                return;
            }
        }
        this.inGame = true;

        this.scheduler.cancelAll();
    
        this.musicManager.setMusicleMode(true);
        
        discordUtils.connectToVoice(new MusicPlayerSendHandler(this.musicManager.getAudioPlayer()));

        MusicleResultHandler resultHandler = new MusicleResultHandler(event.getChannel().asTextChannel(), musicManager.getQueue(), this);
        Future<Void> wait = musicManager.getPlayerManager().loadItem(url, resultHandler);

        byte i = 0;
        while (!wait.isDone()) {
            Thread.sleep(500L);
            if (i >= 10) {
               this.discordUtils.sendMessage("Failed");
               this.stop();
                return;
            }
            i++;
        }

        if (!this.startMode) {
            AudioTrack track = this.musicManager.getAudioPlayer().getPlayingTrack();
            track.setPosition(MiscUtils.randomInt(0, (int) (3 * track.getDuration() / 4))); 
        }

        i = 0;
        while (this.stringBuilder == null) {
            Thread.sleep(500L);
            if (i >= 10) {
                this.discordUtils.sendMessage("Failed");
                this.stop();
                 return;
             }
             i++;
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

        if (event.getMessage().getMentions().getUsers().size() > 0) {
            this.lobbyMode = true;
            this.lobby = new MusicleLobby(this, event);
            lobby.setup(url);
            return;
        }

        this.player = event.getAuthor();
        this.setupPlayerScore(this.player);
    }

    protected void generateAnswers(List<AudioTrack> songs) {
        Set<String> possibleAnswers = new HashSet<>();
        if (this.titleMode) {
            songs.forEach(x -> possibleAnswers.add(x.getInfo().title));
        }
        else {
            songs.forEach(x -> possibleAnswers.add(x.getInfo().author));
        }
        this.answerName = this.titleMode ? musicManager.getAudioPlayer().getPlayingTrack().getInfo().title : musicManager.getAudioPlayer().getPlayingTrack().getInfo().author;
        possibleAnswers.remove(this.answerName);
        this.answers = new String[5];
        List<String> possibleAnswersList = new ArrayList<>(possibleAnswers);
        for (int i = 0; i < 5; i++) {
            this.answers[i] = possibleAnswersList.remove((int) MiscUtils.randomInt(0, possibleAnswersList.size()));
        }

        this.answerIndex = MiscUtils.randomInt(0, answers.length - 1);
        this.answers[this.answerIndex] = this.answerName;
        this.stringBuilder = new StringBuilder();
        for (int i = 0; i < this.answers.length; i++) {
            this.answers[i] = (i + 1 + ". " + this.answers[i]);
            stringBuilder.append(this.answers[i] + "\n");
        }
    }

    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (this.lobbyMode) {
            this.lobby.onButtonInteraction(event);
            return;
        }
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
            builder.append("\nTitle: " + this.musicManager.getAudioPlayer().getPlayingTrack().getInfo().title);
            event.editMessage(builder.toString()).queue(msg -> event.getMessage().editMessageEmbeds().setActionRows().queue());
            this.stop();
        }
    }

    protected void setupPlayerScore(User player) {
        if (!this.playerScore.keySet().contains(player)) {
            this.playerScore.put(player, new MusicleScore());
        }
    }

    protected void stop() {
        this.musicManager.setMusicleMode(false);
        this.lobby = null;
        this.stringBuilder = null;
        this.answerIndex = null;
        this.answerName = null;
        this.player = null;
        this.answers = null;
        this.lobbyMode = false;
        this.inGame = false;
        Runnable disconnect = () -> this.musicManager.onDisconnectCommand();
        this.scheduler.schedule(disconnect, 60);
        this.musicManager.getQueue().clearPlaylist();
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
