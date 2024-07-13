package com.tesch.music;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.hc.core5.http.ParseException;

import com.neovisionaries.i18n.CountryCode;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchProvider;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import com.tesch.exceptions.MusicleException;
import com.tesch.utils.DiscordUtils;

import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.clients.Music;
import dev.lavalink.youtube.clients.TvHtml5Embedded;
import dev.lavalink.youtube.clients.Web;
import dev.lavalink.youtube.clients.skeleton.Client;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;


public class MusicPlayer {
    
    private AudioPlayer audioPlayer;
    private AudioPlayerManager playerManager;
    private MusicQueue queue;
    private YoutubeSearchProvider youtubeSearch;
    private boolean musicleMode;
    private Guild guild;
    private YoutubeAudioSourceManager youtubeSource;
    private SpotifyApi spotifyApi;
    
    public MusicPlayer(AudioPlayerManager playerManager, MusicQueue queue, YoutubeSearchProvider youtubeSearch, Guild guild) {
        this.guild = guild;
        this.playerManager = playerManager;
        this.queue = queue;
        this.youtubeSearch = youtubeSearch;
        this.youtubeSource = new YoutubeAudioSourceManager(true, new Client[] {new Music(), new Web(), new TvHtml5Embedded()});
        
        try {
            spotifyApi = new SpotifyApi.Builder().setClientId(System.getenv("SPOTIFY_CLIENT_ID")).setClientSecret(System.getenv("SPOTIFY_CLIENT_SECRET")).setRedirectUri(new URI("https://github.com/P-Tesch/discord_bot")).build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        this.audioPlayer = this.playerManager.createPlayer();

        this.queue.setPlayer(this.audioPlayer);
        this.audioPlayer.addListener(this.queue);
        this.musicleMode = false;
    }

    public AudioPlayer getAudioPlayer() {
        return this.audioPlayer;
    }

    public AudioPlayerManager getPlayerManager() {
        return this.playerManager;
    }

    public MusicQueue getQueue() {
        return this.queue;
    }

    public YoutubeSearchProvider getYoutubeSearchProvider() {
        return this.youtubeSearch;
    }

    public boolean getMusicleMode() {
        return this.musicleMode;
    }

    public void setMusicleMode(boolean mode) {
        this.musicleMode = mode;
    }

    public void play(Member member, String message, AudioLoadResultHandler resultHandler) {
        if (this.musicleMode) {
            throw new MusicleException("wait for musicle to finish");
        }

        AudioChannel channel = member.getVoiceState().getChannel();
        ((MusicResultHandler) resultHandler).setLastRequester(member.getUser());

        DiscordUtils.connectToVoice(new MusicPlayerSendHandler(audioPlayer), this.guild, channel);

        if (this.isUrl(message)) {
            if (this.isSpotify(message)) {
                SpotifyResultHandler spotifyResultHandler = new SpotifyResultHandler(this.queue, this.queue.getPlayerChannelManager());
                spotifyResultHandler.setLastRequester(member.getUser());
                this.playFromSpotify(message, spotifyResultHandler);
            }
            else {
                this.playFromUrl(message, resultHandler);
            }
        }
        else {
            this.playFromSearch(message, resultHandler);
        }
    }

    public void pause() {
        if (this.musicleMode) {
            throw new MusicleException("Wait for musicle to finish");
        }
        this.audioPlayer.setPaused(this.audioPlayer.isPaused() ? false : true);
    }

    public void disconnect() {
        this.queue.clearPlayedPlaylist();
        DiscordUtils.disconnectFromVoice(this.guild);
    }

    public void skip() {
        if (this.musicleMode) {
            throw new MusicleException("wait for musicle to finish");
        }
        this.queue.playNextTrack(true);
    }

    public String queue(Integer page) {
        if (this.musicleMode) {
            throw new MusicleException("Wait for musicle to finish");
        }

        try {
            List<String> playlist = new ArrayList<>();
            playlist.add(this.audioPlayer.getPlayingTrack().getInfo().title);
            this.queue.getPlaylist().stream().map(x -> x.getInfo().title).forEach(playlist::add);

            int lastPage = playlist.size() % 7 == 0 ? playlist.size() / 7 : playlist.size() / 7 + 1;
            while (page < 0) {
                page += lastPage;
            }
            while (page > lastPage) { 
                page -= lastPage;
            }
            if (page == 0) {
                page = lastPage;
            }

            StringBuilder queueString = new StringBuilder();

            for (int i = (page - 1) * 7; i < ((page - 1) * 7) + 7; i++) {
                if (i < playlist.size()) {
                    queueString.append(i + ". " + playlist.get(i) + "\n");
                }
            }
            return "\nPage " + page + ":\n" + queueString.toString() + "\n";
        }
        catch (IllegalStateException e) {
            e.getMessage();
            return null;
        }
        catch (NullPointerException e) {
            return null;
        }
    }

    public void forceClearDisconnect() {
        this.queue.clearPlaylist();
        this.disconnect();
    }

    public void clear() {
        if (this.musicleMode) {
            throw new MusicleException("Wait for musicle to finish");
        }
        this.queue.clearPlaylist();
    }

    public void loop() {
        if (this.musicleMode) {
            throw new MusicleException("Wait for musicle to finish");
        }
        this.queue.setLoop(!this.queue.getLoop());
    }

    public void shuffle() {
        if (this.musicleMode) {
            throw new MusicleException("Wait for musicle to finish");
        }
        this.queue.shufflePlaylist();
    }

    public void previous() {
        this.queue.playPreviousTrack();
    }

    public boolean isUrl(String test) {
        try {
            URI.create(test).toURL();
            return true;
        } 
        catch (MalformedURLException e) {
            return false;
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isSpotify(String test) {
        return test.contains("open.spotify.com");
    }

    public void playFromUrl(String url, AudioLoadResultHandler resultHandler) {
        playerManager.loadItem(url, resultHandler);
    }

    public void playFromSearch(String search, AudioLoadResultHandler resultHandler) {
        BasicAudioPlaylist songs = (BasicAudioPlaylist) youtubeSearch.loadSearchResult(search, info -> this.youtubeSource.buildAudioTrack(info));
        AudioTrack song = songs.getTracks().get(0);

        playerManager.loadItem(song.getInfo().uri, resultHandler);
    }

    public void playFromSpotify(String search, AudioLoadResultHandler resultHandler) {
        try {
            this.updateSpotifyAccessToken();
            String playlistId = search.replace("https://open.spotify.com/playlist/", "").split("[?]")[0];
            Integer total = this.spotifyApi.getPlaylist(playlistId).build().execute().getTracks().getTotal();
            Integer offset = 0;
            do {
                Paging<PlaylistTrack> tracks = this.spotifyApi.getPlaylistsItems(playlistId).market(CountryCode.BR).offset(offset).build().execute();
                total -= tracks.getLimit();
                offset += offset == 0 ? tracks.getLimit() + 1 : tracks.getLimit();
                Stream.of(tracks.getItems()).forEach(item -> {
                    new Thread(() -> {
                    Track track = (Track) item.getTrack();
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append(track.getName());
                    stringBuffer.append(" ");
                    stringBuffer.append(track.getArtists()[0].getName());
                    stringBuffer.append(" ");
                    stringBuffer.append(track.getAlbum().getName());
                    playerManager.loadItem(((BasicAudioPlaylist) youtubeSearch.loadSearchResult(stringBuffer.toString(), info -> this.youtubeSource.buildAudioTrack(info))).getTracks().get(0).getInfo().uri, resultHandler);
                    }).start();
                });
            } while (total > 0);
            this.queue.getPlayerChannelManager().setFooter("Loaded playlist from spotify", 5);
        } 
        catch (ParseException | SpotifyWebApiException | IOException e) {
            e.printStackTrace();
        }
    }

    private void updateSpotifyAccessToken() {
        try {
        ClientCredentialsRequest clientCredentialsRequest = this.spotifyApi.clientCredentials().build();
            this.spotifyApi.setAccessToken(clientCredentialsRequest.execute().getAccessToken());
        } 
        catch (ParseException | SpotifyWebApiException | IOException e) {
            e.printStackTrace();
        }
    }
}
