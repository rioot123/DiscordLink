package net.dirtcraft.discord.discordlink.Commands.Discord;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlayerList implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String command, List<String> args) {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        ArrayList<String> playerNames = new ArrayList<>();
        Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");


        int visiblePlayers = 0;

        if (ess == null) {
            visiblePlayers = players.size();
            for (Player player : players) {
                playerNames.add(player.getName());
            }
        } else {
            for (Player player : players) {
                User user = ess.getUser(player);
                if (user.isVanished()) continue;

                if (user.isAfk()) {
                    playerNames.add(user.getName() + " " + "—" + " " + "**AFK**");
                } else {
                    playerNames.add(user.getName());
                }
                visiblePlayers++;
            }
        }

        playerNames.sort(String::compareToIgnoreCase);

        EmbedBuilder embed = Utility.embedBuilder();
        if (visiblePlayers > 1) {
            embed.addField("__**" + visiblePlayers + "** players online__", String.join("\n", playerNames), false);
        } else if (visiblePlayers == 1) {
            embed.addField("__**" + visiblePlayers + "** player online__", String.join("\n", playerNames), false);
        } else {
            embed.setDescription("There are no players playing **" + PluginConfiguration.Main.SERVER_NAME + "**!");
        }
        embed.setFooter("Requested By: " + source.getUser().getAsTag(), source.getUser().getAvatarUrl());

        GameChat.sendMessage(embed.build());
    }
}
