package net.dirtcraft.discord.discordlink.Events;

import net.dirtcraft.discord.discordlink.API.Channels;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.API.Roles;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Storage.Database;
import net.dirtcraft.discord.discordlink.Storage.tables.Mutes;
import net.dirtcraft.discord.discordlink.Storage.tables.Verification;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class DiscordJoinHandler extends ListenerAdapter {
    final Database db = DiscordLink.getInstance().getStorage();

    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        CompletableFuture.runAsync(()->checkRoles(event));
    }

    public void checkRoles(@Nonnull GuildMemberJoinEvent event){
        if (event.getUser().isBot()) return;
        Optional<GuildMember> optMember = db.getVerificationData(event.getUser().getId())
                .filter(s->s.getMinecraftUser().isPresent())
                .flatMap(Verification.VerificationData::getGuildMember);
        optMember.ifPresent(user-> Utility.setRoleIfAbsent(Channels.getGuild(), user, Roles.VERIFIED));
        Optional<Mutes.MuteData> muteData = (db.hasActiveMute(event.getUser().getIdLong()));
        if (muteData.isPresent()) Utility.setRoleIfAbsent(event.getUser().getIdLong(), Roles.MUTED);
    }

}
