package com.tesch.managers;

import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchProvider;
import com.tesch.music.MusicPlayer;
import com.tesch.music.MusicQueue;

import net.dv8tion.jda.api.entities.Guild;

public class ManagerManager extends GenericManager {

    private MusicleManager musicleManager;
    private RNGManager rngManager;
    private HelpManager helpManager;
    private TicTacToeManager ticTacToeManager;
    private ChessManager chessManager;
    private PlayerChannelManager playerChannelManager;
    private ChatMusicManager chatMusicManager;
    
    public ManagerManager(Guild guild) {
        super(guild);
        ManagerFactory managerFactory = new ManagerFactory(guild);
        this.rngManager = managerFactory.buildRngManager();
        this.helpManager = managerFactory.buildHelpManager();
        this.ticTacToeManager = managerFactory.buildTicTacToeManager();
        this.chessManager = managerFactory.buildChessManager();
        MusicPlayer musicPlayer = new MusicPlayer(managerFactory.buildAudioPlayerManager(), new MusicQueue(), new YoutubeSearchProvider(), guild);
        this.musicleManager = managerFactory.buildMusicleManager(musicPlayer);
        this.playerChannelManager = managerFactory.buildPlayerChannelManager(musicPlayer);
        this.chatMusicManager = managerFactory.buildChatMusicManager(musicPlayer);
    }

    public MusicleManager getMusicleManager() {
        return musicleManager;
    }

    public RNGManager getRngManager() {
        return rngManager;
    }

    public HelpManager getHelpManager() {
        return helpManager;
    }

    public TicTacToeManager getTicTacToeManager() {
        return ticTacToeManager;
    }

    public ChessManager getChessManager() {
        return chessManager;
    }

    public PlayerChannelManager getPlayerChannelManager() {
        return playerChannelManager;
    }

    public ChatMusicManager getChatMusicManager() {
        return chatMusicManager;
    }
}
