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

import java.util.Random;

public class GetMessage extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent event){
        //System.out.println("xxx");
        String name = event.getAuthor().getName();
        String msg = event.getMessage().getContentDisplay();
        MessageChannel channel = event.getChannel();

        System.out.println("Get a message form "+name+": "+msg);

        if(msg.startsWith("!")){
            command(msg, name, channel, event);
        }
    }

    private void command(String msg,String name,MessageChannel channel,MessageReceivedEvent event) {
        //msg = msg.toLowerCase();
        String m[] = msg.split(" ");
        m[0] = m[0].toLowerCase();
        //System.out.println(msg);
        if (m[0].equals("!ip")) {
            channel.sendMessage("IP: " + new GetIP().getIP()).queue();
        } else if (m[0].equalsIgnoreCase("hi")) {
            channel.sendMessage("Hello " + name).queue();
        } else if (m[0].equals("!minecraft")) {
            channel.sendMessage("Minecraft Version 1.16.5").queue();
            channel.sendMessage("IP: " + new GetIP().getIP()).queue();
        } else if (m[0].equals("!ping")) {
            long time = System.currentTimeMillis();
            channel.sendMessage("Ping").queue(response /* => Message */ -> {
                response.editMessageFormat("Ping: %d ms", System.currentTimeMillis() - time).queue();
            });
        } else if (m[0].equals("!playlist")) {
            Bot.music.playlist(event.getTextChannel());
        } else if (m[0].startsWith("!play") || m[0].startsWith("!เปิดเพลง")
                || m[0].startsWith("!เปิด") || m[0].startsWith("!p") || m[0].startsWith("!ป") || m[0].startsWith("!เล่น")) {
            if (event.getAuthor().isBot()) return;
            //playMusic(msg,name,event.getTextChannel(),event);
            Bot.music.play(event, msg);
        } else if (m[0].equals("!skip") || m[0].equals("!ข้าม")
                || m[0].equals("!s") || m[0].equals("!ข") || m[0].equals("!next") || m[0].equals("!n")) {
            Bot.music.skip(event);
        } else if (m[0].equals("!clear") || m[0].equals("!c") || m[0].equals("ลบ")) {
            Bot.music.clear(event.getTextChannel());
        } else if (m[0].equals("!invite")) {
            channel.sendMessage("Link: https://discord.com/api/oauth2/authorize?client_id=535740583361249300&permissions=8&scope=bot").queue();
        } else if (m[0].equals("!random")) {
            Random random = new Random();
            channel.sendMessage(event.getAuthor().getName() + "'s Random no " + random.nextInt(12)).queue();
        } else if (m[0].startsWith("!randomsong") || msg.startsWith("!r")) {
            int n = Integer.valueOf(m[1]);
            System.out.println(n);
            Bot.music.random(n, event);
        } else if (msg.equals("!delete")) {
            for (int i = 0; i < 5; i++) {
                channel.deleteMessageById(channel.getLatestMessageIdLong()).queue();
            }
        }
    }
}
