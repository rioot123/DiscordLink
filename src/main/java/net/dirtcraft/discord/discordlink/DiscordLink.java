package net.dirtcraft.discord.discordlink;

import net.dirtcraft.discord.dirtdatabaselib.SQLManager;
import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.Commands.Bukkit.Discord;
import net.dirtcraft.discord.discordlink.Commands.Bukkit.Unverify;
import net.dirtcraft.discord.discordlink.Commands.Bukkit.Verify;
import net.dirtcraft.discord.discordlink.Events.DiscordEvents;
import net.dirtcraft.discord.discordlink.Events.OfflineTpHandler;
import net.dirtcraft.discord.discordlink.Events.SpigotEvents;
import net.dirtcraft.discord.discordlink.Exceptions.DependantNotLoadedException;
import net.dirtcraft.discord.discordlink.Storage.ConfigManager;
import net.dirtcraft.discord.discordlink.Storage.Database;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Utility.CrashDetector;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.api.JDA;
import net.milkbowl.vault.chat.Chat;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public class DiscordLink extends JavaPlugin {

    private static DiscordLink instance;
    private static JDA jda;

    private final ConfigurationLoader<CommentedConfigurationNode> loader = getLoader();
    private final Logger logger = getLogger();
    public final List<UUID> tpSpawnList = new ArrayList<>();
    private ConfigManager configManager;
    private Database storage;

    @Override
    public void onEnable() {
        try {
            logger.info("Discord Link initializing...");
            //noinspection ResultOfMethodCallIgnored
            getDataFolder().mkdirs();
            saveDefaultConfig();
            this.configManager = new ConfigManager(loader);
            this.storage = new Database();
            instance = this;

            Chat chat = getProvider(Chat.class)
                    .map(RegisteredServiceProvider::getProvider)
                    .orElseThrow(()->new DependantNotLoadedException("FAILED TO LOCATE CHAT PROVIDER!"));
            if ((jda = SpongeDiscordLib.getJDA()) == null) throw new DependantNotLoadedException("JDA NOT LOADED");

            jda.addEventListener(new DiscordEvents());
            Bukkit.getPluginManager().registerEvents(new SpigotEvents(chat), this);
            Bukkit.getPluginManager().registerEvents(new OfflineTpHandler(), this);

            Utility.setStatus();
            Utility.setTopic();
            GameChat.sendMessage(
                    Utility.embedBuilder()
                            .setColor(Color.GREEN)
                            .setDescription(PluginConfiguration.Format.serverStart
                                    .replace("{modpack}", PluginConfiguration.Main.SERVER_NAME)
                            ).build());

            getCommand("verify").setExecutor(new Verify());
            getCommand("unverify").setExecutor(new Unverify());
            getCommand("discord").setExecutor(new Discord());

            CrashDetector.analyze(this);
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
        Utility.setStatus();
        Utility.setTopic();
        GameChat.sendMessage(
                Utility.embedBuilder()
                        .setDescription(PluginConfiguration.Format.serverStop
                                .replace("{modpack}", PluginConfiguration.Main.SERVER_NAME)
                        ).build());
    }

    public void saveConfig(){
        configManager.save();
    }

    public Database getStorage(){
        return storage;
    }

    public static DiscordLink getInstance() {
        return instance;
    }

    public static JDA getJDA() {
        return jda;
    }

    private ConfigurationLoader<CommentedConfigurationNode> getLoader(){
        return HoconConfigurationLoader.builder()
                .setFile(new File(getDataFolder(), "config.hocon"))
                .build();
    }

    @SuppressWarnings("SameParameterValue")
    private <T> Optional<RegisteredServiceProvider<T>> getProvider(Class<T> clazz){
        return Optional.ofNullable(getServer().getServicesManager().getRegistration(clazz));
    }
}
