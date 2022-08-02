package com.tesch.api.music.player;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

public class MusicQueue extends AudioEventAdapter{

    private Queue<AudioTrack> playlist;
    private AudioPlayer player;
    private boolean loop;

    public MusicQueue() {
        playlist = new LinkedBlockingQueue<>();
        this.loop = false;
    }

    public boolean getLoop() {
        return this.loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
        if (this.loop) {
            this.addToPlaylist(player.getPlayingTrack());
        }
    }

    public void setPlayer(AudioPlayer player) {
        this.player = player;
    }

    public List<AudioTrack> getPlaylist() {
        return playlist.stream().toList();
    }

    public void clearPlaylist() {
        playlist.clear();
        player.stopTrack();
    }

    public void addToPlaylist(AudioPlaylist playlist) {
        playlist.getTracks().stream().forEach(this::addToPlaylist);
    }

    public void addToPlaylist(AudioTrack track) {
        if (!this.playlist.contains(track)) {
            playlist.offer(track);
            this.playNextTrack(false);
        }
    }
    
    public void playNextTrack(Boolean skip) {
        if (player.startTrack(playlist.peek(), !skip)) {
            AudioTrack played = playlist.poll();
            if (loop) {
                playlist.offer(played);
            }
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            this.playNextTrack(false);
        }
    }
}
