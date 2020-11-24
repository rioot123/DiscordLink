package net.dirtcraft.discord.discordlink.Commands.Bungee.Settings;

import net.dirtcraft.discord.discordlink.API.Roles;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Utility.Pair;
import net.md_5.bungee.api.CommandSender;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.dirtcraft.discord.discordlink.Storage.PluginConfiguration.Roles.*;

@SuppressWarnings("deprecation")
public class RoleSettings implements SubCommand {
    Map<String, SubCommand> fields = Stream.of(
            new Pair<String, SubCommand>("owner", this::owner),
            new Pair<String, SubCommand>("dirty", this::dirty),
            new Pair<String, SubCommand>("admin", this::admin),
            new Pair<String, SubCommand>("moderator", this::moderator),
            new Pair<String, SubCommand>("helper", this::helper),
            new Pair<String, SubCommand>("staff", this::staff),
            new Pair<String, SubCommand>("verified", this::verified),
            new Pair<String, SubCommand>("donor", this::donor),
            new Pair<String, SubCommand>("nitro", this::nitro)
    ).collect(Collectors.toMap(Pair::getKey, Pair::getValue));

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (args.isEmpty()) {
            sender.sendMessage("<" + String.join("|", fields.keySet()) + ">");
            return;
        }
        SubCommand subCommand = fields.get(args.remove(0));
        if (subCommand == null) sender.sendMessage("<" + String.join("|", fields.keySet()) + ">");
        else subCommand.execute(sender, args);
    }

    public void owner(CommandSender sender, List<String> args) {
        if (args.isEmpty()) sender.sendMessage(String.valueOf(ownerRoleID));
        else if (!args.get(0).matches("\\d+")) sender.sendMessage("invalid number!");
        else {
            ownerRoleID = Long.parseLong(args.get(0));
            DiscordLink.getInstance().saveConfig();
            Roles.OWNER.id = ownerRoleID;
        }
    }

    public void dirty(CommandSender sender, List<String> args) {
        if (args.isEmpty()) sender.sendMessage(String.valueOf(dirtyRoleID));
        else if (!args.get(0).matches("\\d+")) sender.sendMessage("invalid number!");
        else {
            dirtyRoleID = Long.parseLong(args.get(0));
            DiscordLink.getInstance().saveConfig();
            Roles.DIRTY.id = dirtyRoleID;
        }
    }

    public void admin(CommandSender sender, List<String> args) {
        if (args.isEmpty()) sender.sendMessage(String.valueOf(adminRoleID));
        else if (!args.get(0).matches("\\d+")) sender.sendMessage("invalid number!");
        else {
            adminRoleID = Long.parseLong(args.get(0));
            DiscordLink.getInstance().saveConfig();
            Roles.ADMIN.id = adminRoleID;
        }
    }

    public void moderator(CommandSender sender, List<String> args) {
        if (args.isEmpty()) sender.sendMessage(String.valueOf(moderatorRoleID));
        else if (!args.get(0).matches("\\d+")) sender.sendMessage("invalid number!");
        else {
            moderatorRoleID = Long.parseLong(args.get(0));
            DiscordLink.getInstance().saveConfig();
            Roles.MOD.id = moderatorRoleID;
        }
    }

    public void helper(CommandSender sender, List<String> args) {
        if (args.isEmpty()) sender.sendMessage(String.valueOf(helperRoleID));
        else if (!args.get(0).matches("\\d+")) sender.sendMessage("invalid number!");
        else {
            helperRoleID = Long.parseLong(args.get(0));
            DiscordLink.getInstance().saveConfig();
            Roles.HELPER.id = helperRoleID;
        }
    }

    public void staff(CommandSender sender, List<String> args) {
        if (args.isEmpty()) sender.sendMessage(String.valueOf(staffRoleID));
        else if (!args.get(0).matches("\\d+")) sender.sendMessage("invalid number!");
        else {
            staffRoleID = Long.parseLong(args.get(0));
            DiscordLink.getInstance().saveConfig();
            Roles.STAFF.id = staffRoleID;
        }
    }

    public void verified(CommandSender sender, List<String> args) {
        if (args.isEmpty()) sender.sendMessage(String.valueOf(verifiedRoleID));
        else if (!args.get(0).matches("\\d+")) sender.sendMessage("invalid number!");
        else {
            verifiedRoleID = Long.parseLong(args.get(0));
            DiscordLink.getInstance().saveConfig();
            Roles.VERIFIED.id = verifiedRoleID;
        }
    }

    public void donor(CommandSender sender, List<String> args) {
        if (args.isEmpty()) sender.sendMessage(String.valueOf(donatorRoleID));
        else if (!args.get(0).matches("\\d+")) sender.sendMessage("invalid number!");
        else {
            donatorRoleID = Long.parseLong(args.get(0));
            DiscordLink.getInstance().saveConfig();
            Roles.DONOR.id = donatorRoleID;
        }
    }

    public void nitro(CommandSender sender, List<String> args) {
        if (args.isEmpty()) sender.sendMessage(String.valueOf(nitroRoleID));
        else if (!args.get(0).matches("\\d+")) sender.sendMessage("invalid number!");
        else {
            nitroRoleID = Long.parseLong(args.get(0));
            DiscordLink.getInstance().saveConfig();
            Roles.NITRO.id = nitroRoleID;
        }
    }
}
