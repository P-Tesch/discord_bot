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
    }
    
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        if (event.getMessage().getMentions().isMentioned(jda.getSelfUser(), MentionType.USER)) {
            this.onMention(event);
            return;
        }

        if (event.getMessage().getContentRaw().startsWith("play ")) {
            this.onPlayCommand(event);
            return;
        }

        if (event.getMessage().getContentRaw().startsWith("volume")) {
            this.onVolumeCommand(event);
            return;
        }

        if (event.getMessage().getContentRaw().startsWith("pause")) {
            this.onPauseCommand();
            return;
        }

        if (event.getMessage().getContentRaw().startsWith("disconnect")) {
            this.onDisconnectCommand(event);
            return;
        }
    }
    
    private void onMention(MessageReceivedEvent event) {
        event.getChannel().sendMessage(event.getAuthor().getAsMention()).queue();
    }

    private void onDisconnectCommand(MessageReceivedEvent event) {
        event.getGuild().getAudioManager().closeAudioConnection();
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
        String source = event.getMessage().getContentRaw().replace("play ", "");
        TextChannel textChannel = event.getMessage().getChannel().asTextChannel();
        AudioChannel voiceChannel = event.getMember().getVoiceState().getChannel();
        AudioManager audioManager = event.getGuild().getAudioManager();
        audioManager.setSendingHandler(new AudioPlayerSendHandler(audioPlayer));
        audioPlayer.addListener(this.queue);
        AudioResultHandler resultHandler = new AudioResultHandler(textChannel, queue, audioPlayer);

        playerManager.loadItem(source, resultHandler);
        audioManager.openAudioConnection(voiceChannel);
    }
}
