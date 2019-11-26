package net.dirtcraft.discord.discordlink.Commands.Discord;

import io.github.nucleuspowered.nucleus.api.NucleusAPI;
import net.dirtcraft.discord.discordlink.API.DiscordSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Utility;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.Collection;

public class PlayerList implements DiscordCommandExecutor {
    @Override
    public void execute(DiscordSource member, String[] command, MessageReceivedEvent event) {
        Collection<Player> players = Sponge.getServer().getOnlinePlayers();

        ArrayList<String> playerNames = new ArrayList<>();
        players.forEach(online -> {
            if (NucleusAPI.getAFKService().isPresent()) {
                if (NucleusAPI.getAFKService().get().isAFK(online)) {
                    playerNames.add(online.getName() + " " + "—" + " " + "**AFK**");
                } else {
                    playerNames.add(online.getName());
                }
            } else {
                playerNames.add(online.getName());
            }
        });

        playerNames.sort(String::compareToIgnoreCase);

        EmbedBuilder embed = Utility.embedBuilder();
        if (players.size() > 1) {
            embed.addField("__**" + players.size() + "** players online__", String.join("\n", playerNames), false);
        } else if (players.size() == 1) {
            embed.addField("__**" + players.size() + "** player online__", String.join("\n", playerNames), false);
        } else {
            embed.setDescription("There are no players playing **" + SpongeDiscordLib.getServerName() + "**!");
        }
        embed.setFooter("Requested By: " + member.getUser().getAsTag(), event.getAuthor().getAvatarUrl());

        DiscordLink
                .getJDA()
                .getTextChannelById(SpongeDiscordLib.getGamechatChannelID())
                .sendMessage(embed.build())
                .queue();
    }
}
