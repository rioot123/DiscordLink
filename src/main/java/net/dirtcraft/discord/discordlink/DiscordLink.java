package net.dirtcraft.discord.discordlink;

import com.google.common.reflect.TypeToken;
import net.dirtcraft.discord.dirtdatabaselib.SQLManager;
import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.Commands.Bukkit.Discord;
import net.dirtcraft.discord.discordlink.Commands.Bukkit.Unverify;
import net.dirtcraft.discord.discordlink.Commands.Bukkit.Verify;
import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Database.Storage;
import net.dirtcraft.discord.discordlink.Events.DiscordEvents;
import net.dirtcraft.discord.discordlink.Events.SpigotEvents;
import net.dirtcraft.discord.discordlink.Utility.CrashDetector;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.milkbowl.vault.chat.Chat;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class DiscordLink extends JavaPlugin {

    private static DiscordLink instance;
    private static JDA jda;

    public final List<UUID> tpSpawnList = new ArrayList<>();
    private Storage storage;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;

        try {
            File configDir = new File(getDataFolder(), "config.hocon");
            //noinspection ResultOfMethodCallIgnored
            getDataFolder().mkdirs();
            HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                    .setFile(configDir)
                    .build();
            ConfigurationNode node = loader.load(loader.getDefaultOptions().setShouldCopyDefaults(true));
            node.getValue(TypeToken.of(PluginConfiguration.class), new PluginConfiguration());
            loader.save(node);

            jda = JDABuilder.createDefault(PluginConfiguration.Main.botToken)
                    .build()
                    .awaitReady();
            jda.addEventListener(new DiscordEvents());

        } catch (IOException | ObjectMappingException e){
            System.out.println("[DISCORD-LINK]: FAILED TO PARSE CONFIG. PLEASE MAKE SURE CONFIG DIR IS ACCESSABLE / HAS FILE WRITE ACCESS.");
            e.printStackTrace();
            return;
        } catch (LoginException | InterruptedException e){
            System.out.println("[DISCORD-LINK]: FAILED TO LOG ON USING BOT TOKEN. MAKE SURE BOT TOKEN IS VALID!!!");
            e.printStackTrace();
            return;
        }

        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(Chat.class);
        Chat chat;
        if (chatProvider == null) return;
        else chat = chatProvider.getProvider();

        Bukkit.getPluginManager().registerEvents(new SpigotEvents(chat), this);

        this.storage = new Storage();
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

    public Storage getStorage(){
        return storage;
    }

    public static DiscordLink getInstance() {
        return instance;
    }

    public static JDA getJDA() {
        return jda;
    }
}
