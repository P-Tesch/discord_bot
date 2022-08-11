package com.tesch.api.games.musicle;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.tesch.api.music.MusicPlayerSendHandler;
import com.tesch.api.utils.MiscUtils;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class MusicleLobby {

    private MusicleManager manager;
    private MessageReceivedEvent event;
    private Set<User> players;
    private Map<User, Integer> selections;

    public MusicleLobby(MusicleManager manager, MessageReceivedEvent event) {
        this.manager = manager;
        this.event = event;
        this.players = new HashSet<>();
        this.selections = new HashMap<>();
    }
    
    public synchronized void setup(String url) throws InterruptedException {
        this.manager.getScheduler().cancelAll();
        this.manager.getMusicManager().setMusicleMode(true);

        players.add(event.getAuthor());
        event.getMessage().getMentions().getUsers().forEach(players::add);
        if (this.players.size() + 1 > 9) {
            this.manager.getDiscordUtils().sendMessage("Musicle lobby max players is 9");
            this.manager.stop();
            return;
        }
        players.forEach(player -> this.manager.setupPlayerScore(player));

        this.manager.getDiscordUtils().connectToVoice(new MusicPlayerSendHandler(this.manager.getMusicManager().getAudioPlayer()));

        MusicleResultHandler resultHandler = new MusicleResultHandler(event.getChannel().asTextChannel(), this.manager.getMusicManager().getQueue(), this.manager);
        Future<Void> wait = this.manager.getMusicManager().getPlayerManager().loadItem(url, resultHandler);

        int i = 0;
        while (!wait.isDone()) {
            Thread.sleep(500L);
            if (i >= 10) {
                this.manager.getDiscordUtils().sendMessage("Failed");
                this.manager.stop();
                return;
            }
            i++;
        }
    
        if (!this.manager.getStartMode()) {
            AudioTrack track = this.manager.getMusicManager().getAudioPlayer().getPlayingTrack();
            track.setPosition(MiscUtils.randomInt(0, (int) (3 * track.getDuration() / 4))); 
        }

        i = 0;
        while (this.manager.getStringBuilder() == null) {
            Thread.sleep(500L);
            if (i >= 10) {
                this.manager.getDiscordUtils().sendMessage("Failed");
                this.manager.stop();
                return;
            }
            i++;
        }

        event.getChannel().sendMessage(this.manager.getStringBuilder()).setActionRow(
            Button.primary("1", Emoji.fromUnicode("1️⃣")),
            Button.primary("2", Emoji.fromUnicode("2️⃣")),
            Button.primary("3", Emoji.fromUnicode("3️⃣")),
            Button.primary("4", Emoji.fromUnicode("4️⃣")),
            Button.primary("5", Emoji.fromUnicode("5️⃣"))
            )
        .queue();
        this.timeLimit();
    }

    protected void onButtonInteraction(ButtonInteractionEvent event) {
        if (this.players.size() == 0) {
            event.editMessage("Time's up").queue(msg -> event.getMessage().editMessageEmbeds().setActionRows().queue());
        }
        if (this.selections.keySet().contains(event.getUser())) {
            return;
        }
        this.selections.putIfAbsent(event.getUser(), Integer.parseInt(event.getButton().getId()));
    
        if (this.selections.keySet().containsAll(this.players)) {
            this.stop(event);
        }
        event.getMessage().reply(event.getUser().getAsMention() + " Voted").queue();
    }

    private void stop(ButtonInteractionEvent event) {
        StringBuilder builder = new StringBuilder();
        this.manager.getAnswers()[this.manager.getAnswerIndex()] = this.manager.getAnswers()[this.manager.getAnswerIndex()] + " ✅";
        this.selections.forEach((player, selection) -> {
            this.manager.getAnswers()[selection - 1] = this.manager.getAnswers()[selection - 1] + " " + player.getAsMention();
            if (selection == this.manager.getAnswerIndex() + 1) {
                this.manager.getPlayerScore().get(player).addWin();
            }
            else {
                this.manager.getPlayerScore().get(player).addLoss();
            }
        });
        Arrays.asList(this.manager.getAnswers()).forEach(x -> builder.append(x + "\n"));
        builder.append("\nTitle: " + this.manager.getMusicManager().getAudioPlayer().getPlayingTrack().getInfo().title);
        event.editMessage(builder.toString()).queue(msg -> event.getMessage().editMessageEmbeds().setActionRows().queue());
        this.manager.stop();
    }

    private void timeLimit() {
        this.manager.getScheduler().schedule(() -> this.manager.stop(), 45);
    }
}
