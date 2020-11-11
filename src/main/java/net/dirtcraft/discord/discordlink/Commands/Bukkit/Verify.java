package net.dirtcraft.discord.discordlink.Commands.Bukkit;

import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Storage.Database;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Verify implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return false;
        if (strings.length != 1) return false;
        Player player = (Player) commandSender;
        String code = strings[0];
        Bukkit.getScheduler().runTaskAsynchronously(DiscordLink.getInstance(), ()-> player.sendMessage(verify(player, code)));
        return true;
    }
    private String verify(Player player, String code) {
        final Database storage = DiscordLink.getInstance().getStorage();
        GuildMember discord = GuildMember.fromPlayerId(player.getUniqueId()).orElse(null);
        if (discord != null) {
            final User user = discord.getUser();
            if (user.getName() == null) return Utility.formatColourCodes("&cYour account is already verified with &6" + user.getName() + "&8#&7" + user.getDiscriminator() + "&c!");
            else return Utility.formatColourCodes("&cYour account is already verified!");
        } else if (!storage.validCode(code)) return Utility.formatColourCodes("&cThe code &e" + code + "&c is not valid!");
        storage.updateRecord(code, player.getUniqueId());
        discord = GuildMember.fromPlayerId(player.getUniqueId()).orElse(null);
        if (discord == null) return Utility.formatColourCodes("&cCould not verify your Discord account, please contact an Administrator!");
        final Role verifiedRole = GameChat.getGuild().getRoleById(PluginConfiguration.Roles.verifiedRoleID);
        final Role donorRole = GameChat.getGuild().getRoleById(PluginConfiguration.Roles.donatorRoleID);
        final Guild controller = GameChat.getGuild();
        if (verifiedRole != null && !discord.isVerified()) controller.addRoleToMember(discord, verifiedRole).queue();
        if (!discord.isStaff()) controller.modifyNickname(discord, player.getName()).queue();
        if (donorRole != null && !discord.isDonor() && player.hasPermission("discordlink.donator")) controller.addRoleToMember(discord, donorRole).queue();
        return Utility.formatColourCodes("&2Successfully linked");
    }
}
