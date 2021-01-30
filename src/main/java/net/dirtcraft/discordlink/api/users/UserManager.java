package net.dirtcraft.discordlink.api.users;

import net.dirtcraft.discordlink.users.GuildMember;
import net.dirtcraft.discordlink.users.platform.PlatformPlayerImpl;
import net.dirtcraft.discordlink.users.platform.PlatformUserImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserManager {
    Optional<DiscordMember> getMember(long id);

    Optional<DiscordMember> getMember(UUID player);

    Optional<GuildMember> getMember(String s);

    Optional<PlatformUserImpl> getUser(String s);

    Optional<PlatformUserImpl> getUser(UUID uuid);

    List<PlatformPlayerImpl> getPlayers();
}
