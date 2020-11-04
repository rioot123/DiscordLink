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

public class Unlink extends Command {
    public Unlink() {
        super("unverify");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) sender;
        CompletableFuture.runAsync(()-> player.chat(unverify(player)));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private String unverify(ProxiedPlayer player) {
        final Database storage = DiscordLink.getInstance().getStorage();
        GuildMember discord = GuildMember.fromPlayerId(player.getUniqueId()).orElse(null);
        User user = discord == null ? null : discord.getUser();
        storage.deleteRecord(player.getUniqueId());
        if (user == null) return Utility.formatColourCodes("&cYour account is not verified!");

        final Role verifiedRole = GameChats.getGuild().getRoleById(PluginConfiguration.Roles.verifiedRoleID);
        final Role donorRole = GameChats.getGuild().getRoleById(PluginConfiguration.Roles.donatorRoleID);
        final Guild guild = GameChats.getGuild();

        if (verifiedRole != null && discord.isVerified()) guild.removeRoleFromMember(discord, verifiedRole);
        if (donorRole != null && discord.isDonor()) guild.removeRoleFromMember(discord, donorRole);

        return Utility.formatColourCodes("&7The account &6" + user.getName() + "&8#&7" + user.getName() + " has been &cunverified");

    }
}
