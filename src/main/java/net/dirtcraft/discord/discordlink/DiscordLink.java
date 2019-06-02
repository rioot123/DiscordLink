package net.dirtcraft.discord.discordlink;

import com.google.inject.Inject;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import net.dirtcraft.discord.discordlink.Configuration.ConfigManager;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.core.JDA;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

import javax.security.auth.login.LoginException;

@Plugin(
        id = "discord-link",
        name = "Discord Link",
        description = "Handles gamechats on the DirtCraft Discord.",
        authors = {
                "juliann"
        },
        dependencies = {
                @Dependency(id = "luckperms"),
                @Dependency(id = "sponge-discord-lib")
        }
)
public class DiscordLink {

    @Inject
    @DefaultConfig(sharedRoot = false)
    public ConfigurationLoader<CommentedConfigurationNode> loader;
    public ConfigManager configManager;

    @Inject
    private Logger logger;

    private static DiscordLink instance;

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        instance = this;

        this.configManager = new ConfigManager(loader);

        try {
            SpongeDiscordLib.initJDA();
        } catch (LoginException | InterruptedException exception) {
            logger.warn("Exception while initializing Discord Bot: " + exception.getMessage());
        }

        getJDA().addEventListener(new DiscordEvents());
        Utility.setTopic();
        Sponge.getEventManager().registerListeners(instance, new SpongeEvents());
    }

    public static LuckPermsApi getLuckPerms() {
        return LuckPerms.getApi();
    }

    public static JDA getJDA() {
        return SpongeDiscordLib.getJDA();
    }

    public static DiscordLink getInstance() {
        return instance;
    }

}
