// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.utility;

import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import java.io.IOException;
import ninja.leaping.configurate.ConfigurationNode;
import com.google.common.reflect.TypeToken;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.JDABuilder;
import net.dirtcraft.discord.spongediscordlib.Configuration.DiscordConfiguration;
import org.spongepowered.common.config.SpongeConfigManager;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.dv8tion.jda.api.requests.GatewayIntent;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.Queue;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import net.dv8tion.jda.api.JDA;
import java.util.concurrent.CompletableFuture;

public class JdaSupplier
{
    private CompletableFuture<JDA> jda;
    private long startTime;
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private Queue<Consumer<JDA>> onJdaInit;
    Collection<GatewayIntent> intents;
    
    public JdaSupplier() {
        this.startTime = System.currentTimeMillis();
        this.onJdaInit = new ConcurrentLinkedQueue<Consumer<JDA>>();
        this.intents = Arrays.asList(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES);
        this.loader = (ConfigurationLoader<CommentedConfigurationNode>)SpongeConfigManager.getSharedRoot(() -> "sponge-discord-lib").getConfig();
        this.update();
        this.initialize();
    }
    
    public JDA getJDA() {
        while (!this.jda.isDone() && !this.initTimeExceeded()) {
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException ex) {}
        }
        return this.jda.getNow(null);
    }
    
    public void getJDA(final Consumer<JDA> callback) {
        final JDA jda = this.jda.getNow(null);
        if (jda == null) {
            this.onJdaInit.add(callback);
        }
        else {
            callback.accept(jda);
        }
    }
    
    private void initialize() {
        (this.jda = CompletableFuture.supplyAsync(this::initJDA)).thenAccept((Consumer<? super JDA>)this::executeCallbacks);
    }
    
    private JDA tryGetJda() {
        try {
            final JDA jda = JDABuilder.createDefault(DiscordConfiguration.Discord.TOKEN, (Collection)this.intents).setMemberCachePolicy(MemberCachePolicy.ALL).build();
            jda.awaitStatus(JDA.Status.CONNECTED, new JDA.Status[] { JDA.Status.FAILED_TO_LOGIN });
            return jda;
        }
        catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private JDA initJDA() {
        int i = 0;
        while (true) {
            final int loops = i++;
            if (loops > 50) {
                Utility.trySleep(150000L);
                this.update();
            }
            else if (loops > 0) {
                Utility.trySleep(30000L);
                this.update();
            }
            if (this.jda.getNow(null) != null) {
                return this.jda.join();
            }
            final JDA jda = this.tryGetJda();
            if (jda != null) {
                return jda;
            }
        }
    }
    
    private void executeCallbacks(final JDA jda) {
        this.onJdaInit.forEach(consumer -> {
            try {
                consumer.accept(jda);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    private boolean initTimeExceeded() {
        final long SECOND = 1000L;
        final long MINUTE = 60000L;
        final long duration = System.currentTimeMillis() - this.startTime;
        System.out.println("JDA has taken " + duration + "ms to boot!");
        return duration > 300000L;
    }
    
    public void update() {
        try {
            final CommentedConfigurationNode node = (CommentedConfigurationNode)this.loader.load();
            node.getValue(TypeToken.of((Class)DiscordConfiguration.class), (Object)new DiscordConfiguration());
            this.loader.save((ConfigurationNode)node);
        }
        catch (IOException | ObjectMappingException ex2) {
            final Exception ex;
            final Exception exception = ex;
            exception.printStackTrace();
        }
    }
}
