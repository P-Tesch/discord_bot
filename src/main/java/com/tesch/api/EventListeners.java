package com.tesch.api;

import org.jetbrains.annotations.NotNull;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

public class EventListeners extends ListenerAdapter {

    private JDA jda;
    private AudioPlayerManager playerManager;
    private AudioPlayer audioPlayer;
    private SongQueue queue;

    public EventListeners(JDA jda, AudioPlayerManager playerManager, SongQueue queue) {
        this.jda = jda;
        this.playerManager = playerManager;
        this.audioPlayer = playerManager.createPlayer();
        this.queue = queue;
        this.audioPlayer.addListener(this.queue);
        this.queue.setPlayer(this.audioPlayer);
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
            this.onPlayCommand(event);
            return;
        }

        if (message.startsWith("volume")) {
            this.onVolumeCommand(event);
            return;
        }

        if (message.startsWith("pause")) {
            this.onPauseCommand();
            return;
        }

        if (message.startsWith("disconnect")) {
            this.onDisconnectCommand(event);
            return;
        }

        if (message.startsWith("skip")) {
            this.onSkipCommand(event);
            return;
        }

        if (message.startsWith("queue")) {
            this.onQueueCommand(event);
            return;
        }

        if (message.startsWith("clear")) {
            this.onClearCommand(event);
            return;
        }

        if (message.startsWith("loop")) {
            this.onLoopCommand(event);
            return;
        }
    }
    
    private void onMention(MessageReceivedEvent event) {
        event.getChannel().sendMessage(event.getAuthor().getAsMention()).queue();
    }

    private void onLoopCommand(MessageReceivedEvent event) {
        this.queue.setLoop(!this.queue.getLoop());
        event.getChannel().sendMessage("Loop set to " + this.queue.getLoop()).queue();
    }

    private void onClearCommand(MessageReceivedEvent event) {
        this.queue.clearPlaylist();
        event.getChannel().sendMessage("Cleared queue").queue();;
    }

    private void onQueueCommand(MessageReceivedEvent event) {
        StringBuilder queueString = new StringBuilder();
        this.queue.getPlaylist().stream().map(x -> x.getInfo().title).forEach(x -> queueString.append(x + "\n"));
        event.getChannel().sendMessage("Queue:\n" + queueString).queue();
    }

    private void onSkipCommand(MessageReceivedEvent event) {
        this.queue.playNextTrack(true);
    }

    private void onDisconnectCommand(MessageReceivedEvent event) {
        event.getGuild().getAudioManager().closeAudioConnection();
        this.onClearCommand(event);
    }

    private void onPauseCommand() {
        this.audioPlayer.setPaused(this.audioPlayer.isPaused() ? false : true);
    }

    private void onVolumeCommand(MessageReceivedEvent event) {
        String[] volume = event.getMessage().getContentRaw().split(" ");
        if (volume.length == 1) {
            event.getChannel().sendMessage("Current volume: " + this.audioPlayer.getVolume()).queue();
        }
        else {
            this.audioPlayer.setVolume(Integer.parseInt(volume[1]));
            event.getChannel().sendMessage("Volume set to: " + this.audioPlayer.getVolume()).queue();
        }
    }

    private void onPlayCommand(MessageReceivedEvent event) {
        String[] message = event.getMessage().getContentRaw().split(" ");

        TextChannel textChannel = event.getMessage().getChannel().asTextChannel();
        AudioChannel voiceChannel = event.getMember().getVoiceState().getChannel();
        
        AudioManager audioManager = event.getGuild().getAudioManager();
        audioManager.setSendingHandler(new AudioPlayerSendHandler(audioPlayer));
        audioManager.openAudioConnection(voiceChannel);

        AudioResultHandler resultHandler = new AudioResultHandler(textChannel, queue);

        playerManager.loadItem(message[1], resultHandler);
    }
}
