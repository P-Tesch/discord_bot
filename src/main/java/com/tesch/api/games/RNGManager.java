package com.tesch.api.games;

import java.net.URL;

import com.tesch.api.utils.DiscordUtils;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RNGManager {

    private DiscordUtils discordUtils;
    
    public RNGManager() {
        this.discordUtils = new DiscordUtils();
    }

    public void coinFlip(MessageReceivedEvent event) {
        this.discordUtils.buildFromMessageEvent(event);
        this.discordUtils.sendMessage(Math.random() <= 0.5 ? "https://imgur.com/QiQ63s7" : "https://imgur.com/UbuR3zg");
    }
}
