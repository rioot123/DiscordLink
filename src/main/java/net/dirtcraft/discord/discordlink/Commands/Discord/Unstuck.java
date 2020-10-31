package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class Unstuck implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException {
        final OfflinePlayer user = source.getPlayerData().orElseThrow(()->new DiscordCommandException("Could not retrieve linked player!"));
        if (user.isOnline() && user.getName() != null){
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spawn " + user.getName());
            GameChat.sendMessage(user.getName() + " has been moved to spawn!");
        } else if (user.getUniqueId() != null) {
            DiscordLink.getInstance().tpSpawnList.add(user.getUniqueId());
            GameChat.sendMessage(user.getName() + " will be moved to spawn when they next join!");
        } else {
            throw new DiscordCommandException("Unable to send player to spawn");
        }
    }
}
