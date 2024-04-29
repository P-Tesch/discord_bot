package com.tesch.managers;

import com.tesch.db.BotuserRequester;
import com.tesch.db.entities.Botuser;
import com.tesch.db.entities.Score;
import com.tesch.utils.DiscordUtils;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BotuserManager {

    public BotuserManager() {
    }

    public void onPixCommand(MessageReceivedEvent event) {
        Long discordIdSender = event.getMessage().getAuthor().getIdLong();
        Long discordIdReceiver = event.getMessage().getMentions().getUsers().get(0).getIdLong();
        Integer amount = Integer.parseInt(event.getMessage().getContentRaw().split(" ")[1]);

        BotuserRequester.transferCurrency(discordIdSender, discordIdReceiver, amount);
    }

    public void onCurrencyCommand(MessageReceivedEvent event) {
        Long discordId = event.getMessage().getMentions().getUsers().isEmpty() ? event.getAuthor().getIdLong() : event.getMessage().getMentions().getUsers().get(0).getIdLong();
        Botuser botuser = BotuserRequester.getBotuser(discordId);

        DiscordUtils.sendMessage("" + botuser.getCurrency(), event.getChannel().asTextChannel());
    }
    
    public void onScoreCommand(MessageReceivedEvent event) {
        Long discordId = event.getMessage().getMentions().getUsers().isEmpty() ? event.getAuthor().getIdLong() : event.getMessage().getMentions().getUsers().get(0).getIdLong();
        Score score = BotuserRequester.getUserScore(discordId);
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Musicle total: " + score.getMusicleTotal() + "\n");
        stringBuilder.append("Musicle Wins: " + score.getMusicleWin() + "\n");
        stringBuilder.append("Trivia Total: " + score.getTriviaTotal() + "\n");
        stringBuilder.append("Trivia Wins: " + score.getTriviaWin());

        DiscordUtils.sendMessage(stringBuilder.toString(), event.getChannel().asTextChannel());
    }
}
