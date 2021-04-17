package discord;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord.youtube.Search;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.io.File;
import java.util.*;

public class Music {
    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;
    public Music() {
        this.musicManagers = new HashMap<>();

        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);

    }
    public void play(MessageReceivedEvent event, String msg) {
        String m[] = msg.split(" ");
        String url;
        if(m.length != 2) return;
        if(!m[1].startsWith("https://")){
            String x = "";
            for(int i = 1;i < m.length;i++){
                x += m[i]+" ";
            }
            url = new Search().keywordSearch(x);
        }else{
            url = m[1];
        }
        loadAndPlay(event, url);
    }

    public void skip(MessageReceivedEvent event){
        TextChannel channel = event.getTextChannel();
        skipTrack(channel);
    }

    private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    private void loadAndPlay(MessageReceivedEvent event, final String trackUrl) {
        final TextChannel channel = event.getTextChannel();
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                channel.sendMessage("Adding to queue " + track.getInfo().title).queue();

                play(event, musicManager, track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                channel.sendMessage("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").queue();

                int count = 0;
                for (AudioTrack track : playlist.getTracks()) {
                    play(event, musicManager,track);
                    ++count;
                }
                channel.sendMessage("Total songs in playlist: "+count).queue();
            }

            @Override
            public void noMatches() {
                channel.sendMessage("Nothing found by " + trackUrl).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessage("Could not play: " + exception.getMessage()).queue();
            }
        });
    }

    public void random(int n,MessageReceivedEvent event){

        try {
            File file = new File("src\\main\\java\\discord\\music.txt");
            Scanner myReader = new Scanner(file);
            ArrayList<String> music = new ArrayList<>();
            while (myReader.hasNextLine()){
                music.add(myReader.nextLine());
            }
            myReader.close();

            for(int i = 0;i < n;i++){
                int s = music.size();
                Random random = new Random();
                int x = random.nextInt(s);
                String p = music.get(x);
                music.remove(x);
                --s;
                loadAndPlay(event,p);
            }
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private void play(MessageReceivedEvent event, GuildMusicManager musicManager, AudioTrack track) { ;
        TextChannel channel = event.getTextChannel();
        Guild guild = channel.getGuild();
        connectToFirstVoiceChannel(guild.getAudioManager(),event);
        musicManager.scheduler.queue(track,channel);
    }

    private void skipTrack(TextChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

        channel.sendMessage("Skipped to next track.").queue();
        musicManager.scheduler.nextTrack();
    }

    private static void connectToFirstVoiceChannel(AudioManager audioManager,MessageReceivedEvent event) {
        boolean join = false;
        if (!audioManager.isConnected()) {
            for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
                for (Member member : voiceChannel.getMembers()) {
                    //System.out.println(voiceChannel+" "+member);
                    if (member == event.getMember()) {
                        System.out.println(event.getMember());
                        audioManager.openAudioConnection(voiceChannel);
                        join = true;
                        break;
                    }
                }
            }
            if (!join){
                event.getChannel().sendMessage("Please join voice channel").queue();
            }
        }
    }
    public void clear(TextChannel channel){
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.clear();

        channel.sendMessage("Clear Playlist.").queue();
    }
    public void playlist(TextChannel channel){
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.playlist(channel);
    }
}
