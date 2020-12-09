package net.dirtcraft.discord.discordlink.Utility.Compatability.Platform;

import net.dirtcraft.discord.discordlink.DiscordLink;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlatformUtils {

    public static final String VERSION = "Thermos-1.7.10";

    public static VanishProvider vanishProvider = getVanishProvider();

    public static Optional<PlatformUser> getPlayerOffline(UUID uuid){
        return Optional.ofNullable(Bukkit.getOfflinePlayer(uuid)).map(PlatformUser::new);
    }

    public static Optional<PlatformUser> getPlayerOffline(String identifier){
        boolean isUUID = identifier.matches("(?i)[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
        return Optional.ofNullable(isUUID? Bukkit.getOfflinePlayer(UUID.fromString(identifier)): Bukkit.getOfflinePlayer(identifier))
                .map(PlatformUser::new);
    }

    public static Optional<PlatformUser> getPlayerOffline(CommandSender sender){
        if (!(sender instanceof Player)) return Optional.empty();
        else return Optional.of(new PlatformUser((Player) sender));
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

    public static void toConsole(String command) {
        toConsole(Bukkit.getConsoleSender(), command);
    }

    public static void toConsole(CommandSender source, String command) {
        Bukkit.getScheduler().callSyncMethod(DiscordLink.getInstance(), ()->Bukkit.dispatchCommand(source, command));
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
