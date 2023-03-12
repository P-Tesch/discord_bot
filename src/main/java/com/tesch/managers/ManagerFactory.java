package com.tesch.managers;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchProvider;
import com.tesch.music.MusicQueue;

import net.dv8tion.jda.api.entities.Guild;

public class ManagerFactory {

    private Guild guild;

    public ManagerFactory(Guild guild) {
        this.guild = guild;
    }

    public DefaultAudioPlayerManager buildAudioPlayerManager() {
        DefaultAudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        return playerManager;
    }

    public MusicManager buildMusicManager() {
        return new MusicManager(this.buildAudioPlayerManager(), new MusicQueue(), new YoutubeSearchProvider(), guild);
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
