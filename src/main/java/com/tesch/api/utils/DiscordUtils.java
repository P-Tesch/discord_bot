package com.tesch.api.utils;

import java.util.function.Consumer;

import com.tesch.api.exceptions.BotException;
import com.tesch.api.music.MusicPlayerSendHandler;

import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class DiscordUtils {

    private Guild guild;
    private TextChannel text;
    private AudioChannel voice;
    
    public DiscordUtils() {
    }

    public Guild getGuild() {
        return guild;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    public TextChannel getText() {
        return text;
    }

    public void setText(TextChannel text) {
        this.text = text;
    }

    public AudioChannel getVoice() {
        return voice;
    }

    public void setVoice(AudioChannel voice) {
        this.voice = voice;
    }

    public void buildFromMessageEvent(MessageReceivedEvent event) {
        this.guild = event.getGuild();
        this.text = event.getChannel().asTextChannel();
        this.voice = event.getMember().getVoiceState().getChannel();
    }

    public void connectToVoice(MusicPlayerSendHandler sendHandler) {
        if (this.voice == null) {
            throw new BotException("User is not connected to a voice channel");
        }
        AudioManager audioManager = this.guild.getAudioManager();
        audioManager.setSendingHandler(sendHandler);
        audioManager.openAudioConnection(this.voice);
        audioManager.setSelfDeafened(true);
    }

    public void disconnectFromVoice() {
        this.guild.getAudioManager().closeAudioConnection();
    }

    public void sendMessage(String message) {
        this.text.sendMessage(message).queue();
    }

    public <T> void sendMessage(String message, Consumer<? super Message> consumer) {
        this.text.sendMessage(message).queue(consumer);
    }
}
