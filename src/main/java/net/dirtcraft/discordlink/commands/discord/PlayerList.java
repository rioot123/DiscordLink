// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dirtcraft.discordlink.utility.Utility;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Comparator;
import net.dirtcraft.spongediscordlib.users.platform.PlatformPlayer;
import net.dirtcraft.discordlink.users.platform.PlatformProvider;
import java.util.List;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;

public class PlayerList implements DiscordCommandExecutor
{
    public void execute(final MessageSource source, final String command, final List<String> args) {
        final List<String> players = PlatformProvider.getPlayers().stream().filter(PlatformPlayer::notVanished).sorted((Comparator<? super Object>)this::sortPlayer).map((Function<? super Object, ?>)this::formatPlayer).collect((Collector<? super Object, ?, List<String>>)Collectors.toList());
        final EmbedBuilder embed = Utility.embedBuilder();
        if (players.size() == 1) {
            embed.addField("__**" + players.size() + "** player online__", String.join("\n", players), false);
        }
        else if (!players.isEmpty()) {
            embed.addField("__**" + players.size() + "** players online__", String.join("\n", players), false);
        }
        else {
            embed.setDescription((CharSequence)("There are no players playing **" + SpongeDiscordLib.getServerName() + "**!"));
        }
        embed.setFooter("Requested By: " + source.getUser().getAsTag(), source.getUser().getAvatarUrl());
        source.sendCommandResponse(embed.build());
    }
    
    private String formatPlayer(final PlatformPlayer platformPlayer) {
        String name = platformPlayer.getNameAndPrefix();
        name = Utility.sanitiseMinecraftText(name);
        if (platformPlayer.hasPermission("discordlink.roles.staff")) {
            name = "**" + name + "**";
        }
        return name;
    }
    
    private int sortPlayer(final PlatformPlayer a, final PlatformPlayer b) {
        if (a.hasPermission("discordlink.roles.staff") && !b.hasPermission("discordlink.roles.staff")) {
            return -1;
        }
        if (b.hasPermission("discordlink.roles.staff") && !a.hasPermission("discordlink.roles.staff")) {
            return 1;
        }
        return a.getName().compareTo(b.getName());
    }
}
