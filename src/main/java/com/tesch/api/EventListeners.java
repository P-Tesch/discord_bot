package com.tesch.api;

import java.util.HashMap;
import java.util.Map;

import com.tesch.api.managers.ManagerManager;
import com.tesch.api.utils.TaskScheduler;

import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EventListeners extends ListenerAdapter {

    private TaskScheduler taskScheduler;
    private Map<Long, ManagerManager> managers;

    public EventListeners() {
        this.managers = new HashMap<>();
        this.taskScheduler = new TaskScheduler();
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        ManagerManager manager = this.RegisterGuildAndGetManager(event);

        String message = event.getMessage().getContentRaw();

        if (message.startsWith("play")) {
            manager.getMusicManager().onPlayCommand(event);
            return;
        }

        if (message.startsWith("volume")) {
            manager.getMusicManager().onVolumeCommand(event);
            return;
        }

        if (message.startsWith("pause")) {
            manager.getMusicManager().onPauseCommand(event);
            return;
        }

        if (message.startsWith("disconnect")) {
            manager.getMusicManager().onDisconnectCommand(event);
            return;
        }

        if (message.startsWith("skip")) {
            manager.getMusicManager().onSkipCommand(event);
            return;
        }

        if (message.startsWith("queue")) {
            manager.getMusicManager().onQueueCommand(event);
            return;
        }

        if (message.startsWith("clear")) {
            manager.getMusicManager().onClearCommand(event);
            return;
        }

        if (message.startsWith("loop")) {
            manager.getMusicManager().onLoopCommand(event);
            return;
        }

        if (message.startsWith("shuffle")) {
            manager.getMusicManager().onShuffleCommand(event);
            return;
        }

        if (message.startsWith("jumpto")) {
            manager.getMusicManager().onJumpToCommand(event);
            return;
        }

        if (message.startsWith("musicle")) {
            try {
                manager.getMusicleManager().onMusicleCommand(event);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }

        if (message.startsWith("coinflip")) {
            manager.getRngManager().coinFlip(event);
            return;
        }

        if (message.startsWith("roll")) {
            manager.getRngManager().diceRoll(event);
            return;
        }

        if (message.startsWith("help")) {
            manager.getHelpManager().onHelpCommand(event);
            return;
        }

        if (message.startsWith("tictactoe")) {
            manager.getTicTacToeManager().onTicTacToeCommand(event);
            return;
        }

        if (message.startsWith("chess")) {
            manager.getChessManager().onChessCommand(event);
            return;
        }

        if (manager.getChessManager().userIsInMatch(event.getAuthor())) {
            manager.getChessManager().onMoveCommand(event);
            return;
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Guild guild = event.getGuild();
        guild.addRoleToMember(event.getMember(), guild.getRoleById(698247997091479652L)).queue();
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        ManagerManager manager = this.RegisterGuildAndGetManager(event);

        Runnable scheduleLeave = () -> {
            AudioChannel channel = event.getGuild().getAudioManager().getConnectedChannel();
            if (channel == event.getChannelLeft()) {
                if (channel.getMembers().size() == 1) {
                    manager.getMusicManager().silentDisconnect();
                }
            }
        };
        this.taskScheduler.schedule(scheduleLeave, 30);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        ManagerManager manager = this.RegisterGuildAndGetManager(event);

        if (event.getButton().getId().startsWith("queue")) {
            manager.getMusicManager().onQueueButton(event);
        }
        if (event.getButton().getId().startsWith("chess")) {
            manager.getChessManager().onChessButton(event);
        }
        if (manager.getMusicleManager().isInGame()) {
            manager.getMusicleManager().onButtonInteraction(event);
        }
        if (manager.getTicTacToeManager().isInGame()) {
            manager.getTicTacToeManager().onButtonInteraction(event);
        }
    }

    private ManagerManager RegisterGuildAndGetManager(GenericGuildEvent event) {
        Guild guild = event.getGuild();
        Long guildId = guild.getIdLong();
        if (this.managers.get(guildId) == null) {
            this.managers.put(guildId, new ManagerManager(guild));
        }

        return this.managers.get(guildId);
    }

    private ManagerManager RegisterGuildAndGetManager(GenericMessageEvent event) {
        Guild guild = event.getGuild();
        Long guildId = guild.getIdLong();
        if (this.managers.get(guildId) == null) {
            this.managers.put(guildId, new ManagerManager(guild));
        }

        return this.managers.get(guildId);
    }

    private ManagerManager RegisterGuildAndGetManager(GenericInteractionCreateEvent event) {
        Guild guild = event.getGuild();
        Long guildId = guild.getIdLong();
        if (this.managers.get(guildId) == null) {
            this.managers.put(guildId, new ManagerManager(guild));
        }

        return this.managers.get(guildId);
    }
}
