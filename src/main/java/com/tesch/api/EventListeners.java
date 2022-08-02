package com.tesch.api;

import org.jetbrains.annotations.NotNull;

import com.tesch.api.music.player.MusicEventHandler;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EventListeners extends ListenerAdapter {

    private JDA jda;
    private MusicEventHandler musicEventHandler;

    public EventListeners(JDA jda, MusicEventHandler musicEventHandler) {
        this.jda = jda;
        this.musicEventHandler = musicEventHandler;
    }
    
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String message = event.getMessage().getContentRaw();

        if (event.getMessage().getMentions().isMentioned(jda.getSelfUser(), MentionType.USER)) {
            this.onMention(event);
            return;
        }

        if (message.startsWith("play")) {
            musicEventHandler.onPlayCommand(event);
            return;
        }

        if (message.startsWith("volume")) {
            musicEventHandler.onVolumeCommand(event);
            return;
        }

        if (message.startsWith("pause")) {
            musicEventHandler.onPauseCommand();
            return;
        }

        if (message.startsWith("disconnect")) {
            musicEventHandler.onDisconnectCommand(event);
            return;
        }

        if (message.startsWith("skip")) {
            musicEventHandler.onSkipCommand(event);
            return;
        }

        if (message.startsWith("queue")) {
            musicEventHandler.onQueueCommand(event);
            return;
        }

        if (message.startsWith("clear")) {
            musicEventHandler.onClearCommand(event);
            return;
        }

        if (message.startsWith("loop")) {
            musicEventHandler.onLoopCommand(event);
            return;
        }
    }
    
    private void onMention(MessageReceivedEvent event) {
        event.getChannel().sendMessage(event.getAuthor().getAsMention()).queue();
    }
}
