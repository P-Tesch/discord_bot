package com.tesch.api.music;

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
import com.tesch.api.utils.DiscordUtils;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class MusicManager {

    private AudioPlayer audioPlayer;
    private AudioPlayerManager playerManager;
    private MusicQueue queue;
    private YoutubeSearchProvider youtubeSearch;
    private DiscordUtils discordUtils;
    private boolean musicleMode;
    
    public MusicManager(AudioPlayerManager playerManager, MusicQueue queue, YoutubeSearchProvider youtubeSearch) {
        this.playerManager = playerManager;
        this.queue = queue;
        this.youtubeSearch = youtubeSearch;
        this.discordUtils = new DiscordUtils();

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

    public DiscordUtils getDiscordUtils() {
        return this.discordUtils;
    }

    public void setMusicleMode(boolean mode) {
        this.musicleMode = mode;
    }

    public void onPlayCommand(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw().replace("play ", "");

        discordUtils.buildFromMessageEvent(event);
        if (this.musicleMode) {
            discordUtils.sendMessage("Wait for musicle finish");
            return;
        }
        discordUtils.connectToVoice(new MusicPlayerSendHandler(audioPlayer));

        if (this.isUrl(message)) {
            this.playFromUrl(message);
        }
        else {
            this.playFromSearch(message);
        }
    }

    public void onVolumeCommand(MessageReceivedEvent event) {
        String[] volume = event.getMessage().getContentRaw().split(" ");
        if (volume.length == 1) {
            this.discordUtils.sendMessage("Current volume: " + this.audioPlayer.getVolume());
        }
        else {
            this.audioPlayer.setVolume(Integer.parseInt(volume[1]));
            this.discordUtils.sendMessage("Volume set to: " + this.audioPlayer.getVolume());
        }
    }

    public void onPauseCommand() {
        if (this.musicleMode) {
            discordUtils.sendMessage("Wait for musicle finish");
            return;
        }
        this.audioPlayer.setPaused(this.audioPlayer.isPaused() ? false : true);
    }

    public void onDisconnectCommand() {
        this.discordUtils.disconnectFromVoice();
        this.onClearCommand();
    }

    public void onSkipCommand() {
        if (this.musicleMode) {
            discordUtils.sendMessage("Wait for musicle finish");
            return;
        }
        this.queue.playNextTrack(true);
    }

    public void onQueueCommand(MessageReceivedEvent event) {
        if (this.musicleMode) {
            discordUtils.sendMessage("Wait for musicle finish");
            return;
        }
        try {
            List<String> playlist = new ArrayList<>();
            StringBuilder queueString = new StringBuilder();
            playlist.add(this.audioPlayer.getPlayingTrack().getInfo().title);
            this.queue.getPlaylist().stream().map(x -> x.getInfo().title).forEach(playlist::add);
            for (int i = 0; i < 7; i++) {
                queueString.append(i + ". " + playlist.get(i) + "\n");
            }
            event.getChannel().sendMessage("```\nPage 1:\n" + queueString + "\n```").setActionRow(Button.primary("queue previous", "⬅️"), Button.primary("queue next", "➡️")).queue();
        }
        catch (NullPointerException e) {
            this.discordUtils.sendMessage("Queue empty");
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

    public void onClearCommand() {
        if (this.musicleMode) {
            discordUtils.sendMessage("Wait for musicle finish");
            return;
        }
        if (this.audioPlayer.getPlayingTrack() != null) {
            this.discordUtils.sendMessage("Cleared queue");
        }
        this.queue.clearPlaylist();
    }

    public void onLoopCommand() {
        if (this.musicleMode) {
            discordUtils.sendMessage("Wait for musicle finish");
            return;
        }
        this.queue.setLoop(!this.queue.getLoop());
        this.discordUtils.sendMessage("Loop set to " + this.queue.getLoop());
    }

    public void onShuffleCommand() {
        if (this.musicleMode) {
            discordUtils.sendMessage("Wait for musicle finish");
            return;
        }
        this.queue.shufflePlaylist();
        this.discordUtils.sendMessage("Queue Suffled");
    }

    public void onJumpToCommand(MessageReceivedEvent event) {
        for (int i = 0; i < Integer.parseInt(event.getMessage().getContentRaw().split(" ")[1]); i++) {
            this.onSkipCommand();
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

    private void playFromUrl(String url) {
        MusicResultHandler resultHandler = new MusicResultHandler(this.discordUtils.getText(), queue);
        playerManager.loadItem(url, resultHandler);
    }

    private void playFromSearch(String search) {
        MusicResultHandler resultHandler = new MusicResultHandler(this.discordUtils.getText(), queue);

        BasicAudioPlaylist songs = (BasicAudioPlaylist) youtubeSearch.loadSearchResult(search, info -> new YoutubeAudioTrack(info, new YoutubeAudioSourceManager()));
        AudioTrack song = songs.getTracks().get(0);
        
        playerManager.loadItem(song.getInfo().uri, resultHandler);
    }
}
