package com.tesch.api.music.player;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchProvider;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;

import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class MusicEventHandler {

    private AudioPlayer audioPlayer;
    private AudioPlayerManager playerManager;
    private MusicQueue queue;
    private YoutubeSearchProvider youtubeSearch;
    
    public MusicEventHandler(AudioPlayerManager playerManager, MusicQueue queue, YoutubeSearchProvider youtubeSearch) {
        this.playerManager = playerManager;
        this.queue = queue;
        this.youtubeSearch = youtubeSearch;

        this.audioPlayer = playerManager.createPlayer();

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

        TextChannel textChannel = event.getMessage().getChannel().asTextChannel();
        AudioChannel voiceChannel = event.getMember().getVoiceState().getChannel();
        
        AudioManager audioManager = event.getGuild().getAudioManager();
        audioManager.setSendingHandler(new MusicPlayerSendHandler(audioPlayer));
        audioManager.openAudioConnection(voiceChannel);
        audioManager.setSelfDeafened(true);

        MusicResultHandler resultHandler = new MusicResultHandler(textChannel, queue);

        BasicAudioPlaylist songs = (BasicAudioPlaylist) youtubeSearch.loadSearchResult(message, info -> new YoutubeAudioTrack(info, new YoutubeAudioSourceManager()));

        AudioTrack song = songs.getTracks().get(0);
        playerManager.loadItem(song.getInfo().uri, resultHandler);
    }

    public void onVolumeCommand(MessageReceivedEvent event) {
        String[] volume = event.getMessage().getContentRaw().split(" ");
        if (volume.length == 1) {
            event.getChannel().sendMessage("Current volume: " + this.audioPlayer.getVolume()).queue();
        }
        else {
            this.audioPlayer.setVolume(Integer.parseInt(volume[1]));
            event.getChannel().sendMessage("Volume set to: " + this.audioPlayer.getVolume()).queue();
        }
    }

    public void onPauseCommand() {
        this.audioPlayer.setPaused(this.audioPlayer.isPaused() ? false : true);
    }

    public void onDisconnectCommand(MessageReceivedEvent event) {
        event.getGuild().getAudioManager().closeAudioConnection();
        this.onClearCommand(event);
    }

    public void onSkipCommand(MessageReceivedEvent event) {
        this.queue.playNextTrack(true);
    }

    public void onQueueCommand(MessageReceivedEvent event) {
        try {
            StringBuilder queueString = new StringBuilder();
            queueString.append(this.audioPlayer.getPlayingTrack().getInfo().title + "\n");
            this.queue.getPlaylist().stream().map(x -> x.getInfo().title).forEach(x -> queueString.append(x + "\n"));
            event.getChannel().sendMessage("Queue:\n" + queueString).queue();
        }
        catch (NullPointerException e) {
            event.getChannel().sendMessage("Queue empty").queue();
        }
    }

    public void onClearCommand(MessageReceivedEvent event) {
        this.queue.clearPlaylist();
        event.getChannel().sendMessage("Cleared queue").queue();;
    }

    public void onLoopCommand(MessageReceivedEvent event) {
        this.queue.setLoop(!this.queue.getLoop());
        event.getChannel().sendMessage("Loop set to " + this.queue.getLoop()).queue();
    }
}
