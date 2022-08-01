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
    private SongQueue queue;

    public EventListeners(JDA jda, AudioPlayerManager playerManager, SongQueue queue) {
        this.jda = jda;
        this.playerManager = playerManager;
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
    }
    
    private void onMention(MessageReceivedEvent event) {
        event.getChannel().sendMessage(event.getAuthor().getAsMention()).queue();
    }

    private void onPlayCommand(MessageReceivedEvent event) {
        String source = event.getMessage().getContentRaw().replace("play ", "");
        TextChannel textChannel = event.getMessage().getChannel().asTextChannel();
        AudioChannel voiceChannel = event.getMember().getVoiceState().getChannel();
        AudioManager audioManager = event.getGuild().getAudioManager();
        AudioPlayer audioPlayer = playerManager.createPlayer();
        audioManager.setSendingHandler(new AudioPlayerSendHandler(audioPlayer));
        audioPlayer.addListener(this.queue);
        AudioResultHandler resultHandler = new AudioResultHandler(textChannel, queue, audioPlayer);

        playerManager.loadItem(source, resultHandler);
        audioManager.openAudioConnection(voiceChannel);
    }
}
