package com.tesch.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.tesch.managers.PlayerChannelManager;
import com.tesch.utils.DiscordUtils;
import com.tesch.utils.TaskScheduler;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class MusicPlayerChannelResultHandler implements AudioLoadResultHandler {

    private TextChannel textChannel;
    private MusicQueue queue;
    private TaskScheduler scheduler;
    private PlayerChannelManager manager;

    public MusicPlayerChannelResultHandler(TextChannel textChannel, MusicQueue queue, TaskScheduler scheduler, PlayerChannelManager manager) {
        this.textChannel = textChannel;
        this.queue = queue;
        this.scheduler = scheduler;
        this.manager = manager;
    }

    @Override
    public void loadFailed(FriendlyException e) {
        Message message = DiscordUtils.sendMessage("Error loading track: " + e.getMessage(), textChannel);
        this.scheduler.schedule(() -> {textChannel.deleteMessageById(message.getIdLong()).queue();}, 5);
    }

    @Override
    public void noMatches() {
        Message message = DiscordUtils.sendMessage("No match for track", textChannel);
        this.scheduler.schedule(() -> {textChannel.deleteMessageById(message.getIdLong()).queue();}, 5);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        queue.addToPlaylist(playlist);
        Message message = DiscordUtils.sendMessage("Playlist loaded", textChannel);
        this.scheduler.schedule(() -> {textChannel.deleteMessageById(message.getIdLong()).queue();}, 5);
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        queue.addToPlaylist(track);
        this.manager.updatePlayer("Track loaded " + track.getInfo().title, 5);
    }
    
}
