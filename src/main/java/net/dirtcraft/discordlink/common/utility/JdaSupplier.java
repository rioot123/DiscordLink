package net.dirtcraft.discordlink.common.utility;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.util.Arrays;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class JdaSupplier {
    private final long startTime = System.currentTimeMillis();
    private final String token;
    private final Queue<Consumer<JDA>> onJdaInit = new ConcurrentLinkedQueue<>();
    private CompletableFuture<JDA> jda;
    Collection<GatewayIntent> intents = Arrays.asList(
            GatewayIntent.GUILD_MEMBERS,
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.DIRECT_MESSAGES
    );

    public JdaSupplier(String token){
        this.token = token;
        initialize();
    }

    public JDA getJDA() {
        while (!jda.isDone() && !initTimeExceeded()) trySleep(1000);
        return jda.getNow(null);
    }

    public void getJDA(Consumer<JDA> callback){
        JDA jda = this.jda.getNow(null);
        if (jda == null) onJdaInit.add(callback);
        else callback.accept(jda);
    }

    private void initialize() {
        this.jda = CompletableFuture.supplyAsync(this::initJDA);
        this.jda.thenAccept(this::executeCallbacks);
    }

    private JDA tryGetJda() {
        try {
            JDA jda = JDABuilder.createDefault(token, intents)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .build();
            jda.awaitStatus(JDA.Status.CONNECTED, JDA.Status.FAILED_TO_LOGIN);
            return jda;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    private JDA initJDA() {
        int i = 0;
        while (true) {
            int loops = i++;
            if (loops > 50) {
                trySleep(150000);
            } else if (loops > 0) {
                trySleep(30000);
            }
            if (jda.getNow(null) != null) return jda.join();
            JDA jda = tryGetJda();
            if (jda != null) return jda;
        }
    }

    private void executeCallbacks(JDA jda){
        onJdaInit.forEach(consumer-> {
            try{
                consumer.accept(jda);
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    private boolean initTimeExceeded(){
        final long SECOND = 1000;
        final long MINUTE = SECOND * 60;
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("JDA has taken " + duration + "ms to boot!");
        return duration > 5 * MINUTE;
    }

    public static void trySleep(long ms){
        try{
            Thread.sleep(ms);
        } catch (Exception ignored){

        }
    }
}
