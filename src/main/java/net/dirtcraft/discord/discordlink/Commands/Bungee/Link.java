package net.dirtcraft.discord.discordlink.Commands.Bungee;

import net.dirtcraft.discord.discordlink.API.GameChats;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Storage.Database;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.concurrent.CompletableFuture;

public class Link extends Command {
    public Link() {
        super("verify");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer) || args.length < 1) return;
        ProxiedPlayer player = (ProxiedPlayer) sender;
        String code = args[0];
        CompletableFuture.runAsync(()-> player.chat(verify(player, code)));
    }

    private String verify(ProxiedPlayer player, String code) {
        final Database storage = DiscordLink.getInstance().getStorage();
        GuildMember discord = GuildMember.fromPlayerId(player.getUniqueId()).orElse(null);
        if (discord != null) {
            final User user = discord.getUser();
            return Utility.formatColourCodes("&cYour account is already verified with &6" + user.getName() + "&8#&7" + user.getDiscriminator() + "&c!");
        } else if (!storage.validCode(code)) return Utility.formatColourCodes("&cThe code &e" + code + "&c is not valid!");
        storage.updateRecord(code, player.getUniqueId());
        discord = GuildMember.fromPlayerId(player.getUniqueId()).orElse(null);
        if (discord == null) return Utility.formatColourCodes("&cCould not verify your Discord account, please contact an Administrator!");
        final Role verifiedRole = GameChats.getGuild().getRoleById(PluginConfiguration.Roles.verifiedRoleID);
        final Role donorRole = GameChats.getGuild().getRoleById(PluginConfiguration.Roles.donatorRoleID);
        final Guild controller = GameChats.getGuild();
        if (verifiedRole != null && !discord.isVerified()) controller.addRoleToMember(discord, verifiedRole).queue();
        if (!discord.isStaff()) controller.modifyNickname(discord, player.getName()).queue();
        if (donorRole != null && !discord.isDonor() && player.hasPermission("discordlink.donator")) controller.addRoleToMember(discord, donorRole).queue();
        return Utility.formatColourCodes("&2Successfully linked");
    }
}