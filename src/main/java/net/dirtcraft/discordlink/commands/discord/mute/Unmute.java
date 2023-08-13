// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.discord.mute;

import net.dirtcraft.discordlink.users.UserManagerImpl;
import net.dirtcraft.discordlink.storage.Database;
import net.dirtcraft.discordlink.storage.tables.Mutes;
import net.dirtcraft.discordlink.utility.Utility;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRoles;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import java.util.function.Function;
import net.dirtcraft.spongediscordlib.users.DiscordMember;
import net.dirtcraft.discordlink.DiscordLink;
import java.util.List;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;

public class Unmute implements DiscordCommandExecutor
{
    public void execute(final MessageSource source, final String command, final List<String> args) throws DiscordCommandException {
        final DiscordLink discordLink = DiscordLink.get();
        final Database database = discordLink.getStorage();
        final UserManagerImpl userManager = discordLink.getUserManager();
        final DiscordMember target = this.removeIfPresent((List)args, (Function)userManager::getMember).orElseThrow(() -> new DiscordCommandException("Discord user not specified."));
        Utility.removeRoleIfPresent(target.getIdLong(), DiscordRoles.MUTED);
        final Mutes.MuteData data = database.hasActiveMute(target.getIdLong()).orElseThrow(() -> new DiscordCommandException("The user was never muted."));
        database.deactivateMute(source.getIdLong(), target.getIdLong());
        source.sendCommandResponse(source.getAsMention() + " The following mute has been removed", 30);
        source.sendCommandResponse(MuteInfo.getInfo(data), 30);
    }
}
