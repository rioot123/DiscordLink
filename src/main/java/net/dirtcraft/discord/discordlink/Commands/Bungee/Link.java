package net.dirtcraft.discord.discordlink.Commands.Bungee;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.Storage.Database;
import net.dirtcraft.discord.discordlink.Storage.Settings;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUtils;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class Link extends Command {
    private final Database storage;

    public Link(Database storage) {
        super("verify");
        this.storage = storage;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) sender;
        Supplier<BaseComponent[]> code = Optional.ofNullable(args.length == 0? null : args[0])
                .<Supplier<BaseComponent[]>>map(s -> () -> verify(player, s))
                .orElse(() -> usage(player));
        CompletableFuture.runAsync(()-> player.sendMessage(code.get()));
    }

    private BaseComponent[] verify(ProxiedPlayer player, String code) {
        Optional<Database.VerificationData> optData = storage.getVerificationData(player.getUniqueId());
        if (optData.isPresent() && !optData.flatMap(Database.VerificationData::getMember).isPresent()){
            String invite = "&cYour Discord account has already been verified, but it is not in the DirtCraft Discord!";
            invite += "\n&5&nClick Me&7 to &ajoin &7it";
            return sendDiscordInvite(invite);
        } else if (optData.isPresent()) {
            return sendAlreadyVerifiedError(optData);
        } else if (!(optData = storage.getPendingData(code)).isPresent()) {
            return Utility.format("&cThe code &e" + code + "&c is not valid!");
        }

        GuildMember member = optData.flatMap(Database.VerificationData::getGuildMember).orElse(null);
        if (member == null && !optData.flatMap(Database.VerificationData::getDiscordUser).isPresent()) {
            return Utility.format("&cCould not verify your Discord account, please contact an Administrator!");
        } else if (member == null) {
            String invite = "&cYour Discord account has been verified, but it is not in the DirtCraft Discord!";
            invite += "\n&5&nClick Me&7 to &ajoin &7it";
            return sendDiscordInvite(invite);
        } else {
            storage.updateRecord(code, player.getUniqueId());
            Utility.setRoles(PlatformUtils.getPlayer(player), member);
            sendPacket(player);
            String discordName = member.getUser().getName();
            String discordTag = member.getUser().getDiscriminator();
            return Utility.format("&7Successfully verified &6" + player.getName() + "&7 with &6" + discordName + "&8#&7" + discordTag);
        }
    }

    private BaseComponent[] usage(ProxiedPlayer player){
        Optional<Database.VerificationData> optData = storage.getVerificationData(player.getUniqueId());
        if (optData.flatMap(Database.VerificationData::getGuildMember).isPresent()) {
            return sendAlreadyVerifiedError(optData);
        } else {
            String invite = "\n&5&nClick Me&7 to link your &9Discord&7 account and unlock additional features!\n";
            return sendDiscordInvite(invite);
        }
    }

    private BaseComponent[] sendDiscordInvite(String message){
        TextComponent text = new TextComponent(Utility.format(message));
        text.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://verify.dirtcraft.gg/"));
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Utility.format("&5&nClick Me&7 to verify your Discord account!")));
        return new BaseComponent[]{text};
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private BaseComponent[] sendAlreadyVerifiedError(Optional<Database.VerificationData> optData){
        String response = optData.flatMap(Database.VerificationData::getDiscordUser)
                .map(user -> "&cYour account is already verified with &6" + user.getName() + "&8#&7" + user.getDiscriminator() + "&c!")
                .orElse("&cYour account is already verified!");
        return Utility.format(response);
    }

    @SuppressWarnings("UnstableApiUsage")
    private void sendPacket(ProxiedPlayer player){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(Settings.ROLES_CHANNEL);
        out.writeUTF(player.getUniqueId().toString());
        player.getServer().sendData(Settings.ROOT_CHANNEL, out.toByteArray());
    }


}