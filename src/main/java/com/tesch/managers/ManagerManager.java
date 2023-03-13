package com.tesch.managers;

import net.dv8tion.jda.api.entities.Guild;

public class ManagerManager extends GenericManager {

    private MusicManager musicManager;
    private MusicleManager musicleManager;
    private RNGManager rngManager;
    private HelpManager helpManager;
    private TicTacToeManager ticTacToeManager;
    private ChessManager chessManager;
    private PlayerChannelManager playerChannelManager;
    
    public ManagerManager(Guild guild) {
        super(guild);
        ManagerFactory managerFactory = new ManagerFactory(guild);
        this.musicManager = managerFactory.buildMusicManager();
        this.musicleManager = managerFactory.buildMusicleManager(this.musicManager);
        this.rngManager = managerFactory.buildRngManager();
        this.helpManager = managerFactory.buildHelpManager();
        this.ticTacToeManager = managerFactory.buildTicTacToeManager();
        this.chessManager = managerFactory.buildChessManager();
        this.playerChannelManager = managerFactory.buildPlayerChannelManager(this.musicManager);
    }

    public MusicManager getMusicManager() {
        return musicManager;
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
}
