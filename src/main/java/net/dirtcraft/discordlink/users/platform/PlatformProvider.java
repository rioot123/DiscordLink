// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.users.platform;

import org.spongepowered.api.GameState;
import net.dirtcraft.discordlink.DiscordLink;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.command.CommandSource;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import net.dirtcraft.spongediscordlib.users.platform.PlatformPlayer;
import java.util.function.Function;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.Sponge;
import net.dirtcraft.spongediscordlib.users.platform.PlatformUser;
import java.util.Optional;
import java.util.UUID;

public class PlatformProvider
{
    public static final String VERSION;
    
    public static Optional<PlatformUser> getPlayerOffline(final UUID uuid) {
        return Sponge.getServiceManager().provide((Class)UserStorageService.class).flatMap(uss -> uss.get(uuid)).map((Function<? super Object, ? extends PlatformUser>)PlatformUserImpl::new);
    }
    
    public static Optional<PlatformUser> getPlayerOffline(final String identifier) {
        final boolean isUUID = identifier.matches("(?i)[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
        return Sponge.getServiceManager().provide((Class)UserStorageService.class).flatMap(uss -> isUUID ? uss.get(UUID.fromString(identifier)) : uss.get(identifier)).map((Function<? super Object, ? extends PlatformUser>)PlatformUserImpl::new);
    }
    
    public static Optional<PlatformUser> getPlayerOffline(final String identifier, final boolean acceptUUID) {
        if (acceptUUID) {
            return getPlayerOffline(identifier);
        }
        return Sponge.getServiceManager().provide((Class)UserStorageService.class).flatMap(uss -> uss.get(identifier)).map((Function<? super Object, ? extends PlatformUser>)PlatformUserImpl::new);
    }
    
    public static Optional<PlatformPlayer> getPlayer(final PlatformUser player) {
        return ((User)player.getOfflinePlayer()).getPlayer().map(PlatformPlayerImpl::new);
    }
    
    public static PlatformPlayer getPlayer(final Player player) {
        return (PlatformPlayer)new PlatformPlayerImpl(player);
    }
    
    public static List<PlatformPlayer> getPlayers() {
        return Sponge.getServer().getOnlinePlayers().stream().map((Function<? super Object, ?>)PlatformPlayerImpl::new).collect((Collector<? super Object, ?, List<PlatformPlayer>>)Collectors.toList());
    }
    
    public static void toConsole(final String command) {
        toConsole((CommandSource)Sponge.getServer().getConsole(), command);
    }
    
    public static void toConsole(final CommandSource source, final String command) {
        Task.builder().execute(() -> Sponge.getCommandManager().process(source, command)).submit((Object)DiscordLink.get());
    }
    
    public static boolean isGameReady() {
        return Sponge.getGame().getState() == GameState.SERVER_STARTED;
    }
    
    static {
        VERSION = "Sponge-" + Sponge.getPlatform().getMinecraftVersion().getName();
    }
}
