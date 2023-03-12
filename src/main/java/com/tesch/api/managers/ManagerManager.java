package com.tesch.api.managers;

import net.dv8tion.jda.api.entities.Guild;

public class ManagerManager extends GenericManager {

    private MusicManager musicManager;
    private MusicleManager musicleManager;
    private RNGManager rngManager;
    private HelpManager helpManager;
    private TicTacToeManager ticTacToeManager;
    private ChessManager chessManager;
    
    public ManagerManager(Guild guild) {
        super(guild);
        ManagerFactory managerFactory = new ManagerFactory(guild);
        this.musicManager = managerFactory.buildMusicManager();
        this.musicleManager = managerFactory.buildMusicleManager();
        this.rngManager = managerFactory.buildRngManager();
        this.helpManager = managerFactory.buildHelpManager();
        this.ticTacToeManager = managerFactory.buildTicTacToeManager();
        this.chessManager = managerFactory.buildChessManager();
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
}
