package discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class Bot {
    public static Music music;
    public static void main(String[] args) throws Exception {
        System.out.println("Start");
        String token = "NTM1NzQwNTgzMzYxMjQ5MzAw.XEGRTA.-tqB7IVNgyM0P2TFhYAz3416TMc";
        JDA jda = JDABuilder.createDefault(token).build();
        jda.addEventListener(new GetMessage());
        System.out.println("Bot running");
        music = new Music();
    }
}
