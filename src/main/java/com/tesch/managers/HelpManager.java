package com.tesch.managers;

import java.util.Arrays;

import com.tesch.HelpEnums;
import com.tesch.utils.DiscordUtils;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HelpManager {
    
    public HelpManager() {
    }

    public void onHelpCommand(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        TextChannel text = event.getChannel().asTextChannel();

        if (message.trim().equalsIgnoreCase("help")) {
            this.help(text);
            return;
        }

        String[] splitMessage = message.split(" ");
        this.commandHelp(splitMessage[1], text);
    }

    private void help(TextChannel text) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Use help -Command- to get help to a command\n");
        stringBuilder.append("Avaliable commands:\n");
        Arrays.asList(HelpEnums.values()).forEach(x -> stringBuilder.append(x.toString() + "\n"));
        DiscordUtils.sendMessage(stringBuilder.toString(), text);
    }

    private void commandHelp(String command, TextChannel text) {
        DiscordUtils.sendMessage(HelpEnums.valueOf(command.toUpperCase()).getdescription(), text);
    }
}
