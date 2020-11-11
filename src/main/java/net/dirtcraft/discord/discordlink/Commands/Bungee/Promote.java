package net.dirtcraft.discord.discordlink.Commands.Bungee;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Storage.Permission;
import net.dirtcraft.discord.discordlink.Storage.Settings;
import net.dirtcraft.discord.discordlink.Utility.Permission.PermissionUtils;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class Promote extends Command {
    public Promote() {
        super("promote");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText("§cYou must be a player to use this command!."));
            return;
        } else if (!sender.hasPermission(Permission.PROMOTE_PERMISSION)) {
            sender.sendMessage(TextComponent.fromLegacyText("§cYou do not have permission to do that."));
            return;
        } else if (args.length < 1){
            sender.sendMessage(TextComponent.fromLegacyText("§cYou must specify a target to promote."));
            return;
        }
        UUID secret = UUID.randomUUID();
        UUID player = ((ProxiedPlayer) sender).getUniqueId();
        String target =args[0];
        String track = args.length < 2 ? "staff" : args[1];
        DiscordLink.getInstance().getChannelHandler().registerCallback(secret, success->responseHandler((ProxiedPlayer) sender, target, success));
        sendPacket(secret, player, target, track, ((ProxiedPlayer) sender).getServer());
    }

    @SuppressWarnings("UnstableApiUsage")
    private void sendPacket(UUID secret, UUID source, String target, String track, Server server){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(Settings.PROMOTION_CHANNEL);
        out.writeUTF(secret.toString());
        out.writeUTF(source.toString());
        out.writeUTF(target);
        out.writeUTF(track);
        out.writeBoolean(true);
        server.sendData(Settings.ROOT_CHANNEL, out.toByteArray());
    }

    private void responseHandler(ProxiedPlayer sender, String name, PermissionUtils.RankUpdate rankUpdate) {
        if (rankUpdate == null) {
            sender.sendMessage(TextComponent.fromLegacyText("§cYou do not have permission to promote this user."));
            return;
        }
        PermissionUtils perms = PermissionUtils.INSTANCE;
        sender.sendMessage(TextComponent.fromLegacyText("§2Successfully §a§lpromoted §6" + name + "."));
        if (rankUpdate.added != null) sender.sendMessage(TextComponent.fromLegacyText("§bUpdated Rank: §e" + rankUpdate.added));
        else sender.sendMessage(TextComponent.fromLegacyText("§bUpdated Rank: §edefault"));
        if (rankUpdate.removed != null) sender.sendMessage(TextComponent.fromLegacyText("§3Previous Rank: §6" + rankUpdate.removed));
        else sender.sendMessage(TextComponent.fromLegacyText("§3Previous Rank: §6default"));
        if (Permission.canModify(sender, rankUpdate.added, rankUpdate.removed)){
            sender.sendMessage(TextComponent.fromLegacyText("§2Successfully updated bungee permissions!"));
            if (rankUpdate.removed != null) perms.removeRank(rankUpdate.target, rankUpdate.removed);
            if (rankUpdate.added != null) perms.addRank(rankUpdate.target, rankUpdate.added);
        }
        if (Utility.assignStaffRoles(rankUpdate)) sender.sendMessage(TextComponent.fromLegacyText("§2Successfully set discord roles."));
        else sender.sendMessage(TextComponent.fromLegacyText("§4Failed to set discord roles. Please notify a manager."));
    }
}
