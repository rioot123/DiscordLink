// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.events;

import java.util.function.Consumer;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Member;
import java.util.Optional;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dirtcraft.discordlink.storage.PluginConfiguration;
import java.awt.Color;
import net.dirtcraft.discordlink.utility.Utility;
import net.dirtcraft.discordlink.DiscordLink;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GameLoadCompleteEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.dirtcraft.discordlink.utility.Pair;
import net.dv8tion.jda.api.entities.Message;
import java.util.concurrent.CompletableFuture;
import java.util.Map;
import org.spongepowered.api.GameState;

public class ServerBootHandler
{
    private final long second = 1000L;
    private final long minute = 60000L;
    private final long time;
    private volatile GameState state;
    protected boolean isReady;
    private Map<GameState, String> humanReadable;
    private CompletableFuture<Message> future;
    
    public ServerBootHandler() {
        this.time = System.currentTimeMillis();
        this.humanReadable = Stream.of((Pair[])new Pair[] { new Pair((T)GameState.CONSTRUCTION, (S)"Constructing Game Instance"), new Pair((T)GameState.PRE_INITIALIZATION, (S)"Pre-Initializing Game Instance"), new Pair((T)GameState.INITIALIZATION, (S)"Initializing Game"), new Pair((T)GameState.POST_INITIALIZATION, (S)"Post-Initializing Game"), new Pair((T)GameState.LOAD_COMPLETE, (S)"Game Loaded"), new Pair((T)GameState.SERVER_STARTED, (S)"Loading Server"), new Pair((T)GameState.SERVER_ABOUT_TO_START, (S)"Server Starting"), new Pair((T)GameState.SERVER_STARTING, (S)"Finishing Up"), new Pair((T)GameState.SERVER_STOPPING, (S)"Stopping Server"), new Pair((T)GameState.SERVER_STOPPED, (S)"Stopped Server"), new Pair((T)GameState.GAME_STOPPING, (S)"Game Stopping"), new Pair((T)GameState.GAME_STOPPED, (S)"Game Stopped") }).collect(Collectors.toMap((Function<? super Pair, ? extends GameState>)Pair::getKey, (Function<? super Pair, ? extends String>)Pair::getValue));
    }
    
    public void onGameConstruction(final GameConstructionEvent event) {
        if (!this.isReady) {
            return;
        }
        this.startTimer(event.getState());
        this.sendGameStageEmbed(event.getState());
    }
    
    @Listener(order = Order.FIRST)
    public void onGamePreInitialization(final GamePreInitializationEvent event) {
        if (!this.isReady) {
            return;
        }
        this.startTimer(event.getState());
        this.sendGameStageEmbed(event.getState());
    }
    
    @Listener(order = Order.FIRST)
    public void onGameInitialization(final GameInitializationEvent event) {
        if (!this.isReady) {
            return;
        }
        this.startTimer(event.getState());
        this.sendGameStageEmbed(event.getState());
    }
    
    @Listener(order = Order.FIRST)
    public void onGamePostInitialization(final GamePostInitializationEvent event) {
        if (!this.isReady) {
            return;
        }
        this.startTimer(event.getState());
        this.sendGameStageEmbed(event.getState());
    }
    
    @Listener(order = Order.FIRST)
    public void onGameLoadComplete(final GameLoadCompleteEvent event) {
        if (!this.isReady) {
            return;
        }
        this.startTimer(event.getState());
        this.sendGameStageEmbed(event.getState());
    }
    
    @Listener(order = Order.FIRST)
    public void onGameAboutToStartServer(final GameAboutToStartServerEvent event) {
        if (!this.isReady) {
            return;
        }
        this.startTimer(event.getState());
        this.sendGameStageEmbed(event.getState());
    }
    
    @Listener(order = Order.FIRST)
    public void onGameStartingServerEvent(final GameStartingServerEvent event) {
        if (!this.isReady) {
            return;
        }
        this.startTimer(event.getState());
        this.sendGameStageEmbed(event.getState());
    }
    
    @Listener(order = Order.POST)
    public void onGameStartedServer(final GameStartedServerEvent event) {
        if (!this.isReady) {
            return;
        }
        if (this.future != null) {
            this.future.whenComplete((message, throwable) -> message.delete().queue());
        }
        this.state = null;
        this.future = null;
        this.sendLaunchedEmbed();
    }
    
    public void sendLaunchedEmbed() {
        DiscordLink.get().getChannelManager().getGameChat().sendMessage(Utility.embedBuilder().setColor(Color.GREEN).setDescription((CharSequence)PluginConfiguration.Format.serverStart.replace("{modpack}", SpongeDiscordLib.getServerName())).build());
    }
    
    @Listener
    public void onServerStopping(final GameStoppingServerEvent event) {
        DiscordLink.get().getChannelManager().getGameChat().sendMessage(Utility.embedBuilder().setDescription((CharSequence)PluginConfiguration.Format.serverStop.replace("{modpack}", SpongeDiscordLib.getServerName())).build());
    }
    
    protected void sendGameStageEmbed(final GameState state) {
        final TextChannel gameChat = DiscordLink.get().getChannelManager().getDefaultChannel();
        final String stage = this.humanReadable.get(state);
        final int level = state.ordinal() + 1;
        final String content = String.format("The server is currently booting... Please wait...\n**%s** (%d/7)", stage, level);
        final MessageEmbed embed = Utility.embedBuilder().setColor(Color.ORANGE).setDescription((CharSequence)content).build();
        if (this.future != null) {
            this.future = this.future.whenComplete((message, throwable) -> message.delete().queue()).thenApply(message -> (Message)gameChat.sendMessage(embed).complete());
        }
        else {
            this.future = (CompletableFuture<Message>)gameChat.sendMessage(embed).submit();
        }
    }
    
    private void sendMessage(final RestAction<PrivateChannel> channelRestAction) {
        try {
            final String name = SpongeDiscordLib.getServerName();
            final String id = SpongeDiscordLib.getGamechatChannelID();
            final long ms = System.currentTimeMillis() - this.time;
            final double minutes = Math.max(ms, 1L) / 60000.0;
            final String template = "Server %s has been attempting to boot for %dms (%.1f minutes) @ <#%s>";
            final String message = String.format(template, name, ms, minutes, id);
            channelRestAction.queue(m -> m.sendMessage((CharSequence)message).queue());
        }
        catch (Exception ex) {}
    }
    
    public void startTimer(final GameState state) {
        this.state = state;
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(60000L * PluginConfiguration.Notifier.maxStageMinutes);
                if (this.state == state) {
                    if (!PluginConfiguration.Notifier.notify.contains(248056002274918400L)) {
                        PluginConfiguration.Notifier.notify.add(248056002274918400L);
                    }
                    if (!PluginConfiguration.Notifier.notify.contains(204412960427212800L)) {
                        PluginConfiguration.Notifier.notify.add(204412960427212800L);
                    }
                    PluginConfiguration.Notifier.notify.stream().map((Function<? super Object, ?>)Utility::getMemberById).filter(Optional::isPresent).map((Function<? super Object, ?>)Optional::get).map((Function<? super Object, ?>)Member::getUser).map((Function<? super Object, ?>)User::openPrivateChannel).forEach((Consumer<? super Object>)this::sendMessage);
                }
            }
            catch (InterruptedException ex) {}
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
