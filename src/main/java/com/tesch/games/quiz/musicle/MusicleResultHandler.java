package com.tesch.games.quiz.musicle;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.tesch.music.MusicQueue;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class MusicleResultHandler implements AudioLoadResultHandler {

    private TextChannel textChannel;
    private MusicQueue queue;

    public MusicleResultHandler(TextChannel textChannel, MusicQueue queue) {
        this.textChannel = textChannel;
        this.queue = queue;
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
        throw new IllegalStateException("MusicleResultHandler can't handle a playlist");
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        queue.addToPlaylist(track);
    }
    
}
