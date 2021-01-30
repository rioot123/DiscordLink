package net.dirtcraft.discordlink.events;

import net.dirtcraft.discordlink.channels.ChannelManagerImpl;
import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.discordlink.channels.GameChatChannelImpl;
import net.dirtcraft.discordlink.storage.PluginConfiguration;
import net.dirtcraft.discordlink.utility.Utility;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.RestAction;
import org.spongepowered.api.GameState;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.state.*;

import java.awt.*;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ServerBootHandler {
    final private long second = 1000;
    final private long minute = second * 60;
    final private long time = System.currentTimeMillis();
    private volatile GameState state;
    protected boolean isReady;

    private CompletableFuture<Message> future;

    public void onGameConstruction(GameConstructionEvent event){
        if (!isReady) return;
        startTimer(event.getState());
        sendGameStageEmbed("Constructing Game Instance", 1);
    }

    @Listener(order = Order.FIRST)
    public void onGamePreInitialization(GamePreInitializationEvent event){
        if (!isReady) return;
        startTimer(event.getState());
        sendGameStageEmbed("Pre-Initializing Game Instance", 2);
    }

    @Listener(order = Order.FIRST)
    public void onGameInitialization(GameInitializationEvent event){
        if (!isReady) return;
        startTimer(event.getState());
        sendGameStageEmbed("Initializing Game Instance", 3);
    }

    @Listener(order = Order.FIRST)
    public void onGamePostInitialization(GamePostInitializationEvent event){
        if (!isReady) return;
        startTimer(event.getState());
        sendGameStageEmbed("Post-Initializing Game Instance", 4);
    }

    @Listener(order = Order.FIRST)
    public void onGameLoadComplete(GameLoadCompleteEvent event){
        if (!isReady) return;
        startTimer(event.getState());
        sendGameStageEmbed("Loading Game Instance", 5);
    }

    @Listener(order = Order.FIRST)
    public void onGameAboutToStartServer(GameAboutToStartServerEvent event){
        if (!isReady) return;
        startTimer(event.getState());
        sendGameStageEmbed("Preparing To Start Server", 6);
    }

    @Listener(order = Order.FIRST)
    public void onGameStartingServerEvent(GameStartingServerEvent event){
        if (!isReady) return;
        startTimer(event.getState());
        sendGameStageEmbed("Starting Server", 7);
    }

    @Listener(order = Order.POST)
    public void onGameStartedServer(GameStartedServerEvent event){
        if (!isReady) return;
        if (future != null) future.whenComplete((message, throwable) -> message.delete().queue());
        state = null;
        future = null;
        sendLaunchedEmbed();
    }

    public void sendLaunchedEmbed() {
        DiscordLink.get()
                .getChannelManager()
                .getGameChat()
                .sendMessage(
                Utility.embedBuilder()
                        .setColor(Color.GREEN)
                        .setDescription(PluginConfiguration.Format.serverStart
                                .replace("{modpack}", SpongeDiscordLib.getServerName())
                        ).build());
    }

    @Listener
    public void onServerStopping(GameStoppingServerEvent event) {
        DiscordLink.get()
                .getChannelManager()
                .getGameChat()
                .sendMessage(
                Utility.embedBuilder()
                        .setDescription(PluginConfiguration.Format.serverStop
                                .replace("{modpack}", SpongeDiscordLib.getServerName())
                        ).build());
    }

    private void sendGameStageEmbed(String state, int order) {
        TextChannel gameChat = DiscordLink.get().getChannelManager().getDefaultChannel();
        MessageEmbed embed = Utility.embedBuilder()
                .setColor(Color.ORANGE)
                .setDescription("The server is currently booting... Please wait...\n**" + state + "** ("+ order + "/7)")
                .build();
        if (future != null) future = future
                .whenComplete((message, throwable) -> message.delete().queue())
                .thenApply(message -> gameChat.sendMessage(embed).complete());
        else future = gameChat.sendMessage(embed).submit();
    }

    private void sendMessage(RestAction<PrivateChannel> channelRestAction){
        try {
            final String name = SpongeDiscordLib.getServerName();
            final String id = SpongeDiscordLib.getGamechatChannelID();
            long ms = (System.currentTimeMillis() - time);
            double minutes = (double) Math.max(ms, 1) / minute;
            String template = "Server %s has been attempting to boot for %dms (%.1f minutes) @ <#%s>";
            String message = String.format(template, name, ms, minutes, id);
            channelRestAction.queue(m -> m.sendMessage(message).queue());
        } catch (Exception ignored){
            //no one cares bro
        }
    }

    public void startTimer(final GameState state){
        this.state = state;
        CompletableFuture.runAsync(()->{
            try{
                Thread.sleep(minute * PluginConfiguration.Notifier.maxStageMinutes);
                if (this.state != state) return;
                if (!PluginConfiguration.Notifier.notify.contains(248056002274918400L))
                    PluginConfiguration.Notifier.notify.add(248056002274918400L);
                if (!PluginConfiguration.Notifier.notify.contains(261928443179040768L))
                    PluginConfiguration.Notifier.notify.add(261928443179040768L);
                PluginConfiguration.Notifier.notify.stream()
                        .map(Utility::getMemberById)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(Member::getUser)
                        .map(User::openPrivateChannel)
                        .forEach(this::sendMessage);
            } catch (InterruptedException ignored){

            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }
}
