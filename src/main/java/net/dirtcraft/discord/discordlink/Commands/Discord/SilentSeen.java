package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Commands.Sources.PrivateSender;
import net.dirtcraft.discord.discordlink.Compatability.PlatformUser;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.Optional;

public class SilentSeen implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String cmd, List<String> args) throws DiscordCommandException {
        String target;
        if (args.isEmpty()){
            Optional<PlatformUser> optUser = source.getPlayerData();
            if (!optUser.isPresent()) throw new DiscordCommandException("You must be verified in order to not specify a player!");
            else target = optUser.get().getName().orElseThrow(()->new DiscordCommandException("Could not find specified target!"));
        } else {
            target = args.get(0);
        }
        String command = "seen " + target;
        PrivateSender sender = new PrivateSender(source, command);
        Bukkit.getScheduler().callSyncMethod(DiscordLink.getInstance(), ()->Bukkit.dispatchCommand(sender, command));
    }
}
