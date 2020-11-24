package net.dirtcraft.discord.discordlink.Utility.Compatability.Platform;

import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Storage.Database;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlatformUtils {
    public static final String VERSION = "BungeeCord";

    public static Optional<PlatformUser> getPlayerOffline(UUID uuid) {
        Database database = DiscordLink.getInstance().getStorage();
        final Optional<Database.VoterData> optVoter = database.getPlayerVoteData(uuid);
        String username;

        if (!optVoter.isPresent()) {
            username = APIHelper.getLatestUsername(uuid).orElse(null);
            if (username != null) database.createVoteRecord(uuid, username);
        } else if (!optVoter.map(Database.VoterData::getUsername).isPresent() || !optVoter.get().hasVotedInPastWeek()) {
            username = APIHelper.getLatestUsername(uuid).orElse(null);
            if (username != null) database.updateUsername(uuid, username);
        } else {
            username = optVoter.map(Database.VoterData::getUsername).get();
        }
        return Optional.of(new PlatformUser(uuid, username));
    }

    public static Optional<PlatformPlayer> getPlayer(PlatformUser player){
        return Optional.ofNullable(player.getPlayer()).map(PlatformPlayer::new);
    }

    public static PlatformPlayer getPlayer(ProxiedPlayer player){
        return new PlatformPlayer(player);
    }

    public static List<PlatformPlayer> getPlayers(){
        return ProxyServer.getInstance().getPlayers().stream()
                .map(PlatformPlayer::new)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isGameReady(){
        return true;
    }
}