package com.tesch.db.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Score {
    
    @JsonProperty("Score_id")
    private Long id;

    @JsonProperty("Musicle_total")
    private Integer musicleTotal;

    @JsonProperty("Musicle_win")
    private Integer musicleWin;

    @JsonProperty("Quiz_total")
    private Integer triviaTotal;

    @JsonProperty("Quiz_win")
    private Integer triviaWin;

    @JsonProperty("Tictactoe_total")
    private Integer tictactoeTotal;

    @JsonProperty("Tictactoe_win")
    private Integer tictactoeWin;

    @JsonProperty("Chess_total")
    private Integer chessTotal;

    @JsonProperty("Chess_win")
    private Integer chessWin;

    public Score() {
    }

    public Score(Long id, Integer musicleTotal, Integer musicleWin, Integer triviaTotal, Integer triviaWin, Integer tictactoeTotal, Integer tictactoeWin, Integer chessTotal, Integer chessWin) {
        this.id = id;
        this.musicleTotal = musicleTotal;
        this.musicleWin = musicleWin;
        this.triviaTotal = triviaTotal;
        this.triviaWin = triviaWin;
        this.tictactoeTotal = tictactoeTotal;
        this.tictactoeWin = tictactoeWin;
        this.chessTotal = chessTotal;
        this.chessWin = chessWin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMusicleTotal() {
        return musicleTotal;
    }

    public void setMusicleTotal(Integer musicleTotal) {
        this.musicleTotal = musicleTotal;
    }

    public Integer getMusicleWin() {
        return musicleWin;
    }

    public void setMusicleWin(Integer musicleWin) {
        this.musicleWin = musicleWin;
    }

    public Integer getTriviaTotal() {
        return triviaTotal;
    }

    public void setTriviaTotal(Integer triviaTotal) {
        this.triviaTotal = triviaTotal;
    }

    public Integer getTriviaWin() {
        return triviaWin;
    }

    public void setTriviaWin(Integer triviaWin) {
        this.triviaWin = triviaWin;
    }

    public Integer getTictactoeTotal() {
        return tictactoeTotal;
    }

    public void setTictactoeTotal(Integer tictactoeTotal) {
        this.tictactoeTotal = tictactoeTotal;
    }

    public Integer getTictactoeWin() {
        return tictactoeWin;
    }

    public void setTictactoeWin(Integer tictactoeWin) {
        this.tictactoeWin = tictactoeWin;
    }

    public Integer getChessTotal() {
        return chessTotal;
    }

    public void setChessTotal(Integer chessTotal) {
        this.chessTotal = chessTotal;
    }

    public Integer getChessWin() {
        return chessWin;
    }

    public void setChessWin(Integer chessWin) {
        this.chessWin = chessWin;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Score other = (Score) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Score_id = " + this.id;
    }
}
