package discord;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import javax.swing.plaf.multi.MultiScrollBarUI;
import java.util.Locale;
import java.util.Random;

public class GetMessage extends ListenerAdapter {
    TrackScheduler trackScheduler;
    public void onMessageReceived(MessageReceivedEvent event){
        String name = event.getAuthor().getName();
        String msg = event.getMessage().getContentDisplay();
        MessageChannel channel = event.getChannel();

        System.out.println("Get a message form "+name+": "+msg);

        if(msg.startsWith("!")){
            command(msg, name, channel, event);
        }
    }

    private void command(String msg,String name,MessageChannel channel,MessageReceivedEvent event){
        msg = msg.toLowerCase();
        //System.out.println(msg);
        if(msg.equals("!ip")) {
            channel.sendMessage("IP: " + new GetIP().getIP()).queue();
        }else if(msg.equalsIgnoreCase("hi")){
            channel.sendMessage("Hello "+name).queue();
        }else if(msg.equals("!minecraft")) {
            channel.sendMessage("Minecraft Version 1.16.5").queue();
            channel.sendMessage("IP: " + new GetIP().getIP()).queue();
        }else if(msg.equals("!ping")){
            long time = System.currentTimeMillis();
            channel.sendMessage("Ping").queue(response /* => Message */ -> {
                response.editMessageFormat("Ping: %d ms", System.currentTimeMillis() - time).queue();
            });
        }
        else if(msg.equals("!playlist")){
            Bot.music.playlist(event.getTextChannel());
        }else if(msg.startsWith("!play") || msg.startsWith("!เปิดเพลง")
                || msg.startsWith("!เปิด") || msg.startsWith("!p") || msg.startsWith("!ป")|| msg.startsWith("!เล่น")){
            if (event.getAuthor().isBot()) return;
            //playMusic(msg,name,event.getTextChannel(),event);
            Bot.music.play(event,msg);
        }else if(msg.equals("!skip") || msg.startsWith("!ข้าม")
                || msg.equals("!s") ||msg.equals("!ข")  || msg.equals("!next") || msg.equals("!n")){
            Bot.music.skip(event);
        }else if(msg.equals("!clear") || msg.equals("!c") || msg.equals("ลบ")){
            Bot.music.clear(event.getTextChannel());
        }else if(msg.equals("!invite")){
            channel.sendMessage("Link: https://discord.com/api/oauth2/authorize?client_id=535740583361249300&permissions=8&scope=bot").queue();
        }else if(msg.equals("!random")){
            Random random = new Random();
            channel.sendMessage(event.getAuthor().getName()+"'s Random no "+random.nextInt(12)).queue();
        }else if(msg.startsWith("!randomsong") || msg.startsWith("!r")){
            String s[] = msg.split(" ");
            int n = Integer.valueOf(s[1]);
            System.out.println(n);
            Bot.music.random(n,event);
        }else if(msg.equals("!delete")){
            for(int i = 0;i < 5 ;i++){
                channel.deleteMessageById(channel.getLatestMessageIdLong()).queue();
            }

        }
    }

    public void playMusic(String msg, String name, TextChannel channel, MessageReceivedEvent event){
        Guild guild = event.getGuild();
        VoiceChannel myChannel = event.getMember().getVoiceState().getChannel();
        AudioManager manager = guild.getAudioManager();

        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioPlayer player = playerManager.createPlayer();
        TrackScheduler trackScheduler = new TrackScheduler(player);
        player.addListener(trackScheduler);

        String identifier[] = msg.split(" ");
        playerManager.loadItem(identifier[1], new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                channel.sendMessage("Adding to queue " + track.getInfo().title).queue();

                trackScheduler.queue(track,channel);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for (AudioTrack track : playlist.getTracks()) {
                    trackScheduler.queue(track,channel);
                }
            }

            @Override
            public void noMatches() {
                // Notify the user that we've got nothing
                channel.sendMessage("Music not found").queue();
            }

            @Override
            public void loadFailed(FriendlyException throwable) {
                // Notify the user that everything exploded
            }
        });

        manager.setSendingHandler(new AudioPlayerSendHandler(player));
        manager.openAudioConnection(myChannel);

    }
}
