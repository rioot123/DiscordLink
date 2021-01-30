package net.dirtcraft.discordlink.api.users;

import net.dirtcraft.discordlink.api.users.platform.PlatformPlayer;
import net.dirtcraft.discordlink.api.users.platform.PlatformUser;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Optional;

public interface DiscordMember {

    Optional<PlatformPlayer> getPlayer();

    Optional<PlatformUser> getPlayerData();

    void sendMessage(MessageEmbed embed);

    void sendMessage(String message);

    boolean isStaff();

    boolean isMuted();

    boolean isDonor();

    boolean isBoosting();

    boolean isVerified();
}
