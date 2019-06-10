package net.dirtcraft.discord.discordlink;

import br.net.fabiozumbi12.UltimateChat.Sponge.API.SendChannelMessageEvent;
import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.awt.*;
import java.util.regex.Pattern;

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
                        .setDescription(PluginConfiguration.Format.serverStop
                                .replace("{modpack}", modpack)
                        ).build());
    }

    @Listener
    public void onSendChannelMessage(SendChannelMessageEvent event, @Root Object cause) {
        if (event.getChannel() == null) return;
        if (!event.getChannel().getName().equalsIgnoreCase("global")) return;
        if (!(cause instanceof Player)) return;
        Player player = (Player) cause;


        String prefix = TextSerializers.FORMATTING_CODE.stripCodes(player.getOption("prefix").orElse(""));
        String username = player.getName();
        String message = TextSerializers.FORMATTING_CODE.stripCodes(event.getMessage().toPlain())
                .replace("@everyone", "")
                .replace("@here", "")
                .replaceAll("<@\\d+>", "");

        Utility.chatToDiscord(prefix, username, message);
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event, @Root Player player) {
        if (player.hasPlayedBefore()) {
            String prefix = TextSerializers.FORMATTING_CODE.stripCodes(player.getOption("prefix").orElse(""));

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
        String prefix = TextSerializers.FORMATTING_CODE.stripCodes(player.getOption("prefix").orElse(""));

        Utility.messageToChannel("message", PluginConfiguration.Format.playerDisconnect
                        .replace("{username}", player.getName())
                        .replace("{prefix}", prefix),
                null);
    }

}
