package net.dirtcraft.discord.discordlink.Commands.Bungee;

import net.dirtcraft.discord.discordlink.API.Channels;
import net.dirtcraft.discord.discordlink.API.Roles;
import net.dirtcraft.discord.discordlink.Storage.Database;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dv8tion.jda.api.entities.Guild;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.concurrent.CompletableFuture;

public class Unlink extends Command {
    private final Database storage;
    public Unlink(Database storage) {
        super("unverify");
        this.storage = storage;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) sender;
        CompletableFuture.runAsync(()-> player.sendMessage(execute(player)));
    }

    private BaseComponent[] execute(ProxiedPlayer player){
        Database.VerificationData data = storage.getVerificationData(player.getUniqueId()).orElse(null);
        if (data == null){
            return Utility.format("&cYour account is not verified!");
        } else {
            data.deleteRecord();
            Guild guild = Channels.getGuild();
            String response = data.getDiscordUser()
                    .map(user->"&7The account &6" + user.getName() + "&8#&7" + user.getDiscriminator() + " has been &cunverified")
                    .orElse("&7Your account has been &cunverified");
            data.getGuildMember().ifPresent(member-> Utility.removeRoleIfPresent(guild, member, Roles.VERIFIED));
            return Utility.format(response);
        }
    }
}
