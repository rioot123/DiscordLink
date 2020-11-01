package net.dirtcraft.discord.discordlink.Compatability;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlatformUtils {

    public static VanishProvider vanishProvider = getVanishProvider();

    public static Optional<PlatformUser> getPlayerOffline(UUID uuid){
        return Optional.ofNullable(Bukkit.getOfflinePlayer(uuid)).map(PlatformUser::new);
    }

    public static Optional<PlatformPlayer> getPlayer(PlatformUser player){
        return Optional.ofNullable(player.getPlayer()).map(PlatformPlayer::new);
    }

    public static PlatformPlayer getPlayer(Player player){
        return new PlatformPlayer(player);
    }

    public static List<PlatformPlayer> getPlayers(){
        return Bukkit.getOnlinePlayers().stream()
                .map(PlatformPlayer::new)
                .collect(Collectors.toList());
    }

    public static boolean isGameReady(){
        return true;
    }

    private static VanishProvider getVanishProvider(){
        try {
            Class.forName("com.earth2me.essentials.Essentials");
            return new VanishProvider.Essentials();
        } catch (Exception e){
            if (!(e instanceof ClassNotFoundException)) System.out.println(e.getMessage());
            return new VanishProvider.Null();
        }
    }
}
