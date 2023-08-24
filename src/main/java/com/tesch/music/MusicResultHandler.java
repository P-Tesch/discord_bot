package com.tesch.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.entities.User;

public abstract class MusicResultHandler implements AudioLoadResultHandler {

    private MusicQueue queue;
    private User lastRequester;

    protected User getLastRequester() {
        return this.lastRequester;
    }

    public MusicQueue getQueue() {
        return this.queue;
    }

    public void setLastRequester(User lastRequester) {
        this.lastRequester = lastRequester;
    }

    public void setQueue(MusicQueue queue) {
        this.queue = queue;
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        playlist.getTracks().forEach(track -> track.setUserData(this.lastRequester));
        queue.addToPlaylist(playlist);
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        track.setUserData(this.lastRequester);
        queue.addToPlaylist(track);
    }
}
