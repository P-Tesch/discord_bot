package com.tesch.application;

import com.tesch.api.EventListeners;
import com.tesch.api.SongQueue;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.security.auth.login.LoginException;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {

    public static void main(String[] args) throws LoginException, InterruptedException, IOException, FileNotFoundException {

        JDA builder = JDABuilder
        .createDefault("MTAwMzY0MzgyOTgwMDQwNzA1MA.GaUg7O.lEZWCq9R72tDD7f4D5E63z5LVnDTkYJ1i525d8")
        .enableIntents(getIntents())
        .setActivity(Activity.listening("Boate Azul"))
        .build();

        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        SongQueue queue = new SongQueue();

        builder.addEventListener(new EventListeners(builder, playerManager, queue));
        builder.awaitReady();
    }

    private static String readId() throws IOException, FileNotFoundException {
        try (BufferedReader br = new BufferedReader(new FileReader("D:\\Personal Projects\\Java Projects\\Discord Bot\\tesch_discord_bot\\id.txt"));){
            return br.readLine();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
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
