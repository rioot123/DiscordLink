// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.discord;

import net.dirtcraft.discordlink.users.GuildMember;
import java.util.regex.Matcher;
import net.dv8tion.jda.api.entities.Role;
import net.dirtcraft.discordlink.users.UserManagerImpl;
import net.dirtcraft.discordlink.storage.Database;
import net.dv8tion.jda.api.entities.Guild;
import java.util.UUID;
import net.dirtcraft.discordlink.storage.tables.Verification;
import net.dirtcraft.discordlink.utility.Utility;
import java.util.regex.Pattern;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRoles;
import java.util.Optional;
import java.util.function.Function;
import net.dirtcraft.spongediscordlib.users.platform.PlatformUser;
import net.dv8tion.jda.api.entities.Member;
import net.dirtcraft.discordlink.storage.PluginConfiguration;
import net.dirtcraft.discordlink.DiscordLink;
import java.util.List;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;

public class Unlink implements DiscordCommandExecutor
{
    public void execute(final MessageSource source, final String command, final List<String> args) throws DiscordCommandException {
        final DiscordLink discordLink = DiscordLink.get();
        final Guild guild = discordLink.getChannelManager().getGuild();
        final Database storage = discordLink.getStorage();
        final UserManagerImpl userManager = discordLink.getUserManager();
        if (args.isEmpty()) {
            storage.deleteRecord(source.getUser().getId());
            final Role verifiedRole = guild.getRoleById(PluginConfiguration.Roles.verifiedRoleID);
            final Role donorRole = guild.getRoleById(PluginConfiguration.Roles.donatorRoleID);
            if (verifiedRole != null && source.getRoles().contains(verifiedRole)) {
                guild.removeRoleFromMember((Member)source, verifiedRole).queue();
            }
            if (donorRole != null && source.getRoles().contains(donorRole)) {
                guild.removeRoleFromMember((Member)source, donorRole).queue();
            }
            source.sendCommandResponse("Successfully executed command:", "Successfully unlinked " + source.getPlayerData().flatMap(PlatformUser::getNameIfPresent).orElse("your account") + ".");
            return;
        }
        if (!source.hasRole(DiscordRoles.ADMIN)) {
            throw new DiscordCommandException("You do not have permission to use this command on other users.");
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
        String response;
        if (user.isPresent()) {
            response = user.flatMap((Function<? super PlatformUser, ? extends Optional<? extends String>>)PlatformUser::getNameIfPresent).get();
        }
        else {
            response = storage.getLastKnownUsername(matcher.group(1));
            if (response == null) {
                response = storage.getVerificationData(matcher.group(1)).flatMap((Function<? super Verification.VerificationData, ? extends Optional<?>>)Verification.VerificationData::getUUID).map((Function<? super Object, ? extends String>)UUID::toString).orElseThrow(() -> new DiscordCommandException("The user was not verified!"));
            }
        }
        storage.deleteRecord(matcher.group(1));
        final Role verifiedRole2 = guild.getRoleById(PluginConfiguration.Roles.verifiedRoleID);
        final Role donorRole2 = guild.getRoleById(PluginConfiguration.Roles.donatorRoleID);
        if (verifiedRole2 != null && player.getRoles().contains(verifiedRole2)) {
            guild.removeRoleFromMember((Member)player, verifiedRole2).queue();
        }
        if (donorRole2 != null && player.getRoles().contains(donorRole2)) {
            guild.removeRoleFromMember((Member)player, donorRole2).queue();
        }
        source.sendCommandResponse("Discord-Link Verification:", "Successfully unlinked " + response + ".");
    }
}
