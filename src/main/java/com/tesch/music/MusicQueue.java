package com.tesch.music;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingDeque;
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

    private Deque<AudioTrack> playlist;
    private AudioPlayer player;
    private boolean loop;
    private PlayerChannelManager playerChannelManager;
    private Queue<AudioTrack> unshuffledPlaylist;
    private Stack<AudioTrack> playedPlaylist;

    public MusicQueue() {
        this.playlist = new LinkedBlockingDeque<>();
        this.unshuffledPlaylist = new LinkedBlockingQueue<>();
        this.playedPlaylist = new Stack<>();
        this.loop = false;
    }

    public boolean getLoop() {
        return this.loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
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

    public PlayerChannelManager getPlayerChannelManager() {
        return this.playerChannelManager;
    }

    public boolean isShuffled() {
        return !this.unshuffledPlaylist.isEmpty();
    }

    public void shufflePlaylist() {
        if (this.unshuffledPlaylist.isEmpty()) {
            List<AudioTrack> shuffledPlaylist = new ArrayList<>();
            this.unshuffledPlaylist.addAll(this.playlist);
            playlist.stream().forEach(track -> shuffledPlaylist.add(MiscUtils.randomInt(0, shuffledPlaylist.size()), track));
            this.playlist = new LinkedBlockingDeque<>(shuffledPlaylist);
        }
        else {
            this.playlist.clear();
            this.playlist.addAll(this.unshuffledPlaylist);
            this.unshuffledPlaylist.clear();
        }
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
            if (this.player.getPlayingTrack() == null) {
                this.playNextTrack(false);
            }
        }
    }
    
    public void playNextTrack(Boolean skip) {
        if (loop && skip && this.player.getPlayingTrack() != null) {
            this.addToPlaylist(this.player.getPlayingTrack().makeClone());
        }

        if (this.playlist.isEmpty()) {
            if (skip) {
                player.stopTrack();
            }
            this.playerChannelManager.updatePlayer();
            return;
        }

        if (player.startTrack(playlist.peek().makeClone(), !skip)) {
            AudioTrack toPlay = playlist.poll();
            if (playerChannelManager != null) {
                playerChannelManager.updatePlayer(null, null);
            }
            else {
                this.unshuffledPlaylist.remove(toPlay);
            }
        }
    }

    public void playPreviousTrack() {
        if (this.playedPlaylist.isEmpty()) {
            return;
        }
        this.playlist.addFirst(this.player.getPlayingTrack());
        AudioTrack toPlay = this.playedPlaylist.pop();
        if (player.startTrack(toPlay.makeClone(), false)) {
            if (this.playerChannelManager != null) {
                this.playerChannelManager.updatePlayer(null, null);
            }
            if (loop) {
                this.playlist.remove(toPlay);
            }
        }
    }

    public void clearPlayedPlaylist() {
        this.playedPlaylist.clear();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (!this.playlist.contains(track)) {
            this.playedPlaylist.push(track);
        }
        if (endReason.mayStartNext) {
            if (this.loop) {
                this.addToPlaylist(track.makeClone());
            }
            this.playNextTrack(false);
        }
    }
}
