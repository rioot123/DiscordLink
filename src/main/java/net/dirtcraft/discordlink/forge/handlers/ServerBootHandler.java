package net.dirtcraft.discordlink.forge.handlers;

import net.dirtcraft.discordlink.forge.DiscordLink;
import net.dirtcraft.discordlink.common.storage.PluginConfiguration;
import net.dirtcraft.discordlink.common.utility.Utility;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ForgeI18n;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

import java.awt.*;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ServerBootHandler {
    final private long second = 1000;
    final private long minute = second * 60;
    final private long time = System.currentTimeMillis();
    protected GameStage state;
    protected boolean isReady;

    private CompletableFuture<Message> future;

    public void onLateInitialization() {
        sendGameStageEmbed(this.state);
        if (this.state != GameStage.SERVER_STARTED) startTimer(this.state);
    }

    @SubscribeEvent
    public void commonSetup(FMLCommonSetupEvent event) {
        this.state = GameStage.CONSTRUCT_MOD;
        if (!isReady) return;
        startTimer(GameStage.CONSTRUCT_MOD);
        sendGameStageEmbed(GameStage.CONSTRUCT_MOD);
    }

    @SubscribeEvent
    public void dedicatedSetup(FMLDedicatedServerSetupEvent event) {
        this.state = GameStage.DEDICATED_SETUP;
        if (!isReady) return;
        startTimer(GameStage.DEDICATED_SETUP);
        sendGameStageEmbed(GameStage.DEDICATED_SETUP);
    }

    @SubscribeEvent
    public void interModEnqueue(InterModEnqueueEvent event){
        this.state = GameStage.INTER_MOD_ENQUEUE;
        if (!isReady) return;
        startTimer(GameStage.INTER_MOD_ENQUEUE);
        sendGameStageEmbed(GameStage.INTER_MOD_ENQUEUE);
    }


    @SubscribeEvent
    public void interModProcess(InterModProcessEvent event){
        this.state = GameStage.INTER_MOD_PROCESS;
        if (!isReady) return;
        startTimer(GameStage.INTER_MOD_PROCESS);
        sendGameStageEmbed(GameStage.INTER_MOD_PROCESS);
    }

    @SubscribeEvent
    public void onGameAboutToStartServer(FMLServerAboutToStartEvent event){
        this.state = GameStage.SERVER_ABOUT_TO_START;
        if (!isReady) return;
        startTimer(GameStage.SERVER_ABOUT_TO_START);
        sendGameStageEmbed(GameStage.SERVER_ABOUT_TO_START);
    }

    @SubscribeEvent
    public void onGameStartingServerEvent(FMLServerStartingEvent event){
        this.state = GameStage.SERVER_STARTING;
        if (!isReady) return;
        startTimer(GameStage.SERVER_STARTING);
        sendGameStageEmbed(GameStage.SERVER_STARTING);
    }

    @SubscribeEvent
    public void onGameStartedServer(FMLServerStartedEvent event){
        this.state = GameStage.SERVER_STARTED;
        if (!isReady) return;
        if (future != null) future.whenComplete((message, throwable) -> message.delete().queue());
        state = null;
        future = null;
        sendLaunchedEmbed();
    }

    @SubscribeEvent
    public void onServerStopping(FMLServerStoppingEvent event) {
        this.state = GameStage.SERVER_STOPPING;
        String sName = PluginConfiguration.Main.SERVER_NAME;
        DiscordLink.get()
                .getChannelManager()
                .getGameChat()
                .sendMessage(
                        Utility.embedBuilder()
                                .setDescription(PluginConfiguration.Format.serverStop
                                        .replace("{modpack}", sName)
                                ).build());
    }

    public void sendLaunchedEmbed() {
        String sName = PluginConfiguration.Main.SERVER_NAME;
        DiscordLink.get()
                .getChannelManager()
                .getGameChat()
                .sendMessage(
                Utility.embedBuilder()
                        .setColor(Color.GREEN)
                        .setDescription(PluginConfiguration.Format.serverStart
                                .replace("{modpack}", sName)
                        ).build());
    }

    protected void sendGameStageEmbed(GameStage state) {
        TextChannel gameChat = DiscordLink.get().getChannelManager().getDefaultChannel();
        String stage = state.getName();
        int level = state.getStage();
        String content = String.format("The server is currently booting... Please wait...\n**%s** (%d/4)", stage, level);
        MessageEmbed embed = Utility.embedBuilder()
                .setColor(Color.ORANGE)
                .setDescription(content)
                .build();
        if (future != null) future = future
                .whenComplete((message, throwable) -> message.delete().queue())
                .thenApply(message -> gameChat.sendMessage(embed).complete());
        else future = gameChat.sendMessage(embed).submit();
    }

    private void sendMessage(RestAction<PrivateChannel> channelRestAction){
        try {
            final String name = PluginConfiguration.Main.SERVER_NAME;
            long id = PluginConfiguration.Main.defaultChannelID;
            long ms = (System.currentTimeMillis() - time);
            double minutes = (double) Math.max(ms, 1) / minute;
            String template = "Server %s has been attempting to boot for %dms (%.1f minutes) @ <#%d>";
            String message = String.format(template, name, ms, minutes, id);
            channelRestAction.queue(m -> m.sendMessage(message).queue());
        } catch (Exception ignored){
            //no one cares bro
        }
    }

    public void startTimer(final GameStage state){
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

    public boolean isStarted(){
        return state == GameStage.SERVER_STARTED;
    }

    protected enum GameStage {
        CONSTRUCT_MOD        (1, "Constructing Mod Instances"),
        DEDICATED_SETUP      (2, "Preparing Dedicated Server"),
        INTER_MOD_ENQUEUE    (3, "Running Cross-Mod Initializing"),
        INTER_MOD_PROCESS    (4, "Running Cross-Mod Compat Setup"),
        SERVER_ABOUT_TO_START(5, "Server Preparing to Start"),
        SERVER_STARTING      (6, "Server Starting"),
        SERVER_STARTED       (7, "Server Started"),
        SERVER_STOPPING      (-1,"Server Shutting Down");
        private final String name;
        private final int stage;
        GameStage(int stage, String name){
            this.name = name;
            this.stage = stage;
        }

        private String getName(){
            return name;
        }

        private int getStage(){
            return stage;
        }
    }
}
