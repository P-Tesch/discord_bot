package com.tesch.music;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchProvider;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import com.tesch.exceptions.MusicleException;
import com.tesch.utils.DiscordUtils;

import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;


public class MusicPlayer {
    
    private AudioPlayer audioPlayer;
    private AudioPlayerManager playerManager;
    private MusicQueue queue;
    private YoutubeSearchProvider youtubeSearch;
    private boolean musicleMode;
    private Guild guild;
    
    public MusicPlayer(AudioPlayerManager playerManager, MusicQueue queue, YoutubeSearchProvider youtubeSearch, Guild guild) {
        this.guild = guild;
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

    public YoutubeSearchProvider getYoutubeSearchProvider() {
        return this.youtubeSearch;
    }

    public boolean getMusicleMode() {
        return this.musicleMode;
    }

    public void setMusicleMode(boolean mode) {
        this.musicleMode = mode;
    }

    public void play(AudioChannel channel, String message, AudioLoadResultHandler resultHandler) {
        if (this.musicleMode) {
            throw new MusicleException("wait for musicle to finish");
        }

        DiscordUtils.connectToVoice(new MusicPlayerSendHandler(audioPlayer), this.guild, channel);

        if (this.isUrl(message)) {
            this.playFromUrl(message, resultHandler);
        }
        else {
            this.playFromSearch(message, resultHandler);
        }
    }

    public void pause() {
        if (this.musicleMode) {
            throw new MusicleException("Wait for musicle to finish");
        }
        this.audioPlayer.setPaused(this.audioPlayer.isPaused() ? false : true);
    }

    public void disconnect() {
        this.queue.clearPlayedPlaylist();
        DiscordUtils.disconnectFromVoice(this.guild);
    }

    public void skip() {
        if (this.musicleMode) {
            throw new MusicleException("wait for musicle to finish");
        }
        this.queue.playNextTrack(true);
    }

    public String queue(Integer page) throws NullPointerException {
        if (this.musicleMode) {
            throw new MusicleException("Wait for musicle to finish");
        }

        try {
            List<String> playlist = new ArrayList<>();
            playlist.add(this.audioPlayer.getPlayingTrack().getInfo().title);
            this.queue.getPlaylist().stream().map(x -> x.getInfo().title).forEach(playlist::add);

            int lastPage = playlist.size() % 7 == 0 ? playlist.size() / 7 : playlist.size() / 7 + 1;
            while (page < 0) {
                page += lastPage;
            }
            while (page > lastPage) { 
                page -= lastPage;
            }
            if (page == 0) {
                page = lastPage;
            }

            StringBuilder queueString = new StringBuilder();

            for (int i = (page - 1) * 7; i < ((page - 1) * 7) + 7; i++) {
                if (i < playlist.size()) {
                    queueString.append(i + ". " + playlist.get(i) + "\n");
                }
            }
            return "\nPage " + page + ":\n" + queueString.toString() + "\n";
        }
        catch (IllegalStateException e) {
            e.getMessage();
            return null;
        }
        catch (NullPointerException e) {
            return null;
        }
    }

    public void forceClearDisconnect() {
        this.queue.clearPlaylist();
        this.disconnect();
    }

    public void clear() {
        if (this.musicleMode) {
            throw new MusicleException("Wait for musicle to finish");
        }
        this.queue.clearPlaylist();
    }

    public void loop() {
        if (this.musicleMode) {
            throw new MusicleException("Wait for musicle to finish");
        }
        this.queue.setLoop(!this.queue.getLoop());
    }

    public void shuffle() {
        if (this.musicleMode) {
            throw new MusicleException("Wait for musicle to finish");
        }
        this.queue.shufflePlaylist();
    }

    public boolean isUrl(String test) {
        try {
            new URL(test);
            return true;
        } 
        catch (MalformedURLException e) {
            return false;
        }
    }

    public void playFromUrl(String url, AudioLoadResultHandler resultHandler) {
        playerManager.loadItem(url, resultHandler);
    }

    public void playFromSearch(String search, AudioLoadResultHandler resultHandler) {
        BasicAudioPlaylist songs = (BasicAudioPlaylist) youtubeSearch.loadSearchResult(search, info -> new YoutubeAudioTrack(info, new YoutubeAudioSourceManager()));
        AudioTrack song = songs.getTracks().get(0);
        
        playerManager.loadItem(song.getInfo().uri, resultHandler);
    }
}
