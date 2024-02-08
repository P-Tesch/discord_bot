package com.tesch;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import com.tesch.db.EpicGamesRequester;
import com.tesch.managers.ManagerManager;
import com.tesch.managers.PlayerChannelManager;
import com.tesch.utils.DiscordUtils;
import com.tesch.utils.TaskScheduler;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
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
        String command = message.split(" ")[0];

        if (event.getChannel().getName().equalsIgnoreCase(PlayerChannelManager.CHANNEL_NAME)) {
            manager.getPlayerChannelManager().onPlayCommand(event);
            return;
        }

        switch (command) {
            case "play":
                manager.getChatMusicManager().onPlayCommand(event);
                break;
            case "volume":
                manager.getChatMusicManager().onVolumeCommand(event);
                break;
            case "pause":
                manager.getChatMusicManager().onPauseCommand(event);
                break;
            case "disconnect":
                manager.getChatMusicManager().onDisconnectCommand(event);
                break;
            case "skip": 
                manager.getChatMusicManager().onSkipCommand(event);
                break;
            case "previous":
                manager.getChatMusicManager().onPreviousCommand(event);
                break;
            case "queue":
                manager.getChatMusicManager().onQueueCommand(event);
                break;
            case "clear":
                manager.getChatMusicManager().onClearCommand(event);
                break;
            case "loop":
                manager.getChatMusicManager().onLoopCommand(event);
                break;
            case "shuffle":
                manager.getChatMusicManager().onShuffleCommand(event);
                break;
            case "jumpto":
                manager.getChatMusicManager().onJumpToCommand(event);
                break;
            case "coinflip":
                manager.getRngManager().coinFlip(event);
                break;
            case "roll":
                manager.getRngManager().diceRoll(event);
                break;
            case "help":
                manager.getHelpManager().onHelpCommand(event);
                break;
            case "tictactoe":
                manager.getTicTacToeManager().onTicTacToeCommand(event);
                break;
            case "chess":
                manager.getChessManager().onChessCommand(event);
                break;
            case "musicle":
                manager.getMusicleManager().onMusicleCommand(event);
                break;
            case "score":
                manager.getBotuserManager().onScoreCommand(event);
                break;
            case "trivia":
                manager.getTriviaManager().onTriviaCommand(event);
                break;
            case "currency":
                manager.getBotuserManager().onCurrencyCommand(event);
                break;
            case "pix":
                manager.getBotuserManager().onPixCommand(event);
                break;
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Guild guild = event.getGuild();
        guild.addRoleToMember(event.getMember(), guild.getRoleById(698247997091479652L)).queue();
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        if (event.getChannelLeft() == null) {
            return;
        }
        ManagerManager manager = this.RegisterGuildAndGetManager(event);

        Runnable scheduleLeave = () -> {
            AudioChannel channel = event.getGuild().getAudioManager().getConnectedChannel();
            if (channel == event.getChannelLeft()) {
                if (channel.getMembers().size() == 1) {
                    manager.getChatMusicManager().getMusicPlayer().forceClearDisconnect();
                }
            }
        };
        this.taskScheduler.schedule(scheduleLeave, 30);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        ManagerManager manager = this.RegisterGuildAndGetManager(event);

        switch(event.getButton().getId().split("_")[0]) {
            case "queue":
                manager.getChatMusicManager().onQueueButton(event);
                break;
            case "chess":
                manager.getChessManager().onChessButton(event);
                break;
            case "musicle":
                manager.getMusicleManager().onButtonInteraction(event);
                break;
            case "tictactoe":
                manager.getTicTacToeManager().onButtonInteraction(event);
                break;
            case "playerchannel":
                manager.getPlayerChannelManager().onButtonInteraction(event);
                break;
            case "trivia":
                manager.getTriviaManager().onButtonInteraction(event);
                break;
        }
    }

    private void onThursdayAfternoon(JDA jda) {
        EpicGamesRequester.getFreeGames().forEach(message -> DiscordUtils.sendMessage(message, jda.getTextChannelById(568182843360935936L)));
        this.scheduleThursday(jda);
    }

    public void scheduleThursday(JDA jda) {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime nextThursday = LocalDateTime.now().withHour(14).withMinute(0).withSecond(0).withNano(0);
        while (currentTime.until(nextThursday, ChronoUnit.SECONDS) <= 0 || nextThursday.getDayOfWeek() != DayOfWeek.THURSDAY) {
            nextThursday = nextThursday.plusDays(1);
        }
        this.taskScheduler.scheduleOffList(() -> this.onThursdayAfternoon(jda), currentTime.until(nextThursday, ChronoUnit.SECONDS));
        
        System.out.println("[INFO] Free game task scheduled to " + nextThursday.toString());
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
