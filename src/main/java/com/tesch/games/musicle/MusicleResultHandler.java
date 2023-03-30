package com.tesch.games.musicle;

import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.tesch.managers.MusicleManager;
import com.tesch.music.MusicQueue;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class MusicleResultHandler implements AudioLoadResultHandler {

    private TextChannel textChannel;
    private MusicQueue queue;
    private MusicleManager musicleManager;
    private List<AudioTrack> songs;

    public MusicleResultHandler(TextChannel textChannel, MusicQueue queue, MusicleManager musicleManager) {
        this.textChannel = textChannel;
        this.queue = queue;
        this.musicleManager = musicleManager;
    }

    @Override
    public void loadFailed(FriendlyException e) {
        textChannel.sendMessage("Error loading track: " + e.getMessage()).queue();
    }

    @Override
    public void noMatches() {
        textChannel.sendMessage("Error loading track: Track not found").queue();
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        this.songs = playlist.getTracks();
        AudioTrack track = this.songs.remove((int) Math.floor(Math.random()*(this.songs.size() - 1)));
        queue.addToPlaylist(track);
        this.musicleManager.generateAnswers(this.songs);
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        throw new IllegalStateException("MusicleResultHandler needs a playlist");
    }
    
}
