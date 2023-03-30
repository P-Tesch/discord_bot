package com.tesch.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.tesch.utils.DiscordUtils;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class MusicChatResultHandler implements AudioLoadResultHandler {

    private TextChannel textChannel;
    private MusicQueue queue;

    public MusicChatResultHandler(TextChannel textChannel, MusicQueue queue) {
        this.textChannel = textChannel;
        this.queue = queue;
    }

    @Override
    public void loadFailed(FriendlyException e) {
        DiscordUtils.sendMessage("Error loading track: " + e.getMessage(), textChannel);
    }

    @Override
    public void noMatches() {
        DiscordUtils.sendMessage("No match for track", textChannel);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        queue.addToPlaylist(playlist);
        DiscordUtils.sendMessage("Playlist loaded", textChannel);
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        queue.addToPlaylist(track);
        DiscordUtils.sendMessage("Track loaded " + track.getInfo().title, textChannel);
    }
    
}
