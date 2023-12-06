package com.tesch.games.quiz.musicle;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.tesch.db.MusicleRequester;
import com.tesch.db.entities.Genre;
import com.tesch.db.entities.Interpreter;
import com.tesch.db.entities.Song;
import com.tesch.games.quiz.QuizLobby;
import com.tesch.games.quiz.QuizManager;
import com.tesch.games.quiz.QuizQuestion;
import com.tesch.music.MusicPlayer;
import com.tesch.music.MusicPlayerSendHandler;
import com.tesch.utils.DiscordUtils;
import com.tesch.utils.TaskScheduler;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MusicleManager extends QuizManager {

    private MusicPlayer player;
    private TaskScheduler scheduler;
    
    public MusicleManager(MusicPlayer player) {
        super(Color.green, "Musicle");
        this.player = player;
        this.scheduler = new TaskScheduler();
    }

    public void onMusicleCommand(MessageReceivedEvent event) {
        if (this.player.getAudioPlayer().getPlayingTrack() != null) {
            return;
        }

        String[] split = event.getMessage().getContentRaw().split(" ");
        String genreName = split.length > 1 ? split[1] : null;
        Genre genre = this.validadeGenre(genreName);

        List<Song> songs = MusicleRequester.getSongsByGenre(genre);
        Song song = songs.get((int)(Math.random() * (songs.size() - 1)));
        Set<Interpreter> interpreters = songs.stream().map(Song::getInterpreters).map(x -> x.get(0)).collect(Collectors.toSet());
        song.getInterpreters().forEach(interpreters::remove);

        Map<String, Boolean> answers = new HashMap<>();
        List<Interpreter> songInterpreters = song.getInterpreters();
        answers.put(songInterpreters.get((int)(Math.random() * (songInterpreters.size() - 1))).getName(), true);
        int interpretersLenght = interpreters.size();
        int answerAmount = 4;
        for (Interpreter interpreter : interpreters) {
            double r = Math.random();
            if (r <= answerAmount / interpretersLenght) {
                answers.put(interpreter.getName(), false);
                answerAmount--;
            }
            interpretersLenght--;
        }
        
        TextChannel text = event.getChannel().asTextChannel();
        DiscordUtils.connectToVoice(new MusicPlayerSendHandler(this.player.getAudioPlayer()), event.getGuild(), event.getMember().getVoiceState().getChannel());

        MusicleResultHandler resultHandler = new MusicleResultHandler(text, player.getQueue());
        Future<Void> wait = player.getPlayerManager().loadItem(song.getUrl(), resultHandler);
        this.player.setMusicleMode(true);

        byte i = 0;
        while (!wait.isDone()) {
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (i >= 10) {
                DiscordUtils.sendMessage("Failed", text);
                this.stop();
                return;
            }
            i++;
        }
        this.player.getAudioPlayer().getPlayingTrack().setPosition((long)(Math.random() * (this.player.getAudioPlayer().getPlayingTrack().getDuration() - 45000)));

        Set<User> players = new HashSet<>(event.getMessage().getMentions().getUsers());
        players.add(event.getAuthor());
        QuizQuestion question = new QuizQuestion("Quem está cantando?", answers);
        QuizLobby lobby = this.createLobby(question, text, players);
        this.scheduler.schedule(() -> {lobby.finish();}, 30);
    }

    private Genre validadeGenre(String genreName) {
        List<Genre> genres = MusicleRequester.getAllGenres();
        for (Genre genre : genres) {
            if (genre.getName().equalsIgnoreCase(genreName)) {
                return genre;
            }
        }
        return genres.get((int)(Math.random() * (genres.size() - 1)));
    }

    public void stop() {
        this.scheduler.cancelAll();
        this.player.setMusicleMode(false);
        this.player.getQueue().clearPlaylist();
    }
}
