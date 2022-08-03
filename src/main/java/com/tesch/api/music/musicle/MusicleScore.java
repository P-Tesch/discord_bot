package com.tesch.api.music.musicle;

public class MusicleScore {
    
    private Integer winCount;
    private Integer lossCount;

    public MusicleScore() {
        this.winCount = 0;
        this.lossCount = 0;
    }

    public Integer getWinCount() {
        return winCount;
    }
    public void addWin() {
        this.winCount += 1;
    }
    public Integer getLossCount() {
        return lossCount;
    }
    public void addLoss() {
        this.lossCount += 1;
    }
}
