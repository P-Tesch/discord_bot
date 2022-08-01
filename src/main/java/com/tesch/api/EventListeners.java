package com.tesch.api;

import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EventListeners extends ListenerAdapter {

    private JDA jda;

    public EventListeners(JDA jda) {
        this.jda = jda;
    }
    
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getMessage().getMentions().isMentioned(jda.getSelfUser(), MentionType.USER)) {
            event.getChannel().sendMessage(event.getAuthor().getAsMention()).queue();
        }
    } 
}
