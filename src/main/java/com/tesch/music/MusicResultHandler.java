package com.tesch.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.entities.TextChannel;

public class MusicResultHandler implements AudioLoadResultHandler {

    private TextChannel textChannel;
    private MusicQueue queue;

    public MusicResultHandler(TextChannel textChannel, MusicQueue queue) {
        this.textChannel = textChannel;
        this.queue = queue;
    }

    @Override
    public void loadFailed(FriendlyException e) {
        textChannel.sendMessage("Error loading track: " + e.getMessage()).queue();
    }

    @Override
    public void noMatches() {
        textChannel.sendMessage("No match for track").queue();
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        queue.addToPlaylist(playlist);
        textChannel.sendMessage("Playlist loaded").queue();
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        queue.addToPlaylist(track);
        textChannel.sendMessage("Track loaded " + track.getInfo().title).queue();
    }
    
}
