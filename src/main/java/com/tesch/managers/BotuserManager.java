package com.tesch.managers;

import com.tesch.db.GameRequester;
import com.tesch.db.entities.Score;
import com.tesch.utils.DiscordUtils;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BotuserManager {

    public BotuserManager() {
    }
    
    public void onScoreCommand(MessageReceivedEvent event) {
        Long discordId = event.getMessage().getMentions().getUsers().isEmpty() ? event.getAuthor().getIdLong() : event.getMessage().getMentions().getUsers().get(0).getIdLong();
        Score score = GameRequester.getUserScore(discordId);
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Musicle total: " + score.getMusicleTotal() + "\n");
        stringBuilder.append("Musicle Wins: " + score.getMusicleWin() + "\n");
        stringBuilder.append("Trivia Total: " + score.getTriviaTotal() + "\n");
        stringBuilder.append("Trivia Wins: " + score.getTriviaWin());

        DiscordUtils.sendMessage(stringBuilder.toString(), event.getChannel().asTextChannel());
    }
}
