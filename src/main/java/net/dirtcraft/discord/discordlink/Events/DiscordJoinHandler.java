package net.dirtcraft.discord.discordlink.Events;

import net.dirtcraft.discord.discordlink.API.GameChats;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Storage.Database;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class DiscordJoinHandler extends ListenerAdapter {
    final Database db = DiscordLink.getInstance().getStorage();

    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        if (event.getUser().isBot() || !db.isVerified(event.getUser().getIdLong())) return;
        GuildMember discord = new GuildMember(event.getMember());

        final Guild guild = GameChats.getGuild();
        final Role verifiedRole = guild.getRoleById(PluginConfiguration.Roles.verifiedRoleID);
        if (verifiedRole != null && !discord.isVerified()) guild.addRoleToMember(discord, verifiedRole).queue();
    }

}
