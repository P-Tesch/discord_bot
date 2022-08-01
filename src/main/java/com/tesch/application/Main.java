package com.tesch.application;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.security.auth.login.LoginException;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.tesch.api.EventListeners;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class Main {

    public static void main(String[] args) throws LoginException, InterruptedException, IOException, FileNotFoundException {
        JDA builder = JDABuilder
        .createDefault(readId())
        .build();

        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);

        builder.addEventListener(new EventListeners(builder));
        builder.awaitReady();
    }

    private static String readId() throws IOException, FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader("D:\\Personal Projects\\Java Projects\\Discord Bot\\tesch_discord_bot\\id.txt"));
        return br.readLine();
    }
}
