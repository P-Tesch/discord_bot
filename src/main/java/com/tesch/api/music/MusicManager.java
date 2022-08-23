package com.tesch.api.music;

import java.net.MalformedURLException;
import java.net.URL;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchProvider;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import com.tesch.api.utils.DiscordUtils;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MusicManager {

    private AudioPlayer audioPlayer;
    private AudioPlayerManager playerManager;
    private MusicQueue queue;
    private YoutubeSearchProvider youtubeSearch;
    private DiscordUtils discordUtils;
    private boolean musicleMode;
    
    public MusicManager(AudioPlayerManager playerManager, MusicQueue queue, YoutubeSearchProvider youtubeSearch) {
        this.playerManager = playerManager;
        this.queue = queue;
        this.youtubeSearch = youtubeSearch;
        this.discordUtils = new DiscordUtils();

        this.audioPlayer = this.playerManager.createPlayer();

        this.queue.setPlayer(this.audioPlayer);
        this.audioPlayer.addListener(this.queue);
        this.musicleMode = false;
    }

    public AudioPlayer getAudioPlayer() {
        return this.audioPlayer;
    }

    public AudioPlayerManager getPlayerManager() {
        return this.playerManager;
    }

    public MusicQueue getQueue() {
        return this.queue;
    }

    public DiscordUtils getDiscordUtils() {
        return this.discordUtils;
    }

    public void setMusicleMode(boolean mode) {
        this.musicleMode = mode;
    }

    public void onPlayCommand(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw().replace("play ", "");

        discordUtils.buildFromMessageEvent(event);
        if (this.musicleMode) {
            discordUtils.sendMessage("Wait for musicle finish");
            return;
        }
        discordUtils.connectToVoice(new MusicPlayerSendHandler(audioPlayer));

        if (this.isUrl(message)) {
            this.playFromUrl(message);
        }
        else {
            this.playFromSearch(message);
        }
    }

    public void onVolumeCommand(MessageReceivedEvent event) {
        String[] volume = event.getMessage().getContentRaw().split(" ");
        if (volume.length == 1) {
            this.discordUtils.sendMessage("Current volume: " + this.audioPlayer.getVolume());
        }
        else {
            this.audioPlayer.setVolume(Integer.parseInt(volume[1]));
            this.discordUtils.sendMessage("Volume set to: " + this.audioPlayer.getVolume());
        }
    }

    public void onPauseCommand() {
        if (this.musicleMode) {
            discordUtils.sendMessage("Wait for musicle finish");
            return;
        }
        this.audioPlayer.setPaused(this.audioPlayer.isPaused() ? false : true);
    }

    public void onDisconnectCommand() {
        this.discordUtils.disconnectFromVoice();
        this.onClearCommand();
    }

    public void onSkipCommand() {
        if (this.musicleMode) {
            discordUtils.sendMessage("Wait for musicle finish");
            return;
        }
        this.queue.playNextTrack(true);
    }

    public void onQueueCommand() {
        if (this.musicleMode) {
            discordUtils.sendMessage("Wait for musicle finish");
            return;
        }
        try {
            StringBuilder queueString = new StringBuilder();
            queueString.append(this.audioPlayer.getPlayingTrack().getInfo().title + "\n");
            this.queue.getPlaylist().stream().map(x -> x.getInfo().title).forEach(x -> queueString.append(x + "\n"));
            this.discordUtils.sendMessage("Queue:\n" + queueString);
        }
        catch (NullPointerException e) {
            this.discordUtils.sendMessage("Queue empty");
        }
    }

    public void onClearCommand() {
        if (this.musicleMode) {
            discordUtils.sendMessage("Wait for musicle finish");
            return;
        }
        if (this.audioPlayer.getPlayingTrack() != null) {
            this.discordUtils.sendMessage("Cleared queue");
        }
        this.queue.clearPlaylist();
    }

    public void onLoopCommand() {
        if (this.musicleMode) {
            discordUtils.sendMessage("Wait for musicle finish");
            return;
        }
        this.queue.setLoop(!this.queue.getLoop());
        this.discordUtils.sendMessage("Loop set to " + this.queue.getLoop());
    }

    public void onShuffleCommand() {
        if (this.musicleMode) {
            discordUtils.sendMessage("Wait for musicle finish");
            return;
        }
        this.queue.shufflePlaylist();
        this.discordUtils.sendMessage("Queue Suffled");
    }

    private boolean isUrl(String test) {
        try {
            new URL(test);
            return true;
        } 
        catch (MalformedURLException e) {
            return false;
        }
    }

    private void playFromUrl(String url) {
        MusicResultHandler resultHandler = new MusicResultHandler(this.discordUtils.getText(), queue);
        playerManager.loadItem(url, resultHandler);
    }

    private void playFromSearch(String search) {
        MusicResultHandler resultHandler = new MusicResultHandler(this.discordUtils.getText(), queue);

        BasicAudioPlaylist songs = (BasicAudioPlaylist) youtubeSearch.loadSearchResult(search, info -> new YoutubeAudioTrack(info, new YoutubeAudioSourceManager()));
        AudioTrack song = songs.getTracks().get(0);
        
        playerManager.loadItem(song.getInfo().uri, resultHandler);
    }
}
