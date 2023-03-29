package com.tesch.managers;

import com.tesch.exceptions.MusicleException;
import com.tesch.music.MusicChatResultHandler;
import com.tesch.music.MusicPlayer;
import com.tesch.utils.DiscordUtils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class ChatMusicManager extends MusicManager {

    public ChatMusicManager(MusicPlayer musicPlayer, Guild guild) {
        super(musicPlayer, guild);
    }
    
    public void onPlayCommand(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw().replace("play ", "");
        TextChannel text = event.getChannel().asTextChannel();

        try {
            this.getMusicPlayer().play(event.getMember().getVoiceState().getChannel(), message, new MusicChatResultHandler(text, this.getMusicPlayer().getQueue()));
        }
        catch (MusicleException e) {
            DiscordUtils.sendMessage(e.getMessage(), text);
        }
    }

    public void onVolumeCommand(MessageReceivedEvent event) {
        String[] volume = event.getMessage().getContentRaw().split(" ");
        TextChannel text = event.getChannel().asTextChannel();
        if (volume.length == 1) {
            DiscordUtils.sendMessage("Current volume: " + this.getMusicPlayer().getAudioPlayer().getVolume(), text);
        }
        else {
            this.getMusicPlayer().getAudioPlayer().setVolume(Integer.parseInt(volume[1]));
            DiscordUtils.sendMessage("Volume set to: " + this.getMusicPlayer().getAudioPlayer().getVolume(), text);
        }
    }

    public void onPauseCommand(MessageReceivedEvent event) {
        TextChannel text = event.getChannel().asTextChannel();
        try {
            this.getMusicPlayer().pause();
        }
        catch (MusicleException e) {
            DiscordUtils.sendMessage(e.getMessage(), text);
        }
    }

    public void onDisconnectCommand(MessageReceivedEvent event) {
        this.getMusicPlayer().disconnect();
        this.onClearCommand(event);
    }

    public void onSkipCommand(MessageReceivedEvent event) {
        TextChannel text = event.getChannel().asTextChannel();
        
        try {
            this.getMusicPlayer().skip();
        }
        catch (MusicleException e) {
            DiscordUtils.sendMessage(e.getMessage(), text);
        }
    }

    public void onQueueCommand(MessageReceivedEvent event) {
        TextChannel text = event.getChannel().asTextChannel();
        try {
            event.getChannel().sendMessage("```\n" + this.getMusicPlayer().queue(1) + "\n```").setActionRow(Button.primary("queue previous", "⬅️"), Button.primary("queue next", "➡️")).queue();
        }
        catch (MusicleException e) {
            DiscordUtils.sendMessage(e.getMessage(), text);
        }
        catch (NullPointerException e) {
            DiscordUtils.sendMessage("Queue empty", text);
        }
    }

    public void onQueueButton(ButtonInteractionEvent event) {
        Integer page = Integer.parseInt(event.getMessage().getContentRaw().split("\n")[1].split(" ")[1].replace(":", ""));
        page = event.getButton().getId().equals("queue next") ? page + 1 : page - 1;
        event.editMessage("```" + this.getMusicPlayer().queue(page) + "```").queue();
    }

    public void onClearCommand(MessageReceivedEvent event) {
        TextChannel text = event.getChannel().asTextChannel();
        
        try {
            this.getMusicPlayer().clear();
            DiscordUtils.sendMessage("Cleared queue", text);
        }
        catch (MusicleException e) {
            DiscordUtils.sendMessage(e.getMessage(), text);
        }
    }

    public void onLoopCommand(MessageReceivedEvent event) {
        TextChannel text = event.getChannel().asTextChannel();
        try {
            this.getMusicPlayer().loop();
            DiscordUtils.sendMessage("Loop set to " + this.getMusicPlayer().getQueue().getLoop(), text);
        }
        catch (MusicleException e) {
            DiscordUtils.sendMessage(e.getMessage(), text);
        }
    }

    public void onShuffleCommand(MessageReceivedEvent event) {
        TextChannel text = event.getChannel().asTextChannel();
        try {
            this.getMusicPlayer().shuffle();
            DiscordUtils.sendMessage("Queue shuffled", text);
        }
        catch (MusicleException e) {
            DiscordUtils.sendMessage(e.getMessage(), text);
        }
    }

    public void onJumpToCommand(MessageReceivedEvent event) {
        for (int i = 0; i < Integer.parseInt(event.getMessage().getContentRaw().split(" ")[1]); i++) {
            this.onSkipCommand(event);
        }
    }
}
