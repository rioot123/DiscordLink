package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformPlayer;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUtils;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerList implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String command, List<String> args) {
        final List<String> players = PlatformUtils.getPlayers().stream()
                .filter(PlatformPlayer::notVanished)
                .map(PlatformPlayer::getName)
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());

        EmbedBuilder embed = Utility.embedBuilder();
        if (players.size() == 1) embed.addField("__**" + players.size() + "** player online__", String.join("\n", players), false);
        else if (!players.isEmpty()) embed.addField("__**" + players.size() + "** players online__", String.join("\n", players), false);
        else embed.setDescription("There are no players playing **" + SpongeDiscordLib.getServerName() + "**!");
        embed.setFooter("Requested By: " + source.getUser().getAsTag(), source.getUser().getAvatarUrl());
        GameChat.sendMessage(embed.build());
    }
}
