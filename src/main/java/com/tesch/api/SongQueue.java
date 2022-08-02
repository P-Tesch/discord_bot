package com.tesch.api;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

public class SongQueue extends AudioEventAdapter{

    private Queue<AudioTrack> playlist;
    private AudioPlayer player;

    public SongQueue() {
        playlist = new LinkedBlockingQueue<>();
    }

    public void setPlayer(AudioPlayer player) {
        this.player = player;
    }

    public void addToPlaylist(AudioTrack track) {
        playlist.offer(track);
        this.playNextTrack(false);
    }
    
    public void playNextTrack(Boolean skip) {
        if (player.startTrack(playlist.peek(), !skip)) {
            playlist.poll();
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            this.playNextTrack(false);
        }
    }
}
