package com.tesch.api;

public enum HelpEnums {
    PLAY ("Plays a youtube song. Usage: 'play -Url/Search content-'"), 
    VOLUME ("Changes the bot audio volume. Usage: 'volume -Amount-"),
    PAUSE ("Pauses and unpauses the playing song. Usage: 'pause'"),
    DISCONNECT ("Disconnects the bot from the voice channel and clears the queue. Usage: 'disconnect'"),
    SKIP ("Skips to the next song in the queue. Usage: 'skip'"),
    QUEUE ("Returns the current song queue. Usage: 'queue'"),
    CLEAR ("Clears the queue. Usage: 'clear'"),
    LOOP ("Repeats the queue indefinitely. Usage: 'loop'"),
    SHUFFLE ("Shuffles the current queue. Usage: 'shuffle'"),
    MUSICLE ("Musicle minigame. Usage: 'musicle -genre- / 'musicle score' / 'musicle mode' / 'musicle start''"),
    COINFLIP ("Flips a coin, returning heads or tails. Usage: 'coinflip'"),
    ROLL ("Rolls a X sided dice Y times. Usage: 'roll YdX'");

    private String description;

    private HelpEnums(String description) {
        this.description = description;
    }
    
    public String getdescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
