package com.tesch.api;

import java.util.Arrays;

import com.tesch.api.utils.DiscordUtils;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HelpManager {

    private DiscordUtils discordUtils;
    
    public HelpManager() {
        this.discordUtils = new DiscordUtils();
    }

    public void onHelpCommand(MessageReceivedEvent event) {
        this.discordUtils.buildFromMessageEvent(event);
        String message = event.getMessage().getContentRaw();

        if (message.trim().equalsIgnoreCase("help")) {
            this.help();
            return;
        }

        String[] splitMessage = message.split(" ");
        this.commandHelp(splitMessage[1]);
    }

    private void help() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Use help -Command- to get help to a command\n");
        stringBuilder.append("Avaliable commands:\n");
        Arrays.asList(HelpEnums.values()).forEach(x -> stringBuilder.append(x.toString() + "\n"));
        this.discordUtils.sendMessage(stringBuilder.toString());
    }

    private void commandHelp(String command) {
        this.discordUtils.sendMessage(HelpEnums.valueOf(command.toUpperCase()).getdescription());
    }
}
