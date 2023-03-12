package com.tesch.api.managers;

import java.util.ArrayList;
import java.util.List;

import com.tesch.api.utils.DiscordUtils;
import com.tesch.api.utils.MiscUtils;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RNGManager {
    
    public RNGManager() {
    }

    public void coinFlip(MessageReceivedEvent event) {
        TextChannel text = event.getChannel().asTextChannel();
        DiscordUtils.sendMessage(MiscUtils.randomInt(0, 100) < 50 ? "https://imgur.com/QiQ63s7" : "https://imgur.com/UbuR3zg", text);
    }

    public void diceRoll(MessageReceivedEvent event) {
        TextChannel text = event.getChannel().asTextChannel();
        String[] rollInfo = event.getMessage().getContentRaw().replace("roll ", "").split("d");
        List<Integer> rolls = new ArrayList<>();

        for (int i = 0; i < Integer.parseInt(rollInfo[0]); i++) {
            rolls.add(MiscUtils.randomInt(1, 6));
        }

        StringBuilder stringBuilder = new StringBuilder();

        if (rolls.size() > 1) {
            stringBuilder.append("Total: ");
            stringBuilder.append(MiscUtils.listSum(rolls).toString());
            stringBuilder.append(" (");
            rolls.forEach(n -> stringBuilder.append(n + " + "));
            int lastPlus = stringBuilder.lastIndexOf("+");
            stringBuilder.deleteCharAt(lastPlus + 1);
            stringBuilder.deleteCharAt(lastPlus);
            stringBuilder.deleteCharAt(lastPlus - 1);
            stringBuilder.append(")");
        }
        else {
            stringBuilder.append(rolls.get(0));
        }

        DiscordUtils.sendMessage(stringBuilder.toString(), text);
    }
}
