package com.tesch.api;

import org.jetbrains.annotations.NotNull;

import com.tesch.api.games.RNGManager;
import com.tesch.api.music.musicle.MusicleManager;
import com.tesch.api.music.player.MusicManager;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EventListeners extends ListenerAdapter {

    private MusicManager musicManager;
    private MusicleManager musicleManager;
    private RNGManager rngManager;
    private HelpManager helpManager;

    public EventListeners(ManagerFactory managerFactory) {
        this.musicManager = managerFactory.buildMusicManager();
        this.musicleManager = managerFactory.buildMusicleManager(this.musicManager);
        this.rngManager = managerFactory.buildRngManager();
        this.helpManager = managerFactory.buildHelpManager();
    }
    
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String message = event.getMessage().getContentRaw();

        if (message.startsWith("play")) {
            musicManager.onPlayCommand(event);
            return;
        }

        if (message.startsWith("volume")) {
            musicManager.onVolumeCommand(event);
            return;
        }

        if (message.startsWith("pause")) {
            musicManager.onPauseCommand();
            return;
        }

        if (message.startsWith("disconnect")) {
            musicManager.onDisconnectCommand();
            return;
        }

        if (message.startsWith("skip")) {
            musicManager.onSkipCommand();
            return;
        }

        if (message.startsWith("queue")) {
            musicManager.onQueueCommand();
            return;
        }

        if (message.startsWith("clear")) {
            musicManager.onClearCommand();
            return;
        }

        if (message.startsWith("loop")) {
            musicManager.onLoopCommand();
            return;
        }

        if (message.startsWith("shuffle")) {
            musicManager.onShuffleCommand();
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

        if (message.startsWith("coinflip")) {
            rngManager.coinFlip(event);
        }

        if (message.startsWith("roll")) {
            rngManager.diceRoll(event);
        }

        if (message.startsWith("help")) {
            helpManager.onHelpCommand(event);
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Guild guild = event.getGuild();
        guild.addRoleToMember(event.getMember(), guild.getRoleById(698247997091479652L)).queue();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        this.musicleManager.onButtonInteraction(event);
    }
}
