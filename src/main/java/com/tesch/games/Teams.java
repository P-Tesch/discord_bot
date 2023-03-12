package com.tesch.games;

public enum Teams {
    A,
    B;

    private Teams() {
    }

    public static Teams getOther(Teams team) {
        return team == Teams.A ? Teams.B : Teams.A;
    } 
}
