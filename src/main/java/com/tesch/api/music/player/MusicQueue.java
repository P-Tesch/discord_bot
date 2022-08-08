package com.tesch.api.music.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.tesch.api.utils.MiscUtils;
import com.tesch.api.utils.TaskScheduler;

public class MusicQueue extends AudioEventAdapter{

    private Queue<AudioTrack> playlist;
    private AudioPlayer player;
    private boolean loop;
    private TaskScheduler scheduler;
    private MusicEventHandler musicManager;

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

    public void setScheduler(TaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void setPlayer(AudioPlayer player) {
        this.player = player;
    }

    public void setMusicManager(MusicEventHandler musicManager) {
        this.musicManager = musicManager;
    }

    public List<AudioTrack> getPlaylist() {
        return playlist.stream().collect(Collectors.toList());
    }

    public void shufflePlaylist() {
        List<AudioTrack> shuffledPlaylist = new ArrayList<>();
        playlist.stream().forEach(track -> shuffledPlaylist.add(MiscUtils.randomInt(0, shuffledPlaylist.size()), track));
        this.playlist = new LinkedBlockingQueue<>(shuffledPlaylist);
    }

    public void clearPlaylist() {
        playlist.clear();
        player.stopTrack();
        this.scheduler.schedule(() -> this.musicManager.onDisconnectCommand(), 120);
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
            this.scheduler.cancelAll();
            if (loop) {
                playlist.offer(played);
            }
        }
        if (this.playlist.isEmpty() && this.player.getPlayingTrack() == null) {
            this.scheduler.schedule(() -> this.musicManager.onDisconnectCommand(), 120);
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            this.playNextTrack(false);
        }
    }
}
