package net.dirtcraft.discord.discordlink.Commands.Bukkit;

import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Storage.Database;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUtils;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class Verify implements CommandExecutor {

    private final Database storage;

    public Verify(Database storage) {
        this.storage = storage;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only a player can verify their Discord account!");
            return false;
        }
        Optional<String> code = Optional.ofNullable(strings.length > 0? strings[0]: null);
        Player player = (Player) commandSender;
        Bukkit.getScheduler().runTaskAsynchronously(DiscordLink.getInstance(), code.<Runnable>map(s -> () -> verify(player, s)).orElse(() -> usage(player)));
        return true;
    }

    private void verify(Player player, String code) {
        Optional<Database.VerificationData> optData = storage.getVerificationData(player.getUniqueId());
        if (optData.isPresent() && !optData.flatMap(Database.VerificationData::getMember).isPresent()){
            String invite = "&cYour Discord account has already been verified, but it is not in the DirtCraft Discord!";
            invite += "\n&5&nClick Me&7 to &ajoin &7it";
            sendDiscordInvite(player, invite);
            return;
        } else if (optData.isPresent()) {
            sendAlreadyVerifiedError(player, optData);
            return;
        } else if (!(optData = storage.getPendingData(code)).isPresent()) {
            player.spigot().sendMessage(Utility.format("&cThe code &e" + code + "&c is not valid!"));
            return;
        }

        GuildMember member = optData.flatMap(Database.VerificationData::getGuildMember).orElse(null);
        if (member == null && !optData.flatMap(Database.VerificationData::getDiscordUser).isPresent()) {
            player.spigot().sendMessage(Utility.format("&cCould not verify your Discord account, please contact an Administrator!"));
        } else if (member == null) {
            String invite = "&cYour Discord account has been verified, but it is not in the DirtCraft Discord!";
            invite += "\n&5&nClick Me&7 to &ajoin &7it";
            sendDiscordInvite(player, invite);
        } else {
            storage.updateRecord(code, player.getUniqueId());
            Utility.setRoles(PlatformUtils.getPlayer(player), member);
            String discordName = member.getUser().getName();
            String discordTag = member.getUser().getDiscriminator();
            player.spigot().sendMessage(Utility.format("&7Successfully verified &6" + player.getName() + "&7 with &6" + discordName + "&8#&7" + discordTag));
        }
    }

    private void usage(Player player){
        Optional<Database.VerificationData> optData = storage.getVerificationData(player.getUniqueId());
        if (optData.flatMap(Database.VerificationData::getGuildMember).isPresent()) {
            sendAlreadyVerifiedError(player, optData);
        } else {
            String invite = "\n&5&nClick Me&7 to link your &9Discord&7 account and unlock additional features!\n";
            sendDiscordInvite(player, invite);
        }
    }

    private void sendDiscordInvite(Player player, String message){
        TextComponent text = new TextComponent(TextComponent.fromLegacyText(message));
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("&5&nClick Me&7 to verify your Discord account!")));
        text.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://verify.dirtcraft.gg/"));
        player.spigot().sendMessage(text);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private void sendAlreadyVerifiedError(Player player, Optional<Database.VerificationData> optData){
        String response = optData.flatMap(Database.VerificationData::getDiscordUser)
                .map(user -> "&cYour account is already verified with &6" + user.getName() + "&8#&7" + user.getDiscriminator() + "&c!")
                .orElse("&cYour account is already verified!");
        player.spigot().sendMessage(Utility.format(response));
    }
}