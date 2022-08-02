package com.tesch.api.music.player;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class MusicEventHandler {

    private AudioPlayer audioPlayer;
    private AudioPlayerManager playerManager;
    private MusicQueue queue;
    
    public MusicEventHandler(AudioPlayerManager playerManager, MusicQueue queue) {
        this.playerManager = playerManager;
        this.queue = queue;

        this.audioPlayer = playerManager.createPlayer();

        this.queue.setPlayer(this.audioPlayer);
        this.audioPlayer.addListener(this.queue);
    }

    public void onPlayCommand(MessageReceivedEvent event) {
        String[] message = event.getMessage().getContentRaw().split(" ");

        TextChannel textChannel = event.getMessage().getChannel().asTextChannel();
        AudioChannel voiceChannel = event.getMember().getVoiceState().getChannel();
        
        AudioManager audioManager = event.getGuild().getAudioManager();
        audioManager.setSendingHandler(new MusicPlayerSendHandler(audioPlayer));
        audioManager.openAudioConnection(voiceChannel);

        MusicResultHandler resultHandler = new MusicResultHandler(textChannel, queue);

        playerManager.loadItem(message[1], resultHandler);
    }

    public void onVolumeCommand(MessageReceivedEvent event) {
        String[] volume = event.getMessage().getContentRaw().split(" ");
        if (volume.length == 1) {
            event.getChannel().sendMessage("Current volume: " + this.audioPlayer.getVolume()).queue();
        }
        else {
            this.audioPlayer.setVolume(Integer.parseInt(volume[1]));
            event.getChannel().sendMessage("Volume set to: " + this.audioPlayer.getVolume()).queue();
        }
    }

    public void onPauseCommand() {
        this.audioPlayer.setPaused(this.audioPlayer.isPaused() ? false : true);
    }

    public void onDisconnectCommand(MessageReceivedEvent event) {
        event.getGuild().getAudioManager().closeAudioConnection();
        this.onClearCommand(event);
    }

    public void onSkipCommand(MessageReceivedEvent event) {
        this.queue.playNextTrack(true);
    }

    public void onQueueCommand(MessageReceivedEvent event) {
        StringBuilder queueString = new StringBuilder();
        this.queue.getPlaylist().stream().map(x -> x.getInfo().title).forEach(x -> queueString.append(x + "\n"));
        event.getChannel().sendMessage("Queue:\n" + queueString).queue();
    }

    public void onClearCommand(MessageReceivedEvent event) {
        this.queue.clearPlaylist();
        event.getChannel().sendMessage("Cleared queue").queue();;
    }

    public void onLoopCommand(MessageReceivedEvent event) {
        this.queue.setLoop(!this.queue.getLoop());
        event.getChannel().sendMessage("Loop set to " + this.queue.getLoop()).queue();
    }
}
