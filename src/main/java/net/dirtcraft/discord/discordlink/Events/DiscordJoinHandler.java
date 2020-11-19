package net.dirtcraft.discord.discordlink.Events;

import net.dirtcraft.discord.discordlink.API.Channels;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.API.Roles;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Storage.Database;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class DiscordJoinHandler extends ListenerAdapter {
    final Database db = DiscordLink.getInstance().getStorage();

    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        if (event.getUser().isBot()) return;
        db.getVerificationData(event.getUser().getId())
                .filter(s->s.getMinecraftUser().isPresent())
                .flatMap(Database.VerificationData::getGuildMember)
                .ifPresent(user-> Utility.setRoleIfAbsent(Channels.getGuild(), user, Roles.VERIFIED));
    }

}
