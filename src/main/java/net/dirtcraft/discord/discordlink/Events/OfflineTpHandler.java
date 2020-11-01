package net.dirtcraft.discord.discordlink.Events;

import net.dirtcraft.discord.discordlink.DiscordLink;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OfflineTpHandler implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (DiscordLink.getInstance().tpSpawnList.contains(player.getUniqueId())){
            player.teleport(Bukkit.getWorld("world").getSpawnLocation());
            DiscordLink.getInstance().tpSpawnList.remove(player.getUniqueId());
        }
    }
}
