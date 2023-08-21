package com.tesch.managers;

import java.util.Arrays;
import java.util.Collection;
import java.awt.Color;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.tesch.exceptions.MusicleException;
import com.tesch.music.MusicPlayer;
import com.tesch.music.MusicPlayerChannelResultHandler;
import com.tesch.utils.TaskScheduler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

public class PlayerChannelManager extends MusicManager {

    public static final String CHANNEL_NAME = "musica";

    private TaskScheduler scheduler;
    private TextChannel text;
    private Message embedMessage;
    private EmbedBuilder embedBuilder;
    private boolean queueMode;
    private Integer queuePage;

    public PlayerChannelManager(MusicPlayer musicPlayer, Guild guild) {
        super(musicPlayer, guild);
        this.genericConstructor();
    }

    private void genericConstructor() {
        this.queueMode = false;
        this.scheduler = new TaskScheduler();
        this.text = this.getGuild().getTextChannelsByName(CHANNEL_NAME, true).get(0);
        MessageHistory.getHistoryFromBeginning(this.text).complete().getRetrievedHistory().forEach(msg -> msg.delete().queue());
        this.getMusicPlayer().getQueue().setPlayerChannelManager(this);
        this.buildPlayer();
    }

    private void buildPlayer() {
        this.embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Music Player");
        embedBuilder.setColor(new Color(255, 51, 153));
        MessageEmbed embed = embedBuilder.build();

        MessageCreateBuilder messageBuilder = new MessageCreateBuilder();
        messageBuilder.setEmbeds(embed);

        this.embedMessage = text.sendMessage(messageBuilder.build()).complete();
        this.updatePlayer();
    }

    private Collection<ActionRow> buildButtons() {
        return Arrays.asList(
            ActionRow.of(Arrays.asList(
                !this.getMusicPlayer().getAudioPlayer().isPaused() ? 
                    Button.success("playerchannel_playPause", "⏸️") : 
                    Button.danger("playerchannel_playPause", "▶️"),
                Button.secondary("playerchannel_stop", "⏹️"),
                Button.secondary("playerchannel_previous", "⏮️"),
                Button.secondary("playerchannel_next", "⏭️"),
                Button.primary("playerchannel_queue", "🔼")
            )),
            !this.queueMode ?
                ActionRow.of(Arrays.asList(
                    !this.getMusicPlayer().getQueue().isShuffled() ?
                        Button.danger("playerchannel_shuffle", "🔀") :
                        Button.success("playerchannel_shuffle", "🔀"),
                    !this.getMusicPlayer().getQueue().getLoop() ?
                        Button.danger("playerchannel_loop", "🔁") :
                        Button.success("playerchannel_loop", "🔁")
                )) :
                ActionRow.of(Arrays.asList(
                    !this.getMusicPlayer().getQueue().isShuffled() ?
                        Button.danger("playerchannel_shuffle", "🔀") :
                        Button.success("playerchannel_shuffle", "🔀"),
                    !this.getMusicPlayer().getQueue().getLoop() ?
                        Button.danger("playerchannel_loop", "🔁") :
                        Button.success("playerchannel_loop", "🔁"),
                    Button.secondary("playerchannel_queuePrevious", "⏪"),
                    Button.secondary("playerchannel_queueNext", "⏩")
                ))
        );
    }

    private void updatePlayerImage(AudioTrack playing) {
        if (playing == null) {
            embedBuilder.setImage("https://i.ytimg.com/vi/p3orUcVWn6Q/maxresdefault.jpg");
            this.embedBuilder.setDescription("");
            return;
        }

        String videoId = playing.getInfo().uri.split("=")[1];
        String thumbURL = "http://img.youtube.com/vi/" + videoId + "/0.jpg";
        this.embedBuilder.setImage(thumbURL);
        this.embedBuilder.setDescription(playing.getInfo().title);
    }

    public void updatePlayer() {
        if (this.getMusicPlayer().getMusicleMode()) {
            return;
        }
        if (this.queueMode) {
            this.updatePlayerAsQueue(this.queuePage);
            return;
        }
        AudioTrack playing = this.getMusicPlayer().getAudioPlayer().getPlayingTrack();
        updatePlayerImage(playing);

        MessageEditBuilder messageBuilder = new MessageEditBuilder();
        messageBuilder.setEmbeds(this.embedBuilder.build());
        messageBuilder.setComponents(this.buildButtons());
        embedMessage.editMessage(messageBuilder.build()).queue(); 
    }

    public void updatePlayer(String footer, Integer footerTime) {
        if (footerTime == null) {
            this.setFooter(footer);
        }
        else {
            this.setFooter(footer, footerTime);
        }
        this.updatePlayer();
    }

    private void updatePlayerAsQueue(Integer page) {
        String queueString = this.getMusicPlayer().queue(page);
        if (queueString == null) {
            this.queueMode = false;
            this.updatePlayer();
            return;
        }
        this.embedBuilder.setImage(null);
        this.embedBuilder.setDescription(queueString);

        MessageEditBuilder messageBuilder = new MessageEditBuilder();
        messageBuilder.setEmbeds(this.embedBuilder.build());
        messageBuilder.setComponents(this.buildButtons());
        embedMessage.editMessage(messageBuilder.build()).queue(); 
    }

    public void setFooter(String footer) {
        this.embedBuilder.setFooter(footer);
    }

    public void setFooter(String footer, Integer time) {
        this.setFooter(footer);
        scheduler.schedule(() -> this.updatePlayer(null, null), time);
    }
    
    public void onPlayCommand(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        event.getMessage().delete().queue();

        try {
            this.getMusicPlayer().play(event.getMember().getVoiceState().getChannel(), message, new MusicPlayerChannelResultHandler(this.getMusicPlayer().getQueue(), this));
        }
        catch (MusicleException e) {
            this.setFooter(e.getMessage(), 5);
        }
    }

    public void onButtonInteraction(ButtonInteractionEvent event) {
        String command = event.getButton().getId().split("_")[1];
        event.deferEdit().queue();
        switch (command) {
            case "playPause":
                this.onPauseCommand();
                break;
            case "next":
                this.onSkipCommand();
                break;
            case "previous":
                this.OnPreviousCommand();
                break;
            case "stop":
                this.onStopCommand();
                break;
            case "loop":
                this.onLoopCommand();
                break;
            case "shuffle":
                this.onShuffleCommand();
                break;
            case "queue":
                this.onQueueCommand();
                break;
            case "queueNext":
                this.onQueueNextCommand();
                break;
            case "queuePrevious":
                this.onQueuePreviousCommand();
                break;
        }
    }

    private void onPauseCommand() {
        try {
            this.getMusicPlayer().pause();
            this.updatePlayer();
        }
        catch (MusicleException e) {
            this.updatePlayer(e.getMessage(), 5);
        }
    }

    private void onSkipCommand() {
        try {
            this.getMusicPlayer().skip();
            this.updatePlayer();
        }
        catch (MusicleException e) {
            this.updatePlayer(e.getMessage(), 5);
        }
    }

    private void onStopCommand() {
        try {
            if (this.getMusicPlayer().getQueue().getPlaylist().isEmpty() && this.getMusicPlayer().getAudioPlayer().getPlayingTrack() == null) {
                this.getMusicPlayer().disconnect();
                this.updatePlayer();
            }
            else {
                this.getMusicPlayer().clear();
                this.updatePlayer();
            }
        }
        catch (MusicleException e) {
            this.updatePlayer(e.getMessage(), 5);
        }
    }

    private void onLoopCommand() {
        try {
            this.getMusicPlayer().loop();
            this.updatePlayer();
        }
        catch (MusicleException e) {
            this.updatePlayer(e.getMessage(), 5);
        }
    }

    private void onShuffleCommand() {
        try {
            this.getMusicPlayer().shuffle();
            this.updatePlayer();
        }
        catch(MusicleException e) {
            this.updatePlayer(e.getMessage(), 5);
        }
    }

    private void onQueueNextCommand() {
        this.queuePage++;
        this.updatePlayer();
    }

    private void onQueuePreviousCommand() {
        this.queuePage--;
        this.updatePlayer();
    }

    private void onQueueCommand() {
        this.queueMode = !this.queueMode;
        this.queuePage = 1;
        this.updatePlayer();
    }

    private void OnPreviousCommand() {
        this.getMusicPlayer().previous();
    }
}
