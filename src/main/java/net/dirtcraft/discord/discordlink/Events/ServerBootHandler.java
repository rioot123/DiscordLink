package net.dirtcraft.discord.discordlink.Events;

import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.requests.RequestFuture;
import net.dv8tion.jda.core.requests.RestAction;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.state.*;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

public class ServerBootHandler {
    private final Thread bootWatchdog;
    private final String modpack = SpongeDiscordLib.getServerName();
    private final long[] dmList = new long[]{248056002274918400L};
    private final int time = 1000*60*30;

    private RequestFuture<Message> future;

    public ServerBootHandler(){
        sendGameStageEmbed("Constructing Game Instance", 1);
        bootWatchdog = new Thread(()->{
            try{
                Thread.sleep(time);
                Arrays.stream(dmList)
                        .mapToObj(GameChat.getGuild()::getMemberById)
                        .filter(Objects::nonNull)
                        .map(Member::getUser)
                        .map(User::openPrivateChannel)
                        .forEach(this::sendMessage);
            } catch (InterruptedException ignored){ }
        });
        bootWatchdog.start();
    }

    @Listener(order = Order.FIRST)
    public void onGamePreInitialization(GamePreInitializationEvent event){
        sendGameStageEmbed("Pre-Initializing Game Instance", 2);
    }

    @Listener(order = Order.FIRST)
    public void onGameInitialization(GameInitializationEvent event){
        sendGameStageEmbed("Initializing Game Instance", 3);
    }

    @Listener(order = Order.FIRST)
    public void onGamePostInitialization(GamePostInitializationEvent event){
        sendGameStageEmbed("Post-Initializing Game Instance", 4);
    }

    @Listener(order = Order.FIRST)
    public void onGameLoadComplete(GameLoadCompleteEvent event){
        sendGameStageEmbed("Loading Game Instance", 5);
    }

    @Listener(order = Order.FIRST)
    public void onGameAboutToStartServer(GameAboutToStartServerEvent event){
        sendGameStageEmbed("Preparing To Start Server", 6);
    }

    @Listener(order = Order.FIRST)
    public void onGameStartingServerEvent(GameStartingServerEvent event){
        sendGameStageEmbed("Starting Server", 7);
    }

    @Listener(order = Order.POST)
    public void onGameStartedServer(GameStartedServerEvent event){
        if (future == null) return;
        bootWatchdog.interrupt();
        future.whenComplete((message, throwable) -> message.delete().queue());
        future = null;
    }

    private void sendGameStageEmbed(String state, int order) {
        MessageEmbed embed = Utility.embedBuilder()
                .setColor(Color.ORANGE)
                .setDescription("The server is currently booting... Please wait...\n**" + state + "** ("+ order + "/7)")
                .build();
        if (future != null) future.whenComplete((message, throwable) -> message.delete().queue());
        future = GameChat.getChannel().sendMessage(embed).submit();
    }

    private void sendMessage(RestAction<PrivateChannel> channelRestAction){
        try {
            channelRestAction.queue(m -> m.sendMessage("Server \"" + modpack + "\" has been attempting to boot for " + time + "ms.").queue());
        } catch (Exception ignored){
            //no one cares bro
        }
    }
}
