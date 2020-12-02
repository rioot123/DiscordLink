package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.Channels;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Commands.Sources.DiscordResponder;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUser;
import org.bukkit.Bukkit;

import java.util.List;

public class Unstuck implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException {
        final PlatformUser user = source.getPlayerData().orElseThrow(()->new DiscordCommandException("Could not retrieve linked player!"));
        if (user.isOnline()){
            String username = user.getName().orElseThrow(()->new DiscordCommandException("Player name not found!"));
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spawn " + username);
        } else {
            DiscordLink.getInstance().tpSpawnList.add(user.getUUID());
            source.sendCommandResponse("DiscordLink Spawn Service", user.getName() + " will be moved to spawn when they next join!");
        }
    }
}
