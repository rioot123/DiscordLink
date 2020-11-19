package net.dirtcraft.discord.discordlink.Commands;

import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.API.Roles;
import net.dirtcraft.discord.discordlink.Commands.Discord.StopServer;
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
        DiscordCommand halt = DiscordCommand.builder()
                .setDescription("Abruptly stops the server.")
                .setCommandExecutor(new StopServer())
                .setRequiredRoles(Roles.DIRTY)
                .build();

        register(halt, "halt");
    }

    public void process(MessageSource member, String args){
        try {
            String[] command = args == null || defaultAliases.contains(args)? new String[0] : args.toLowerCase().split(" ");
            execute(member, null, new ArrayList<>(Arrays.asList(command)));
        } catch (Exception e){
            String message = e.getMessage() != null? e.getMessage() : "an error occurred while executing the command.";
            Utility.sendCommandError(member, message);
        } finally {
            member.getMessage().delete().queue();
        }
    }

    @Override
    public void defaultResponse(MessageSource member, String command, List<String> args) {
        EmbedBuilder embed = Utility.embedBuilder();
        String pre = PluginConfiguration.Main.discordCommand;
        getCommandMap().forEach((alias, cmd)->{
            if (!cmd.hasPermission(member)) return;
            String title = pre + alias + " " + cmd.getUsage();
            embed.addField(title, cmd.getDescription(), false);
        });
        member.sendCommandResponse(embed.build());
    }

}