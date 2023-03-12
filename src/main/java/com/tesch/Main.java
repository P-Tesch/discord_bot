package com.tesch;

import com.tesch.api.EventListeners;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {

    public static void main(String[] args) throws LoginException, InterruptedException, IOException, FileNotFoundException {

        // https://github.com/DV8FromTheWorld/JDA/issues/1858 -> needed for fly.io single core
        final int cores = Runtime.getRuntime().availableProcessors();
        if (cores <= 1) {
            System.out.println("Available Cores \"" + cores + "\", setting Parallelism Flag");
            System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "1");
        }

        JDA jda = JDABuilder
        .createDefault(System.getenv("DISCORD_TOKEN"))
        .enableIntents(getIntents())
        .setActivity(Activity.listening("Boate Azul"))
        .addEventListeners(new EventListeners())
        .build()
        .awaitReady();

        System.out.println("Logged as: " + jda.getSelfUser().getAsTag());
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
