package net.dirtcraft.discordlink.forge.platform;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import net.dirtcraft.discordlink.forge.DiscordLink;
import net.dirtcraft.discordlink.api.users.platform.PlatformPlayer;
import net.dirtcraft.discordlink.api.users.platform.PlatformUser;
import net.dirtcraft.discordlink.common.commands.sources.ConsoleSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.server.management.PlayerProfileCache;
import org.spongepowered.api.GameState;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlatformProvider {
    public static final String VERSION = "Forge-1.16.5";
    private final IntegratedServer server;
    private final PlayerProfileCache cache;
    protected final PlayerList list;

    public PlatformProvider(IntegratedServer server){
        this.cache = server.getPlayerProfileCache();
        this.list = server.getPlayerList();
        this.server = server;
    }

    public Optional<PlatformUser> getPlayerOffline(UUID uuid){
        return Optional.ofNullable(cache.getProfileByUUID(uuid))
                .map(gp->new PlatformUserImpl(gp, list, this));
    }

    public Optional<PlatformUser> getPlayerOffline(String identifier){
        boolean isUUID = identifier.matches("(?i)[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
        if (isUUID) return getPlayerOffline(UUID.fromString(identifier));
        else return Optional.ofNullable(cache.getGameProfileForUsername(identifier))
                    .map(gp->new PlatformUserImpl(gp, list, this));
    }

    public Optional<PlatformUser> getPlayerOffline(String identifier, boolean acceptUUID){
        if (acceptUUID) return getPlayerOffline(identifier);
        else return Optional.ofNullable(cache.getGameProfileForUsername(identifier))
                .map(gp->new PlatformUserImpl(gp, list, this));
    }

    public Optional<PlatformPlayer> getPlayer(PlatformUser player){
        return player.getPlatformPlayer();
    }

    public PlatformPlayer getPlayer(ServerPlayerEntity player){
        return new PlatformPlayerImpl(player, list, this);
    }

    public List<PlatformPlayer> getPlayers(){
        return server.getPlayerList().getPlayers().stream()
                .map(player->new PlatformPlayerImpl(player, list, this))
                .collect(Collectors.toList());
    }

    public void toConsole(String command) {
        toConsole(Sponge.getServer().getConsole(), command);
    }

    public void toConsole(ConsoleSource source, String command) {
        Task.builder()
                .execute(() -> Sponge.getCommandManager().process(source, command))
                .submit(DiscordLink.get());
    }

    public boolean isGameReady(){
        return DiscordLink.get().isStarted();
    }

}