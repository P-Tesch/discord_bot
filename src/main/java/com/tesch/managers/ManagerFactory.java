package com.tesch.managers;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.tesch.music.MusicPlayer;

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

    public MusicleManager buildMusicleManager(MusicPlayer musicPlayer) {
        return new MusicleManager(musicPlayer);
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

    public PlayerChannelManager buildPlayerChannelManager(MusicPlayer musicPlayer) {
        return new PlayerChannelManager(musicPlayer, guild);
    }

    public ChatMusicManager buildChatMusicManager(MusicPlayer musicPlayer) {
        return new ChatMusicManager(musicPlayer, guild);
    }
}
