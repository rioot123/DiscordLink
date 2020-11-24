package net.dirtcraft.discord.discordlink.Commands.Bungee;

import net.dirtcraft.discord.discordlink.API.Channels;
import net.dirtcraft.discord.discordlink.API.Roles;
import net.dirtcraft.discord.discordlink.Commands.Bungee.Settings.ChannelSettings;
import net.dirtcraft.discord.discordlink.Commands.Bungee.Settings.RoleSettings;
import net.dirtcraft.discord.discordlink.Commands.Bungee.Settings.SubCommand;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Storage.Permission;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Sanctions.SanctionUtils;
import net.dirtcraft.discord.discordlink.Utility.Pair;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.dirtcraft.discord.discordlink.Storage.PluginConfiguration.Roles.*;

public class SettingsBase extends Command {

    private Map<String, SubCommand> subCommands = Stream.of(
            new Pair<>("roles", new RoleSettings()),
            new Pair<>("channels", new ChannelSettings()),
            new Pair<String, SubCommand>("reload", this::reload)
    ).collect(Collectors.toMap(Pair::getKey, Pair::getValue));

    public SettingsBase() {
        super("settings");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(Permission.SETTINGS_MODIFY)) return;
        List<String> arguments = new ArrayList<>(Arrays.asList(args));
        if (arguments.isEmpty()) {
            sender.sendMessage("<" + String.join("|", subCommands.keySet()) + ">");
            return;
        }
        SubCommand subCommand = subCommands.get(arguments.remove(0));
        if (subCommand == null) sender.sendMessage("<" + String.join("|", subCommands.keySet()) + ">");
        else subCommand.execute(sender, arguments);
    }

    public void reload(CommandSender sender, List<String> args){
        DiscordLink.getInstance().reloadConfig();
        SanctionUtils.INSTANCE.updateChannel(PluginConfiguration.Channels.litebansChannel);
        Roles.OWNER.id = ownerRoleID;
        Roles.DIRTY.id = dirtyRoleID;
        Roles.ADMIN.id = adminRoleID;
        Roles.MOD.id = moderatorRoleID;
        Roles.HELPER.id = helperRoleID;
        Roles.STAFF.id = staffRoleID;
        Roles.VERIFIED.id = verifiedRoleID;
        Roles.DONOR.id = donatorRoleID;
        Roles.NITRO.id = nitroRoleID;
        Channels.update();
    }
}
