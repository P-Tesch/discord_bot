package com.tesch.managers;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.tesch.games.quiz.musicle.MusicleManager;
import com.tesch.games.quiz.trivia.TriviaManager;
import com.tesch.music.MusicPlayer;

import dev.lavalink.youtube.YoutubeAudioSourceManager;
import net.dv8tion.jda.api.entities.Guild;

public class ManagerFactory {

    private Guild guild;

    public ManagerFactory(Guild guild) {
        this.guild = guild;
    }

    @SuppressWarnings("deprecation")
    public DefaultAudioPlayerManager buildAudioPlayerManager() {
        DefaultAudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        AudioSourceManagers.registerRemoteSources(playerManager, com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager.class);
        return playerManager;
    }

    public MusicleManager buildMusicleManager(MusicPlayer player) {
        return new MusicleManager(player);
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

    public BotuserManager buildBotuserManager() {
        return new BotuserManager();
    }

    public TriviaManager buildTriviaManager() {
        return new TriviaManager();
    }
}
