package com.tesch.api;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchProvider;
import com.tesch.api.games.RNGManager;
import com.tesch.api.games.chess.ChessManager;
import com.tesch.api.games.musicle.MusicleManager;
import com.tesch.api.games.tictactoe.TicTacToeManager;
import com.tesch.api.music.MusicManager;
import com.tesch.api.music.MusicQueue;

public class ManagerFactory {
    
    public ManagerFactory() {
    }

    public DefaultAudioPlayerManager buildAudioPlayerManager() {
        DefaultAudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        return playerManager;
    }

    public MusicManager buildMusicManager() {
        return new MusicManager(this.buildAudioPlayerManager(), new MusicQueue(), new YoutubeSearchProvider());
    }

    public MusicleManager buildMusicleManager() {
        return new MusicleManager(this.buildMusicManager());
    }

    public MusicleManager buildMusicleManager(MusicManager musicManager) {
        return new MusicleManager(musicManager);
    }

    public RNGManager buildRngManager() {
        return new RNGManager();
    }

    public HelpManager buildHelpManager() {
        return new HelpManager();
    }

    public TicTacToeManager buildTicTacToeManager() {
        return new TicTacToeManager();
    }

    public ChessManager buildChessManager() {
        return new ChessManager();
    }
}
