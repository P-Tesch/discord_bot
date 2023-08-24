package com.tesch.music;

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.tesch.managers.PlayerChannelManager;

public class MusicPlayerChannelResultHandler extends MusicResultHandler {

    private PlayerChannelManager manager;

    public MusicPlayerChannelResultHandler(MusicQueue queue, PlayerChannelManager manager) {
        this.setQueue(queue);
        this.manager = manager;
    }

    @Override
    public void loadFailed(FriendlyException e) {
        this.manager.updatePlayer("Error loading track: " + e.getMessage(), 5);
    }

    @Override
    public void noMatches() {
        this.manager.updatePlayer("No match for track", 5);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        super.playlistLoaded(playlist);
        this.manager.updatePlayer("Playlist" + playlist.getName() + "loaded", 5);
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        super.trackLoaded(track);
        this.manager.updatePlayer("Track loaded " + track.getInfo().title, 5);
    }
    
}
