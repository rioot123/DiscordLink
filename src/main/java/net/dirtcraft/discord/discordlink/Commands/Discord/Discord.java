package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.Channels;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Commands.Sources.DiscordResponder;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

public class Discord implements DiscordCommandExecutor {
    @SuppressWarnings("deprecation") // stupid bukkit bullcrap
    @Override
    public void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException {
        if (args.isEmpty()) throw new DiscordCommandException("Invalid Minecraft name");
        final String minecraftIdentifier = args.get(0);
        OfflinePlayer offlinePlayer;
        Pattern pattern = Pattern.compile("(\\d{8}-?\\d{4}-?\\d{4}-?\\d{4}-?\\d{12})");
        if ((minecraftIdentifier.length() == 32 || minecraftIdentifier.length() == 36) && pattern.matcher(minecraftIdentifier).matches()){
            UUID uuid = UUID.fromString(minecraftIdentifier);
            offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        } else {
            offlinePlayer = Bukkit.getOfflinePlayer(minecraftIdentifier);
        }
        if (!offlinePlayer.hasPlayedBefore()) throw new DiscordCommandException("Invalid username or UUID.");
        Optional<GuildMember> optDiscordSource = GuildMember.fromPlayerId(offlinePlayer.getUniqueId());
        if (!optDiscordSource.isPresent()) throw new DiscordCommandException("User not verified.");
        source.sendCommandResponse("Discord Username", "\\" + optDiscordSource.get().getAsMention());
    }
}
