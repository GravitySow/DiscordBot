package discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import java.util.EventListener;

public class Bot implements EventListener {
    public static Music music;
    public static void main(String[] args) throws Exception {
        System.out.println("Start");
        String token = "NTM1NzQwNTgzMzYxMjQ5MzAw.XEGRTA.0bmymI1nQrIyCQJOP_LYHNtKCQc";
        JDA jda = JDABuilder.createDefault(token).addEventListeners(new GetMessage()).build();
        //jda.addEventListener(new GetMessage());
        jda.awaitReady();
        System.out.println("Bot running");
        music = new Music();
    }
}
