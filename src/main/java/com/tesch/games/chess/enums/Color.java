package com.tesch.games.chess.enums;

import com.tesch.games.Teams;

public enum Color {
    WHITE (Teams.A),
    BLACK (Teams.B);

    private Teams team;

    private Color(Teams team) {
        this.team = team;
    }

    public Teams getTeam() {
        return this.team;
    }

    public String toString() {
        return this.name().toLowerCase();
    }
}
