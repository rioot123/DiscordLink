// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.sponge;

import net.dv8tion.jda.api.entities.User;
import net.dirtcraft.spongediscordlib.users.DiscordMember;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRoles;
import net.dirtcraft.discordlink.storage.tables.Verification;
import net.dirtcraft.discordlink.DiscordLink;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.command.CommandException;
import net.dirtcraft.discordlink.utility.Utility;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.CommandSource;
import net.dirtcraft.discordlink.storage.Database;
import org.spongepowered.api.command.spec.CommandExecutor;

public class UnVerify implements CommandExecutor
{
    private final Database storage;
    
    public UnVerify(final Database storage) {
        this.storage = storage;
    }
    
    public CommandResult execute(final CommandSource source, final CommandContext args) throws CommandException {
        if (!(source instanceof Player)) {
            throw new CommandException(Utility.format("&cOnly a player can unverify their Discord account!"));
        }
        final Player player = (Player)source;
        Task.builder().async().execute(() -> this.execute(player)).submit((Object)DiscordLink.get());
        return CommandResult.success();
    }
    
    private void execute(final Player player) {
        final Verification.VerificationData data = this.storage.getVerificationData(player.getUniqueId()).orElse(null);
        if (data == null) {
            player.sendMessage(Utility.format("&cYour account is not verified!"));
        }
        else {
            data.deleteRecord();
            final String response = data.getDiscordUser().map(user -> "&7The account &6" + user.getName() + "&8#&7" + user.getDiscriminator() + " has been &cunverified").orElse("&7Your account has been &cunverified");
            player.sendMessage(Utility.format(response));
            data.getGuildMember().ifPresent(member -> member.removeRoleIfPresent(DiscordRoles.VERIFIED));
        }
    }
}
