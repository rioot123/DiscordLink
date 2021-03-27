package net.dirtcraft.discordlink.common.utility;

import net.dirtcraft.discordlink.common.storage.PluginConfiguration;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class JdaSupplier {
    private final long startTime = System.currentTimeMillis();
    private final ConfigurationLoader<CommentedConfigurationNode> loader;
    private final Queue<Consumer<JDA>> onJdaInit = new ConcurrentLinkedQueue<>();
    private CompletableFuture<JDA> jda;
    Collection<GatewayIntent> intents = Arrays.asList(
            GatewayIntent.GUILD_MEMBERS,
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.DIRECT_MESSAGES
    );

    public JdaSupplier(ConfigurationLoader<CommentedConfigurationNode> loader){
        this.loader = loader;
        update();
        initialize();
    }

    public JDA getJDA() {
        while (!jda.isDone() && !initTimeExceeded()){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) { }
        }
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
            JDA jda = JDABuilder.createDefault(PluginConfiguration.Main.TOKEN, intents)
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
                Utility.trySleep(150000);
                update();
            } else if (loops > 0) {
                Utility.trySleep(30000);
                update();
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

    public void update() {
        try {
            CommentedConfigurationNode node = loader.load();
            node.set(PluginConfiguration.class, new PluginConfiguration());
            loader.save(node);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
