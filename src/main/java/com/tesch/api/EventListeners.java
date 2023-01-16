package com.tesch.api;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

import com.tesch.api.games.RNGManager;
import com.tesch.api.games.chess.ChessManager;
import com.tesch.api.games.musicle.MusicleManager;
import com.tesch.api.games.tictactoe.TicTacToeManager;
import com.tesch.api.music.MusicManager;
import com.tesch.api.utils.TaskScheduler;

import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EventListeners extends ListenerAdapter {

    private MusicManager musicManager;
    private MusicleManager musicleManager;
    private RNGManager rngManager;
    private HelpManager helpManager;
    private TicTacToeManager ticTacToeManager;
    private TaskScheduler taskScheduler;
    private ChessManager chessManager;

    public EventListeners(ManagerFactory managerFactory) {
        this.musicManager = managerFactory.buildMusicManager();
        this.musicleManager = managerFactory.buildMusicleManager(this.musicManager);
        this.rngManager = managerFactory.buildRngManager();
        this.helpManager = managerFactory.buildHelpManager();
        this.ticTacToeManager = managerFactory.buildTicTacToeManager();
        this.chessManager = managerFactory.buildChessManager();
        this.taskScheduler = new TaskScheduler();
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
            musicManager.onQueueCommand(event);
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

        if (message.startsWith("jumpto")) {
            musicManager.onJumpToCommand(event);
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
            return;
        }

        if (message.startsWith("roll")) {
            rngManager.diceRoll(event);
            return;
        }

        if (message.startsWith("help")) {
            helpManager.onHelpCommand(event);
            return;
        }

        if (message.startsWith("tictactoe")) {
            ticTacToeManager.onTicTacToeCommand(event);
            return;
        }

        if (message.startsWith("chess")) {
            chessManager.onChessCommand(event);
            return;
        }

        if (this.chessManager.userIsInMatch(event.getAuthor())) {
            this.chessManager.onMoveCommand(event);
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
        Runnable scheduleLeave = () -> {
            AudioChannel channel = event.getGuild().getAudioManager().getConnectedChannel();
            if (channel == event.getChannelLeft()) {
                if (channel.getMembers().size() == 1) {
                    this.musicManager.onDisconnectCommand();
                }
            }
        };
        this.taskScheduler.schedule(scheduleLeave, 30);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getButton().getId().startsWith("queue")) {
            this.musicManager.onQueueButton(event);
        }
        if (event.getButton().getId().startsWith("chess")) {
            this.chessManager.onChessButton(event);
        }
        if (this.musicleManager.isInGame()) {
            this.musicleManager.onButtonInteraction(event);
        }
        if (this.ticTacToeManager.isInGame()) {
            this.ticTacToeManager.onButtonInteraction(event);
        }
    }
}
