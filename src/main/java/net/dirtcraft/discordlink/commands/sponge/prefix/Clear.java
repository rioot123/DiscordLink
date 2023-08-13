// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.sponge.prefix;

import javax.annotation.Nonnull;
import org.spongepowered.api.command.source.ConsoleSource;
import net.dirtcraft.discordlink.users.permission.PermissionProvider;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.spec.CommandExecutor;

public class Clear implements CommandExecutor
{
    public CommandResult execute(final CommandSource src, final CommandContext args) throws CommandException {
        final User target = args.getOne("Target").orElseThrow(() -> new CommandException((Text)Text.of("§cYou must specify a target.")));
        if (target != src && !src.hasPermission("discordlink.prefix.others")) {
            throw new CommandException((Text)Text.of("§cYou do not have permission to set other players prefixes."));
        }
        PermissionProvider.INSTANCE.clearPlayerPrefix((ConsoleSource)this.getSource(src), target);
        return CommandResult.success();
    }
    
    private net.dirtcraft.discordlink.commands.sources.ConsoleSource getSource(final CommandSource source) {
        return new net.dirtcraft.discordlink.commands.sources.ConsoleSource() {
            public void sendMessage(@Nonnull final Text message) {
                source.sendMessage(message);
            }
        };
    }
}
