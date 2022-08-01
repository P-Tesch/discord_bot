package com.tesch.api;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class SongQueue extends AudioEventAdapter{

    private Queue<AudioTrack> playlist;

    public SongQueue() {
        playlist = new LinkedBlockingQueue<>();
    }

    public void addToPlaylist(AudioTrack track) {
        playlist.add(track);
    }
    
    public AudioTrack getNextTrack() {
        return playlist.poll();
    }
}
