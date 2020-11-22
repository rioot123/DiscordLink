package net.dirtcraft.discord.discordlink;

import net.dirtcraft.discord.dirtdatabaselib.SQLManager;
import net.dirtcraft.discord.discordlink.Commands.Bungee.*;
import net.dirtcraft.discord.discordlink.Events.BungeeEventHandler;
import net.dirtcraft.discord.discordlink.Events.DiscordChatHandler;
import net.dirtcraft.discord.discordlink.Events.DiscordJoinHandler;
import net.dirtcraft.discord.discordlink.Events.PluginMessageHandler;
import net.dirtcraft.discord.discordlink.Exceptions.DependantNotLoadedException;
import net.dirtcraft.discord.discordlink.Storage.ConfigManager;
import net.dirtcraft.discord.discordlink.Storage.Database;
import net.dirtcraft.discord.discordlink.Storage.Settings;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Sanctions.SanctionUtils;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.api.JDA;
import net.md_5.bungee.api.plugin.Plugin;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class DiscordLink extends Plugin {

    private static DiscordLink instance;
    private static JDA jda;

    private Logger logger;
    private ConfigManager configManager;
    private Database storage;
    private PluginMessageHandler channelHandler;

    @Override
    public void onEnable() {
        try {
            getLogger().info("Discord Link initializing...");
            ConfigurationLoader<CommentedConfigurationNode> loader = getLoader();
            this.logger = getLogger();
            this.configManager = new ConfigManager(loader);
            this.storage = new Database();
            this.channelHandler = new PluginMessageHandler(this);
            instance = this;

            if ((jda = SpongeDiscordLib.getJDA()) == null) throw new DependantNotLoadedException("JDA NOT LOADED");

            jda.addEventListener(new DiscordChatHandler());
            jda.addEventListener(new DiscordJoinHandler());
            this.getProxy().getPluginManager().registerCommand(this, new Link(storage));
            this.getProxy().getPluginManager().registerCommand(this, new Unlink(storage));
            //this.getProxy().getPluginManager().registerCommand(this, new Discord());
            this.getProxy().getPluginManager().registerCommand(this, new Promote());
            this.getProxy().getPluginManager().registerCommand(this, new Demote());
            getProxy().getPluginManager().registerListener(this, new BungeeEventHandler());
            getProxy().getPluginManager().registerListener(this, channelHandler);
            getProxy().registerChannel(Settings.ROOT_CHANNEL);
            SanctionUtils utils = SanctionUtils.INSTANCE;

            logger.info("Discord Link initialized");
        } catch (DependantNotLoadedException e){
            logger.warning("[DISCORD-LINK]: " + e.getMessage() + " DISCORD-LINK DISABLED.");
        } catch (IOException | ObjectMappingException e){
            logger.warning("[DISCORD-LINK]: FAILED TO PARSE CONFIG. PLEASE MAKE SURE CONFIG DIR IS ACCESSABLE / HAS FILE WRITE ACCESS.");
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        SQLManager.close();
    }

    public void saveConfig(){
        configManager.save();
    }

    public Database getStorage(){
        return storage;
    }

    public PluginMessageHandler getChannelHandler(){
        return channelHandler;
    }

    public static DiscordLink getInstance() {
        return instance;
    }

    public static JDA getJDA() {
        return jda;
    }

    private ConfigurationLoader<CommentedConfigurationNode> getLoader(){
        //noinspection ResultOfMethodCallIgnored
        getDataFolder().mkdirs();
        return HoconConfigurationLoader.builder()
                .setFile(new File(getDataFolder(), "config.hocon"))
                .build();
    }
}
