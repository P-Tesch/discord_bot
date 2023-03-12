package com.tesch.managers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchProvider;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import com.tesch.music.MusicPlayerSendHandler;
import com.tesch.music.MusicQueue;
import com.tesch.music.MusicResultHandler;
import com.tesch.utils.DiscordUtils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class MusicManager extends GenericManager {

    private AudioPlayer audioPlayer;
    private AudioPlayerManager playerManager;
    private MusicQueue queue;
    private YoutubeSearchProvider youtubeSearch;
    private boolean musicleMode;
    
    public MusicManager(AudioPlayerManager playerManager, MusicQueue queue, YoutubeSearchProvider youtubeSearch, Guild guild) {
        super(guild);
        this.playerManager = playerManager;
        this.queue = queue;
        this.youtubeSearch = youtubeSearch;

        this.audioPlayer = this.playerManager.createPlayer();

        this.queue.setPlayer(this.audioPlayer);
        this.audioPlayer.addListener(this.queue);
        this.musicleMode = false;
    }

    public AudioPlayer getAudioPlayer() {
        return this.audioPlayer;
    }

    public AudioPlayerManager getPlayerManager() {
        return this.playerManager;
    }

    public MusicQueue getQueue() {
        return this.queue;
    }

    public void setMusicleMode(boolean mode) {
        this.musicleMode = mode;
    }

    public void onPlayCommand(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw().replace("play ", "");
        TextChannel text = event.getChannel().asTextChannel();

        if (this.musicleMode) {
            DiscordUtils.sendMessage("Wait for musicle finish", text);
            return;
        }
        DiscordUtils.connectToVoice(new MusicPlayerSendHandler(audioPlayer), event.getGuild(), event.getMember().getVoiceState().getChannel());

        if (this.isUrl(message)) {
            this.playFromUrl(message, text);
        }
        else {
            this.playFromSearch(message, text);
        }
    }

    public void onVolumeCommand(MessageReceivedEvent event) {
        String[] volume = event.getMessage().getContentRaw().split(" ");
        TextChannel text = event.getChannel().asTextChannel();
        if (volume.length == 1) {
            DiscordUtils.sendMessage("Current volume: " + this.audioPlayer.getVolume(), text);
        }
        else {
            this.audioPlayer.setVolume(Integer.parseInt(volume[1]));
            DiscordUtils.sendMessage("Volume set to: " + this.audioPlayer.getVolume(), text);
        }
    }

    public void onPauseCommand(MessageReceivedEvent event) {
        TextChannel text = event.getChannel().asTextChannel();
        if (this.musicleMode) {
            DiscordUtils.sendMessage("Wait for musicle finish", text);
            return;
        }
        this.audioPlayer.setPaused(this.audioPlayer.isPaused() ? false : true);
    }

    public void onDisconnectCommand(MessageReceivedEvent event) {
        DiscordUtils.disconnectFromVoice(this.getGuild());
        this.onClearCommand(event);
    }

    public void silentDisconnect() {
        DiscordUtils.disconnectFromVoice(this.getGuild());
        this.silentClear();
    }

    public void onSkipCommand(MessageReceivedEvent event) {
        TextChannel text = event.getChannel().asTextChannel();
        if (this.musicleMode) {
            DiscordUtils.sendMessage("Wait for musicle finish", text);
            return;
        }
        this.queue.playNextTrack(true);
    }

    public void onQueueCommand(MessageReceivedEvent event) {
        TextChannel text = event.getChannel().asTextChannel();
        if (this.musicleMode) {
            DiscordUtils.sendMessage("Wait for musicle finish", text);
            return;
        }
        try {
            List<String> playlist = new ArrayList<>();
            StringBuilder queueString = new StringBuilder();
            playlist.add(this.audioPlayer.getPlayingTrack().getInfo().title);
            this.queue.getPlaylist().stream().map(x -> x.getInfo().title).forEach(playlist::add);
            for (int i = 0; i < 7; i++) {
                if (i < playlist.size()) {
                    queueString.append((i == 0 ? "Playing" : i) + ". " + playlist.get(i) + "\n");
                }
            }
            event.getChannel().sendMessage("```\nPage 1:\n" + queueString + "\n```").setActionRow(Button.primary("queue previous", "⬅️"), Button.primary("queue next", "➡️")).queue();
        }
        catch (NullPointerException e) {
            DiscordUtils.sendMessage("Queue empty", text);
        }
    }

    public void onQueueButton(ButtonInteractionEvent event) {
        try {
            List<String> playlist = new ArrayList<>();
            playlist.add(this.audioPlayer.getPlayingTrack().getInfo().title);
            this.queue.getPlaylist().stream().map(x -> x.getInfo().title).forEach(playlist::add);

            Integer page = Integer.parseInt(event.getMessage().getContentRaw().split("\n")[1].split(" ")[1].replace(":", ""));
            page = event.getButton().getId().equals("queue next") ? page + 1 : page - 1;

            int lastPage = playlist.size() % 7 == 0 ? playlist.size() / 7 : playlist.size() / 7 + 1;
            if (page == 0) page = lastPage;
            if (page > lastPage) page = 1;

            StringBuilder queueString = new StringBuilder();

            for (int i = (page - 1) * 7; i < ((page - 1) * 7) + 7; i++) {
                if (i < playlist.size()) {
                    queueString.append(i + ". " + playlist.get(i) + "\n");
                }
            }
            event.editMessage("```\nPage " + page + ":\n" + queueString + "\n```").queue();
        }
        catch (IllegalStateException e) {
            e.getMessage();
        }
    }

    public void onClearCommand(MessageReceivedEvent event) {
        TextChannel text = event.getChannel().asTextChannel();
        if (this.musicleMode) {
            DiscordUtils.sendMessage("Wait for musicle finish", text);
            return;
        }
        if (this.audioPlayer.getPlayingTrack() != null) {
            DiscordUtils.sendMessage("Cleared queue", text);
        }
        this.queue.clearPlaylist();
    }

    private void silentClear() {
        this.queue.clearPlaylist();
    }

    public void onLoopCommand(MessageReceivedEvent event) {
        TextChannel text = event.getChannel().asTextChannel();
        if (this.musicleMode) {
            DiscordUtils.sendMessage("Wait for musicle finish", text);
            return;
        }
        this.queue.setLoop(!this.queue.getLoop());
        DiscordUtils.sendMessage("Loop set to " + this.queue.getLoop(), text);
    }

    public void onShuffleCommand(MessageReceivedEvent event) {
        TextChannel text = event.getChannel().asTextChannel();
        if (this.musicleMode) {
            DiscordUtils.sendMessage("Wait for musicle finish", text);
            return;
        }
        this.queue.shufflePlaylist();
        DiscordUtils.sendMessage("Queue Suffled", text);
    }

    public void onJumpToCommand(MessageReceivedEvent event) {
        for (int i = 0; i < Integer.parseInt(event.getMessage().getContentRaw().split(" ")[1]); i++) {
            this.onSkipCommand(event);
        }
    }

    private boolean isUrl(String test) {
        try {
            new URL(test);
            return true;
        } 
        catch (MalformedURLException e) {
            return false;
        }
    }

    private void playFromUrl(String url, TextChannel text) {
        MusicResultHandler resultHandler = new MusicResultHandler(text, queue);
        playerManager.loadItem(url, resultHandler);
    }

    private void playFromSearch(String search, TextChannel text) {
        MusicResultHandler resultHandler = new MusicResultHandler(text, queue);

        BasicAudioPlaylist songs = (BasicAudioPlaylist) youtubeSearch.loadSearchResult(search, info -> new YoutubeAudioTrack(info, new YoutubeAudioSourceManager()));
        AudioTrack song = songs.getTracks().get(0);
        
        playerManager.loadItem(song.getInfo().uri, resultHandler);
    }
}
