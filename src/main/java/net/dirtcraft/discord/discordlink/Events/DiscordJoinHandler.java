package net.dirtcraft.discord.discordlink.Events;

import net.dirtcraft.discord.discordlink.API.Channels;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.API.Roles;
import net.dirtcraft.discord.discordlink.Commands.Discord.Mute.MuteInfo;
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
        Optional<Mutes.MuteData> muteData = db.hasActiveMute(event.getUser().getIdLong());
        muteData.ifPresent(data->{
            Utility.setRoleIfAbsent(event.getUser().getIdLong(), Roles.MUTED);
            GuildMember member = new GuildMember(event.getMember());
            member.sendMessage("An active mute has been found linked to your account so it has been applied.\n" +
                    "You can appeal this at <#590388043379376158>");
            member.sendMessage(MuteInfo.getInfo(data));
        });
    }

}
