// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.discord;

import net.dirtcraft.discordlink.users.GuildMember;
import java.util.regex.Matcher;
import net.dirtcraft.discordlink.users.UserManagerImpl;
import java.util.Optional;
import java.util.function.Function;
import net.dirtcraft.spongediscordlib.users.platform.PlatformUser;
import net.dv8tion.jda.api.entities.Member;
import net.dirtcraft.discordlink.utility.Utility;
import java.util.regex.Pattern;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import net.dirtcraft.discordlink.DiscordLink;
import java.util.List;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;

public class Username implements DiscordCommandExecutor
{
    public void execute(final MessageSource source, final String cmd, final List<String> args) throws DiscordCommandException {
        final DiscordLink discordLink = DiscordLink.get();
        final UserManagerImpl userManager = discordLink.getUserManager();
        if (args.isEmpty()) {
            throw new DiscordCommandException("Invalid Discord ID");
        }
        final String discordID = args.get(0);
        final Pattern pattern = Pattern.compile("<?@?!?(\\d+)>?");
        final Matcher matcher = pattern.matcher(discordID);
        final Optional<Member> member;
        if (!matcher.matches() || !(member = Utility.getMemberById(matcher.group(1))).isPresent()) {
            throw new DiscordCommandException("Invalid Discord ID");
        }
        final GuildMember player = userManager.getMember(member.get());
        final Optional<PlatformUser> user = player.getPlayerData();
        if (!user.isPresent()) {
            throw new DiscordCommandException("The user was not verified!");
        }
        source.sendCommandResponse("Minecraft Username:", (String)user.flatMap((Function<? super PlatformUser, ? extends Optional<? extends String>>)PlatformUser::getNameIfPresent).get());
    }
}
