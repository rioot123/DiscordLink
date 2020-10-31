package net.dirtcraft.discord.discordlink.Commands.Bukkit;

import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Database.Storage;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Unverify implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return false;
        Player player = (Player) commandSender;
        Bukkit.getScheduler().runTaskAsynchronously(DiscordLink.getInstance(), ()-> player.sendMessage(unverify(player)));
        return true;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private String unverify(Player player) {
        final Storage storage = DiscordLink.getInstance().getStorage();
        GuildMember discord = GuildMember.fromPlayerId(player.getUniqueId()).orElse(null);
        User user = discord == null ? null : discord.getUser();
        storage.deleteRecord(player.getUniqueId());
        if (user == null) return Utility.formatColourCodes("&cYour account is not verified!");

        final Role verifiedRole = GameChat.getGuild().getRoleById(PluginConfiguration.Roles.verifiedRoleID);
        final Role donorRole = GameChat.getGuild().getRoleById(PluginConfiguration.Roles.donatorRoleID);
        final Guild guild = GameChat.getGuild();

        if (discord.isVerified()) guild.removeRoleFromMember(discord, verifiedRole);
        if (discord.isDonor()) guild.removeRoleFromMember(discord, donorRole);

        return Utility.formatColourCodes("&7The account &6" + user.getName() + "&8#&7" + user.getName() + " has been &cunverified");

    }
}
