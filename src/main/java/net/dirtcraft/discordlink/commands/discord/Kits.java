package net.dirtcraft.discordlink.commands.discord;

import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.discordlink.users.GuildMember;
import net.dirtcraft.discordlink.users.MessageSource;
import net.dirtcraft.discordlink.api.commands.DiscordCommandExecutor;
import net.dirtcraft.discordlink.api.exceptions.DiscordCommandException;
import net.dirtcraft.discordlink.api.exceptions.DiscordPermissionException;
import net.dirtcraft.discordlink.users.permission.PermissionProvider;
import net.dirtcraft.discordlink.users.platform.PlatformUserImpl;
import net.dirtcraft.discordlink.users.platform.PlatformProvider;

import java.util.List;
import java.util.Optional;

public class Kits implements DiscordCommandExecutor {
    private PermissionProvider provider;

    @Override
    public void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException {
        if (provider == null) {
            provider = PermissionProvider.INSTANCE;
        }

        Optional<PlatformUserImpl> player;

        if (args.isEmpty()){
            player = source.getPlayerData();
        } else {
            if (!source.isStaff()) throw new DiscordPermissionException();
            player = parseIdentifier(args.get(0));
        }

        if (!player.isPresent()) {
            String response = args.isEmpty()? "You are not correctly verified, or have not played on this server." : "Invalid user. Either the user does not exist or they have never played on this server.";
            source.sendCommandResponse(response, 30);
        } else provider.printUserKits(source, player.map(PlatformUserImpl::getUser).get());
    }

    private Optional<PlatformUserImpl> parseIdentifier(String s){
        if (s.matches("<?@?!?(\\d+)>?")){
            long discordId = Long.parseLong(s.replaceAll("<?@?!?(\\d+)>?", "$1"));
            return DiscordLink.get()
                    .getUserManager()
                    .getMember(discordId)
                    .flatMap(GuildMember::getPlayerData);
        } else {
            return PlatformProvider.getPlayerOffline(s);
        }
    }
}
