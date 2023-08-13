// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.sponge;

import net.dv8tion.jda.api.entities.User;
import java.net.MalformedURLException;
import org.spongepowered.api.text.action.ClickAction;
import java.net.URL;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.Text;
import net.dirtcraft.discordlink.users.platform.PlatformProvider;
import net.dirtcraft.spongediscordlib.users.DiscordMember;
import java.util.function.Function;
import net.dirtcraft.discordlink.storage.tables.Verification;
import java.util.Optional;
import net.dirtcraft.discordlink.DiscordLink;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.command.CommandException;
import net.dirtcraft.discordlink.utility.Utility;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import javax.annotation.Nonnull;
import org.spongepowered.api.command.CommandSource;
import net.dirtcraft.discordlink.storage.Database;
import org.spongepowered.api.command.spec.CommandExecutor;

public class Verify implements CommandExecutor
{
    private final Database storage;
    
    public Verify(final Database storage) {
        this.storage = storage;
    }
    
    @Nonnull
    public CommandResult execute(@Nonnull final CommandSource source, @Nonnull final CommandContext args) throws CommandException {
        if (!(source instanceof Player)) {
            throw new CommandException(Utility.format("&cOnly a player can verify their Discord account!"));
        }
        final Optional<String> code = (Optional<String>)args.getOne("code");
        final Player player = (Player)source;
        Task.builder().async().execute((Runnable)code.map(s -> () -> this.verify(player, s)).orElse(() -> this.usage(player))).submit((Object)DiscordLink.get());
        return CommandResult.success();
    }
    
    private void verify(final Player player, final String code) {
        Optional<Verification.VerificationData> optData = this.storage.getVerificationData(player.getUniqueId());
        if (optData.isPresent() && !optData.flatMap((Function<? super Verification.VerificationData, ? extends Optional<?>>)Verification.VerificationData::getMember).isPresent()) {
            String invite = "&cYour Discord account has already been verified, but it is not in the DirtCraft Discord!";
            invite += "\n&5&nClick Me&7 to &ajoin &7it";
            this.sendDiscordInvite(player, invite);
            return;
        }
        if (optData.isPresent()) {
            this.sendAlreadyVerifiedError(player, optData);
            return;
        }
        if (!(optData = this.storage.getPendingData(code)).isPresent()) {
            player.sendMessage(Utility.format("&cThe code &e" + code + "&c is not valid!"));
            return;
        }
        final DiscordMember member = optData.flatMap((Function<? super Verification.VerificationData, ? extends Optional<? extends DiscordMember>>)Verification.VerificationData::getGuildMember).orElse(null);
        if (member == null && !optData.flatMap((Function<? super Verification.VerificationData, ? extends Optional<?>>)Verification.VerificationData::getDiscordUser).isPresent()) {
            player.sendMessage(Utility.format("&cCould not verify your Discord account, please contact an Administrator!"));
        }
        else if (member == null) {
            String invite2 = "&cYour Discord account has been verified, but it is not in the DirtCraft Discord!";
            invite2 += "\n&5&nClick Me&7 to &ajoin &7it";
            this.sendDiscordInvite(player, invite2);
        }
        else {
            this.storage.updateRecord(code, player.getUniqueId());
            Utility.setRoles(PlatformProvider.getPlayer(player), member);
            final String discordName = member.getUser().getName();
            final String discordTag = member.getUser().getDiscriminator();
            player.sendMessage(Utility.format("&7Successfully verified &6" + player.getName() + "&7 with &6" + discordName + "&8#&7" + discordTag));
        }
    }
    
    private void usage(final Player player) {
        final Optional<Verification.VerificationData> optData = this.storage.getVerificationData(player.getUniqueId());
        if (optData.flatMap((Function<? super Verification.VerificationData, ? extends Optional<?>>)Verification.VerificationData::getGuildMember).isPresent()) {
            this.sendAlreadyVerifiedError(player, optData);
        }
        else {
            final String invite = "\n&5&nClick Me&7 to link your &9Discord&7 account and unlock additional features!\n";
            this.sendDiscordInvite(player, invite);
        }
    }
    
    private void sendDiscordInvite(final Player player, final String message) {
        final Text.Builder text = Text.builder().append(new Text[] { Utility.format(message) });
        try {
            text.onHover((HoverAction)TextActions.showText(Utility.format("&5&nClick Me&7 to verify your Discord account!")));
            text.onClick((ClickAction)TextActions.openUrl(new URL("http://verify.dirtcraft.gg/")));
        }
        catch (MalformedURLException exception) {
            text.onHover((HoverAction)TextActions.showText(Utility.format("&cMalformed URL, contact Administrator!")));
        }
        player.sendMessage(text.build());
    }
    
    private void sendAlreadyVerifiedError(final Player player, final Optional<Verification.VerificationData> optData) {
        final String response = optData.flatMap((Function<? super Verification.VerificationData, ? extends Optional<?>>)Verification.VerificationData::getDiscordUser).map(user -> "&cYour account is already verified with &6" + user.getName() + "&8#&7" + user.getDiscriminator() + "&c!").orElse("&cYour account is already verified!");
        player.sendMessage(Utility.format(response));
    }
}
