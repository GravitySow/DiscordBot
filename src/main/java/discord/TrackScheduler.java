package discord;

import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private final BlockingQueue<TextChannel> channels;

    @Override
    public void onPlayerPause(AudioPlayer player) {
        // Player was paused
        player.setPaused(true);
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        // Player was resumed
        player.setPaused(false);
    }
    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
        channels = new LinkedBlockingQueue<>();
    }

    public void queue(AudioTrack track,TextChannel channel) {
        if (!player.startTrack(track, true)) {
            queue.offer(track);
            channels.offer(channel);
        }
    }

    public void nextTrack() {
        if(queue.peek() != null){
            channels.poll().sendMessage("Playing: "+queue.peek().getInfo().title).queue();
        }
        player.startTrack(queue.poll(), false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }

    public void clear(){
        System.out.println("Start clear");
        channels.clear();
        queue.clear();
        player.stopTrack();
        System.out.println("Clear");
    }

    public void playlist(TextChannel channel){
        new Thread(() ->{
            int count = 0;
            for(AudioTrack i:queue){
                channel.sendMessage(++count+": "+i.getInfo().title).queue();
            }
            channel.sendMessage("Total songs in playlist: "+count).queue();
        })
        .run();
    }

}