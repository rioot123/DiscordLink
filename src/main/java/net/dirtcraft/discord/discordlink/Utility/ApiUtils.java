package net.dirtcraft.discord.discordlink.Utility;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class ApiUtils {
    public static Optional<OfflinePlayer> getPlayerOffline(UUID uuid){
        return Optional.ofNullable(Bukkit.getOfflinePlayer(uuid));
    }

    public static Optional<Player> getPlayer(OfflinePlayer player){
        return Optional.ofNullable(player.getPlayer());
    }

    public static boolean isGameReady(){
        return true;
    }
}
