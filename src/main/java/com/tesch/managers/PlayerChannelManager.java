package com.tesch.managers;

import java.util.concurrent.TimeUnit;
import java.awt.Color;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchProvider;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.tesch.music.MusicPlayerChannelResultHandler;
import com.tesch.music.MusicPlayerSendHandler;
import com.tesch.music.MusicQueue;
import com.tesch.utils.DiscordUtils;
import com.tesch.utils.TaskScheduler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PlayerChannelManager extends MusicManager {

    public static final String CHANNEL_NAME = "musica";

    private TaskScheduler scheduler;
    private TextChannel text;
    private Message embedMessage;
    private EmbedBuilder embedBuilder;

    public PlayerChannelManager(MusicManager musicManager) {
        super(musicManager.getPlayerManager(), musicManager.getQueue(), musicManager.getYoutubeSearchProvider(), musicManager.getGuild());
        this.genericConstructor();
    }

    public PlayerChannelManager(AudioPlayerManager playerManager, MusicQueue queue, YoutubeSearchProvider youtubeSearch, Guild guild) {
        super(playerManager, queue, youtubeSearch, guild);
        this.genericConstructor();
    }

    private void genericConstructor() {
        this.scheduler = new TaskScheduler();
        this.text = this.getGuild().getTextChannelsByName(CHANNEL_NAME, true).get(0);
        this.text.deleteMessages(MessageHistory.getHistoryFromBeginning(this.text).complete().getRetrievedHistory()).queue();
        this.getQueue().setPlayerChannelManager(this);
        this.buildPlayer();
    }

    private void buildPlayer() {
        this.embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Music Player");
        embedBuilder.setColor(new Color(255, 51, 153));
        embedBuilder.setImage("https://i.ytimg.com/vi/p3orUcVWn6Q/maxresdefault.jpg");
        MessageEmbed embed = embedBuilder.build();

        MessageBuilder messageBuilder = new MessageBuilder();
        messageBuilder.setEmbeds(embed);

        this.embedMessage = text.sendMessage(messageBuilder.build()).complete();
    }

    private void updatePlayerImage(AudioTrack playing) {
        String videoId = playing.getInfo().uri.split("=")[1];
        String thumbURL = "http://img.youtube.com/vi/" + videoId + "/0.jpg";
        this.embedBuilder.setImage(thumbURL);

        MessageBuilder messageBuilder = new MessageBuilder();
        messageBuilder.setEmbeds(this.embedBuilder.build());
        embedMessage.editMessage(messageBuilder.build()).queue();
    }

    public void updatePlayer(AudioTrack playing) {
        updatePlayerImage(playing);
    }
    
    @Override
    public void onPlayCommand(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        event.getMessage().delete().queue();

        if (this.getMusicleMode()) {
            Message waitMusicle = DiscordUtils.sendMessage("Wait for musicle finish", text);
            waitMusicle.delete().queueAfter(5, TimeUnit.SECONDS);
            return;
        }
        DiscordUtils.connectToVoice(new MusicPlayerSendHandler(this.getAudioPlayer()), this.getGuild(), event.getMember().getVoiceState().getChannel());

        if (this.isUrl(message)) {
            this.playFromUrl(message, new MusicPlayerChannelResultHandler(text, this.getQueue(), this.scheduler));
        }
        else {
            this.playFromSearch(message, new MusicPlayerChannelResultHandler(text, this.getQueue(), this.scheduler));
        }
    }
}
