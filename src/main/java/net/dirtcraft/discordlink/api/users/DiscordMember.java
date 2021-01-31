package net.dirtcraft.discordlink.api.users;

import net.dirtcraft.discordlink.api.users.platform.PlatformPlayer;
import net.dirtcraft.discordlink.api.users.platform.PlatformUser;
import net.dirtcraft.discordlink.api.users.roles.DiscordRole;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Optional;

public interface DiscordMember extends Member {

    Optional<? extends PlatformPlayer> getPlayer();

    Optional<? extends PlatformUser> getPlayerData();

    void sendMessage(MessageEmbed embed);

    void sendMessage(String message);

    boolean isStaff();

    boolean isMuted();

    boolean isDonor();

    boolean isBoosting();

    boolean isVerified();

    boolean hasRole(DiscordRole role);

    void setRoleIfAbsent(DiscordRole role);

    void removeRoleIfPresent(DiscordRole role);

    void tryChangeNickname(String name);

    DiscordRole getHighestRank();

    String getChevron();

    String getNameStyle();

    void reloadRoles();
}
