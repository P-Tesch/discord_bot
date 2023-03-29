package com.tesch.managers;

import com.tesch.music.MusicPlayer;

import net.dv8tion.jda.api.entities.Guild;

public abstract class MusicManager extends GenericManager {

    private MusicPlayer musicPlayer;
    
    public MusicManager(MusicPlayer musicPlayer, Guild guild) {
        super(guild);
        this.musicPlayer = musicPlayer;
    }

    public MusicPlayer getMusicPlayer() {
        return this.musicPlayer;
    }
}
