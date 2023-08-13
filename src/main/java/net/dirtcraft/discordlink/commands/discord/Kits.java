// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.discord;

import net.dirtcraft.discordlink.users.platform.PlatformProvider;
import net.dirtcraft.spongediscordlib.users.DiscordMember;
import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import java.util.Optional;
import java.util.function.Function;
import net.dirtcraft.spongediscordlib.users.platform.PlatformUser;
import org.spongepowered.api.entity.living.player.User;
import net.dirtcraft.spongediscordlib.exceptions.DiscordPermissionException;
import java.util.List;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.discordlink.users.permission.PermissionProvider;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;

public class Kits implements DiscordCommandExecutor
{
    private PermissionProvider provider;
    
    public void execute(final MessageSource source, final String command, final List<String> args) throws DiscordCommandException {
        if (this.provider == null) {
            this.provider = PermissionProvider.INSTANCE;
        }
        Optional<PlatformUser> player;
        if (args.isEmpty()) {
            player = (Optional<PlatformUser>)source.getPlayerData();
        }
        else {
            if (!source.isStaff()) {
                throw new DiscordPermissionException();
            }
            player = this.parseIdentifier(args.get(0));
        }
        if (!player.isPresent()) {
            final String response = args.isEmpty() ? "You are not correctly verified, or have not played on this server." : "Invalid user. Either the user does not exist or they have never played on this server.";
            source.sendCommandResponse(response, 30);
        }
        else {
            this.provider.printUserKits(source, player.map((Function<? super PlatformUser, ? extends User>)PlatformUser::getOfflinePlayer).get());
        }
    }
    
    private Optional<PlatformUser> parseIdentifier(final String s) {
        if (s.matches("<?@?!?(\\d+)>?")) {
            final long discordId = Long.parseLong(s.replaceAll("<?@?!?(\\d+)>?", "$1"));
            return DiscordLink.get().getUserManager().getMember(discordId).flatMap((Function<? super DiscordMember, ? extends Optional<? extends PlatformUser>>)DiscordMember::getPlayerData);
        }
        return PlatformProvider.getPlayerOffline(s);
    }
}
