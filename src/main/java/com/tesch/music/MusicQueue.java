package com.tesch.music;

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
import com.tesch.managers.PlayerChannelManager;
import com.tesch.utils.MiscUtils;

public class MusicQueue extends AudioEventAdapter{

    private Queue<AudioTrack> playlist;
    private AudioPlayer player;
    private boolean loop;
    private PlayerChannelManager playerChannelManager;

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
        return playlist.stream().collect(Collectors.toList());
    }

    public void setPlayerChannelManager(PlayerChannelManager playerChannelManager) {
        this.playerChannelManager = playerChannelManager;
    }

    public void shufflePlaylist() {
        List<AudioTrack> shuffledPlaylist = new ArrayList<>();
        playlist.stream().forEach(track -> shuffledPlaylist.add(MiscUtils.randomInt(0, shuffledPlaylist.size()), track));
        this.playlist = new LinkedBlockingQueue<>(shuffledPlaylist);
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
        if (this.playlist.isEmpty()) {
            if (skip) {
                player.stopTrack();
            }
            return;
        }
        if (player.startTrack(playlist.peek().makeClone(), !skip)) {
            AudioTrack toPlay = playlist.poll();
            if (playerChannelManager != null) {
                playerChannelManager.updatePlayer(null, null);
            }
            if (loop) {
                playlist.offer(toPlay);
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
