package discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import java.util.EventListener;

public class Bot implements EventListener {
    public static Music music;
    public static void main(String[] args) throws Exception {
        System.out.println("Start");
<<<<<<< HEAD
        String token = "NTM1NzQwNTgzMzYxMjQ5MzAw.XEGRTA.0bmymI1nQrIyCQJOP_LYHNtKCQc";
=======
        String token = "Token";
>>>>>>> 4d0360018151d78c55208d955648dc371124e844
        JDA jda = JDABuilder.createDefault(token).addEventListeners(new GetMessage()).build();
        //jda.addEventListener(new GetMessage());
        jda.awaitReady();
        System.out.println("Bot running");
        music = new Music();
    }
}
