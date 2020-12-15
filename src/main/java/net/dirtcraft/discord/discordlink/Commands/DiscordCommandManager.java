package net.dirtcraft.discord.discordlink.Commands;

import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.API.Roles;
import net.dirtcraft.discord.discordlink.Commands.Discord.LobbyList;
import net.dirtcraft.discord.discordlink.Commands.Discord.MuteBase;
import net.dirtcraft.discord.discordlink.Commands.Discord.StopServer;
import net.dirtcraft.discord.discordlink.Commands.Discord.Version;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class DiscordCommandManager extends DiscordCommandTree {

    private final HashSet<String> defaultAliases = new HashSet<>(Arrays.asList("", "help"));

    public DiscordCommandManager() {
        DiscordCommand mute = DiscordCommand.builder()
                .setDescription("Command base for mute manipulation")
                .setCommandExecutor(new MuteBase())
                .setRequiredRoles(Roles.STAFF)
                .build();

        DiscordCommand halt = DiscordCommand.builder()
                .setDescription("Abruptly stops the server.")
                .setCommandExecutor(new StopServer())
                .setRequiredRoles(Roles.DIRTY)
                .build();

        DiscordCommand version = DiscordCommand.builder()
                .setDescription("Shows version info.")
                .setCommandExecutor(new Version())
                .setRequiredRoles(Roles.DIRTY)
                .build();

        DiscordCommand list = DiscordCommand.builder()
                .setDescription("Shows lobby players.")
                .setCommandExecutor(new LobbyList())
                .build();

        register(list, "list");
        register(mute, "mute");
        register(halt, "proxy-halt");
        register(version, "version");
    }

    public void process(MessageSource member, String args){
        try {
            String[] command = args == null || defaultAliases.contains(args)? new String[0] : args.split(" ");
            execute(member, null, new ArrayList<>(Arrays.asList(command)));
        } catch (Exception e){
            String message = e.getMessage() != null? e.getMessage() : "an error occurred while executing the command.";
            Utility.sendCommandError(member, message);
        } finally {
            if (!member.isPrivateMessage()) member.getMessage().delete().queue();
        }
    }

    @Override
    public void defaultResponse(MessageSource member, String command, List<String> args) {
        EmbedBuilder embed = Utility.embedBuilder();
        String pre = PluginConfiguration.Prefixes.discordCommand;
        getCommandMap().forEach((alias, cmd)->{
            if (!cmd.hasPermission(member)) return;
            String title = pre + alias + " " + cmd.getUsage();
            embed.addField(title, cmd.getDescription(), false);
            embed.setFooter("Requested By: " + member.getUser().getAsTag(), member.getUser().getAvatarUrl());
        });
        member.sendCommandResponse(embed.build());
    }
}