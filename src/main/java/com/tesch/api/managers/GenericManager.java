package com.tesch.api.managers;

import net.dv8tion.jda.api.entities.Guild;

public class GenericManager {
    
    private Guild guild;

    public GenericManager(Guild guild) {
        this.guild = guild;
    }

    public Guild getGuild() {
        return this.guild;
    }
}
