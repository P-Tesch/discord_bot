package com.tesch.api;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.entities.TextChannel;

public class AudioResultHandler implements AudioLoadResultHandler {

    private TextChannel textChannel;
    private SongQueue queue;
    private AudioPlayer player;

    public AudioResultHandler(TextChannel textChannel, SongQueue queue, AudioPlayer player) {
        this.textChannel = textChannel;
        this.queue = queue;
        this.player = player;
    }

    @Override
    public void loadFailed(FriendlyException e) {
        textChannel.sendMessage("Error loading track: " + e.getMessage()).queue();;
    }

    @Override
    public void noMatches() {
        textChannel.sendMessage("No match for track").queue();;
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        textChannel.sendMessage("Playlist loaded").queue();
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        queue.addToPlaylist(track.makeClone());
        player.playTrack(track);
        textChannel.sendMessage("Track loaded " + track.getInfo().title).queue();;
    }
    
}
