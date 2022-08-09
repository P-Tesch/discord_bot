package com.tesch.api.games;

import java.util.ArrayList;
import java.util.List;

import com.tesch.api.utils.DiscordUtils;
import com.tesch.api.utils.MiscUtils;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RNGManager {

    private DiscordUtils discordUtils;
    
    public RNGManager() {
        this.discordUtils = new DiscordUtils();
    }

    public void coinFlip(MessageReceivedEvent event) {
        this.discordUtils.buildFromMessageEvent(event);
        this.discordUtils.sendMessage(MiscUtils.randomInt(0, 100) < 50 ? "https://imgur.com/QiQ63s7" : "https://imgur.com/UbuR3zg");
    }

    public void diceRoll(MessageReceivedEvent event) {
        this.discordUtils.buildFromMessageEvent(event);
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

        discordUtils.sendMessage(stringBuilder.toString());
    }
}
