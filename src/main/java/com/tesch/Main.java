package com.tesch;

import com.tesch.api.EventListeners;
import com.tesch.api.games.RNGManager;
import com.tesch.api.music.musicle.MusicleManager;
import com.tesch.api.music.player.MusicEventHandler;
import com.tesch.api.music.player.MusicQueue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.security.auth.login.LoginException;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchProvider;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {

    public static void main(String[] args) throws LoginException, InterruptedException, IOException, FileNotFoundException {

        JDA builder = JDABuilder
        .createDefault(System.getenv("DISCORD_TOKEN"))
        .enableIntents(getIntents())
        .setActivity(Activity.listening("Boate Azul"))
        .build();

        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);

        MusicEventHandler musicEventHandler = new MusicEventHandler(playerManager, new MusicQueue(), new YoutubeSearchProvider());
        MusicleManager musicleManager = new MusicleManager(musicEventHandler);
        RNGManager rngManager = new RNGManager();

        builder.addEventListener(new EventListeners(builder, musicEventHandler, musicleManager, rngManager));
        builder.awaitReady();
    }

    private static List<GatewayIntent> getIntents() {
        return  Arrays.asList(
            GatewayIntent.DIRECT_MESSAGES,
            GatewayIntent.DIRECT_MESSAGE_REACTIONS,
            GatewayIntent.DIRECT_MESSAGE_TYPING,
            GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
            GatewayIntent.GUILD_INVITES,
            GatewayIntent.GUILD_MEMBERS,
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.GUILD_MESSAGE_REACTIONS,
            GatewayIntent.GUILD_MESSAGE_TYPING,
            GatewayIntent.GUILD_PRESENCES,
            GatewayIntent.GUILD_VOICE_STATES,
            GatewayIntent.GUILD_WEBHOOKS,
            GatewayIntent.MESSAGE_CONTENT
            );
    }
}
