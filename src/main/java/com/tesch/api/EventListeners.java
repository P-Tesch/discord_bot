package com.tesch.api;

import org.jetbrains.annotations.NotNull;

import com.tesch.api.music.musicle.MusicleManager;
import com.tesch.api.music.player.MusicEventHandler;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EventListeners extends ListenerAdapter {

    private JDA jda;
    private MusicEventHandler musicEventHandler;
    private MusicleManager musicleManager;

    public EventListeners(JDA jda, MusicEventHandler musicEventHandler, MusicleManager musicleManager) {
        this.jda = jda;
        this.musicEventHandler = musicEventHandler;
        this.musicleManager = musicleManager;
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
            musicEventHandler.onDisconnectCommand();
            return;
        }

        if (message.startsWith("skip")) {
            musicEventHandler.onSkipCommand();
            return;
        }

        if (message.startsWith("queue")) {
            musicEventHandler.onQueueCommand();
            return;
        }

        if (message.startsWith("clear")) {
            musicEventHandler.onClearCommand();
            return;
        }

        if (message.startsWith("loop")) {
            musicEventHandler.onLoopCommand();
            return;
        }

        if (message.startsWith("musicle")) {
            try {
                musicleManager.onMusicleCommand(event);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        this.musicleManager.onButtonInteraction(event);
    }
    
    private void onMention(MessageReceivedEvent event) {
        event.getChannel().sendMessage(event.getAuthor().getAsMention()).queue();
    }
}
