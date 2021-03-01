package discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import java.io.File;
import java.io.IOException;
import java.util.EventListener;

public class Bot implements EventListener {
    public static Music music;
    public static void main(String[] args) throws Exception {
        System.out.println("Start");
        String token = "NTM1NzQwNTgzMzYxMjQ5MzAw.XEGRTA.-tqB7IVNgyM0P2TFhYAz3416TMc";
        JDA jda = JDABuilder.createDefault(token).addEventListeners(new GetMessage()).build();
        //jda.addEventListener(new GetMessage());
        jda.awaitReady();
        System.out.println("Bot running");
        music = new Music();
    }
}
