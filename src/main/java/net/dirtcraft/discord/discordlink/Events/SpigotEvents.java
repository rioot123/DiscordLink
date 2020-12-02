package net.dirtcraft.discord.discordlink.Events;

import net.dirtcraft.discord.discordlink.API.Channels;
import net.dirtcraft.discord.discordlink.Storage.Permission;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformPlayer;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUtils;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SpigotEvents implements Listener {
    public SpigotEvents(Chat chat){
        this.vault = chat;
    }
    private final Chat vault;
    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent event){
         if (event.isCancelled()) return;
        String prefix = Utility.sanitiseMinecraftText(vault.getPlayerPrefix(event.getPlayer()));
        String nickName = Utility.sanitiseMinecraftText(event.getPlayer().getDisplayName());
        String message = Utility.sanitiseMinecraftText(event.getMessage());
        Channels.sendPlayerMessage(prefix, nickName, message);
        if (event.getPlayer().hasPermission(Permission.COLOUR_CHAT)) {
            String s = event.getMessage();
            s = Utility.formatColourCodes(s);
            event.setMessage(s);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlatformPlayer player = PlatformUtils.getPlayer(event.getPlayer());
        if (player.isVanished()) return;
        if (player.hasPlayedBefore()) {
            String prefix = Utility.removeColourCodes(vault.getPlayerPrefix(event.getPlayer()));
            Channels.getDefaultChat().sendMessage(PluginConfiguration.Format.playerJoin
                    .replace("{username}", player.getName())
                    .replace("{prefix}", prefix)
            );
        } else {
            MessageEmbed embed = Utility
                    .embedBuilder()
                    .setDescription(PluginConfiguration.Format.newPlayerJoin
                            .replace("{username}", player.getName()))
                    .build();
            Channels.getDefaultChat().sendMessage(embed);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        PlatformPlayer player = PlatformUtils.getPlayer(event.getPlayer());
        if (player.isVanished()) return;
        String prefix = Utility.removeColourCodes(vault.getPlayerPrefix(event.getPlayer()));
        Channels.getDefaultChat().sendMessage(PluginConfiguration.Format.playerDisconnect
                .replace("{username}", player.getName())
                .replace("{prefix}", prefix)
        );
    }

}
