package com.tesch.api.music.player;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchProvider;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import com.tesch.api.utils.DiscordUtils;
import com.tesch.api.utils.TaskScheduler;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MusicEventHandler {

    private AudioPlayer audioPlayer;
    private AudioPlayerManager playerManager;
    private MusicQueue queue;
    private YoutubeSearchProvider youtubeSearch;
    private TaskScheduler scheduler;
    private DiscordUtils discordUtils;
    
    public MusicEventHandler(AudioPlayerManager playerManager, MusicQueue queue, YoutubeSearchProvider youtubeSearch) {
        this.playerManager = playerManager;
        this.queue = queue;
        this.youtubeSearch = youtubeSearch;
        this.scheduler = new TaskScheduler();
        this.queue.setScheduler(scheduler);
        this.queue.setMusicManager(this);
        this.discordUtils = new DiscordUtils();

        this.audioPlayer = this.playerManager.createPlayer();

        this.queue.setPlayer(this.audioPlayer);
        this.audioPlayer.addListener(this.queue);
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

    public void onPlayCommand(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw().replace("play ", "");

        discordUtils.buildFromMessageEvent(event);
        
        discordUtils.connectToVoice(new MusicPlayerSendHandler(audioPlayer));

        MusicResultHandler resultHandler = new MusicResultHandler(this.discordUtils.getText(), queue);

        BasicAudioPlaylist songs = (BasicAudioPlaylist) youtubeSearch.loadSearchResult(message, info -> new YoutubeAudioTrack(info, new YoutubeAudioSourceManager()));

        AudioTrack song = songs.getTracks().get(0);
        playerManager.loadItem(song.getInfo().uri, resultHandler);
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
        this.audioPlayer.setPaused(this.audioPlayer.isPaused() ? false : true);
    }

    public void onDisconnectCommand() {
        this.discordUtils.disconnectFromVoice();
        this.onClearCommand();
    }

    public void onSkipCommand() {
        this.queue.playNextTrack(true);
    }

    public void onQueueCommand() {
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
        if (this.audioPlayer.getPlayingTrack() != null) {
            this.discordUtils.sendMessage("Cleared queue");
        }
        this.queue.clearPlaylist();
    }

    public void onLoopCommand() {
        this.queue.setLoop(!this.queue.getLoop());
        this.discordUtils.sendMessage("Loop set to " + this.queue.getLoop());
    }
}
