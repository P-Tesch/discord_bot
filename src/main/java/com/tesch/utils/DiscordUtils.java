package com.tesch.utils;

import java.util.function.Consumer;

import com.tesch.exceptions.BotException;
import com.tesch.music.MusicPlayerSendHandler;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

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

    public static MessageCreateData sendMessage(String content, TextChannel text) {
        MessageCreateData message = new MessageCreateBuilder().addContent(content).build();
        text.sendMessage(message).queue();
        return message;
    }

    public static <T> MessageCreateData sendMessage(String content, Consumer<? super Message> consumer, TextChannel text) {
        MessageCreateData message = new MessageCreateBuilder().addContent(content).build();
        text.sendMessage(message).queue(consumer);
        return message;
    }
}
