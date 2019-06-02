package net.dirtcraft.discord.discordlink;

import me.lucko.luckperms.api.Contexts;
import me.lucko.luckperms.api.User;
import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.awt.*;

public class SpongeEvents {

    private final String modpack = SpongeDiscordLib.getServerName();

    @Listener
    public void onServerStarted(GameStartedServerEvent event) {
        Utility.messageToChannel("embed", null,
                Utility.embedBuilder()
                .setColor(Color.GREEN)
                .setDescription(PluginConfiguration.Format.serverStart
                        .replace("{modpack}", modpack)
                ).build());
    }

    @Listener
    public void onServerStopping(GameStoppingServerEvent event) {
        Utility.messageToChannel("embed", null,
                Utility.embedBuilder()
                        .setColor(Color.GREEN)
                        .setDescription(PluginConfiguration.Format.serverStop
                                .replace("{modpack}", modpack)
                        ).build());
    }

    @Listener
    public void onChat(MessageChannelEvent.Chat event, @Root Object cause) {
        if (!(cause instanceof Player)) return;
        Player player = (Player) cause;
        User user = DiscordLink.getLuckPerms().getUser(player.getUniqueId());
        if (user == null) return;

        String prefix = TextSerializers.FORMATTING_CODE.stripCodes(user.getCachedData().getMetaData(Contexts.global()).getPrefix());
        String username = player.getName();
        String message = TextSerializers.FORMATTING_CODE.stripCodes(event.getRawMessage().toPlain());

        Utility.chatToDiscord(prefix, username, message);
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event, @Root Player player) {
        if (player.hasPlayedBefore()) {
            User user = DiscordLink.getLuckPerms().getUser(player.getUniqueId());
            if (user == null) return;
            String prefix = TextSerializers.FORMATTING_CODE.stripCodes(user.getCachedData().getMetaData(Contexts.global()).getPrefix());

            Utility.messageToChannel("message", PluginConfiguration.Format.playerJoin
                    .replace("{username}", player.getName())
                    .replace("{prefix}", prefix),
                    null);
        } else {
            MessageEmbed embed = Utility
                    .embedBuilder()
                    .setDescription(PluginConfiguration.Format.newPlayerJoin
                            .replace("{username}", player.getName()))
                    .build();

            Utility.messageToChannel("embed", null, embed);
        }
    }

    @Listener
    public void onPlayerDisconnect(ClientConnectionEvent.Disconnect event, @Root Player player) {
        User user = DiscordLink.getLuckPerms().getUser(player.getUniqueId());
        if (user == null) return;
        String prefix = TextSerializers.FORMATTING_CODE.stripCodes(user.getCachedData().getMetaData(Contexts.global()).getPrefix());

        Utility.messageToChannel("message", PluginConfiguration.Format.playerDisconnect
                        .replace("{username}", player.getName())
                        .replace("{prefix}", prefix),
                null);
    }

}
