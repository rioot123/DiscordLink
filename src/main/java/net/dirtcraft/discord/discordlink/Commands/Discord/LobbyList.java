package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dirtcraft.discord.discordlink.Storage.Permission;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformPlayer;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUtils;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dv8tion.jda.api.EmbedBuilder;
import net.md_5.bungee.api.ProxyServer;

import java.util.stream.Collectors;

public class LobbyList implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String command, java.util.List<String> args) throws DiscordCommandException {
        final java.util.List<String> players = ProxyServer.getInstance()
                .getServerInfo(PluginConfiguration.HubChat.serverId)
                .getPlayers()
                .stream()
                .map(PlatformUtils::getPlayer)
                .filter(PlatformPlayer::notVanished)
                .sorted(this::sortPlayer)
                .map(this::formatPlayer)
                .collect(Collectors.toList());

        EmbedBuilder embed = Utility.embedBuilder();
        if (players.size() == 1) embed.addField("__**" + players.size() + "** player in lobby__", String.join("\n", players), false);
        else if (!players.isEmpty()) embed.addField("__**" + players.size() + "** players in lobby__", String.join("\n", players), false);
        else embed.setDescription("There are no players connected to lobby!");
        embed.setFooter("Requested By: " + source.getUser().getAsTag(), source.getUser().getAvatarUrl());
        source.sendCommandResponse(embed.build());
    }

    private String formatPlayer(PlatformPlayer platformPlayer){
        String name = platformPlayer.getName();
        name = Utility.sanitiseMinecraftText(name);
        if (platformPlayer.hasPermission(Permission.ROLES_STAFF)) name = "**" + name + "**";
        return name;
    }

    private int sortPlayer(PlatformPlayer a, PlatformPlayer b){
        if (a.hasPermission(Permission.ROLES_STAFF) && !b.hasPermission(Permission.ROLES_STAFF)) return -1;
        else if (b.hasPermission(Permission.ROLES_STAFF) && !a.hasPermission(Permission.ROLES_STAFF)) return 1;
        else return a.getName().compareTo(b.getName());
    }
}
