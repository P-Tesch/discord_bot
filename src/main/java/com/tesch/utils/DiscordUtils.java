package com.tesch.utils;

import java.util.function.Consumer;

import com.tesch.exceptions.BotException;
import com.tesch.music.MusicPlayerSendHandler;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class DiscordUtils {

    public static void connectToVoice(MusicPlayerSendHandler sendHandler, Guild guild, AudioChannel voice) {
        if (voice == null) {
            throw new BotException("User is not connected to a voice channel");
        }
        AudioManager audioManager = guild.getAudioManager();
        audioManager.setSendingHandler(sendHandler);
        audioManager.openAudioConnection(voice);
        audioManager.setSelfDeafened(true);
    }

    public static void disconnectFromVoice(Guild guild) {
        guild.getAudioManager().closeAudioConnection();
    }

    public static Message sendMessage(String content, TextChannel text) {
        Message message = new MessageBuilder(content).build();
        text.sendMessage(message).queue();
        return message;
    }

    public static <T> Message sendMessage(String content, Consumer<? super Message> consumer, TextChannel text) {
        Message message = new MessageBuilder(content).build();
        text.sendMessage(message).queue(consumer);
        return message;
    }
}
