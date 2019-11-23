package net.dirtcraft.discord.discordlink;

import com.google.inject.Inject;
import net.dirtcraft.discord.discordlink.Commands.CommandManager;
import net.dirtcraft.discord.discordlink.Commands.Discord.DiscordCommand;
import net.dirtcraft.discord.discordlink.Configuration.ConfigManager;
import net.dirtcraft.discord.discordlink.Database.Storage;
import net.dirtcraft.discord.discordlink.Events.DiscordEvents;
import net.dirtcraft.discord.discordlink.Events.NormalChat;
import net.dirtcraft.discord.discordlink.Events.SpongeEvents;
import net.dirtcraft.discord.discordlink.Events.UltimateChat;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.core.JDA;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.HashMap;

@Plugin(
        id = "discord-link",
        name = "Discord Link",
        description = "Handles gamechats on the DirtCraft Discord.",
        authors = {
                "juliann"
        },
        dependencies = {
                @Dependency(id = "sponge-discord-lib", optional = true),
                @Dependency(id = "ultimatechat", optional = true),
                @Dependency(id = "dirt-database-lib", optional = true)
        }
)
public class DiscordLink {

    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigManager configManager;

    @Inject
    private Logger logger;

    @Inject
    private PluginContainer container;

    private static DiscordLink instance;
    private Storage storage;

    @Listener (order = Order.LAST)
    public void onPreInit(GamePreInitializationEvent event) {
        if (!Sponge.getPluginManager().isLoaded("sponge-discord-lib")) {
            logger.error("Sponge-Discord-Lib is not installed! " + container.getName() + " will not load.");
            return;
        }
        if (!Sponge.getPluginManager().isLoaded("dirt-database-lib")) {
            logger.error("Dirt-Database-Lib is not installed! " + container.getName() + " will not load.");
            return;
        }

        instance = this;

        this.configManager = new ConfigManager(loader);
        this.storage = new Storage();
        Utility.setStatus();
        Utility.setTopic();

        final HashMap<String, DiscordCommand> commandMap = new HashMap<>();
        new CommandManager(this, storage, commandMap);
        getJDA().addEventListener(new DiscordEvents(storage, commandMap));
        Sponge.getEventManager().registerListeners(instance, new SpongeEvents(instance, storage));
        if (SpongeDiscordLib.getServerName().toLowerCase().contains("pixel")) {
            Sponge.getEventManager().registerListeners(instance, new NormalChat());
        } else {
            Sponge.getEventManager().registerListeners(instance, new UltimateChat());
        }
    }

    @Listener
    public void onGameInit(GameInitializationEvent event) {

    }

    public static JDA getJDA() {
        return SpongeDiscordLib.getJDA();
    }

    public static DiscordLink getInstance() {
        return instance;
    }

    public Storage getStorage(){
        return storage;
    }

}
