// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.users;

import net.dv8tion.jda.api.entities.Guild;
import net.dirtcraft.discordlink.commands.sources.DiscordResponder;
import net.dirtcraft.discordlink.commands.sources.ConsoleSource;
import net.dirtcraft.discordlink.DiscordLink;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dirtcraft.discordlink.storage.tables.Verification;
import net.dirtcraft.discordlink.users.platform.PlatformProvider;
import net.dirtcraft.spongediscordlib.users.platform.PlatformPlayer;
import java.util.Optional;
import net.dirtcraft.discordlink.users.permission.PermissionProvider;
import net.dv8tion.jda.api.entities.Role;
import java.util.Collection;
import java.util.function.Consumer;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRoles;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.HashSet;
import net.dv8tion.jda.api.entities.Member;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRole;
import java.util.Set;
import net.dirtcraft.discordlink.storage.Database;
import net.dirtcraft.discordlink.channels.DiscordChannelImpl;
import net.dirtcraft.spongediscordlib.users.roles.RoleManager;
import net.dirtcraft.discordlink.users.permission.subject.PermissionResolver;
import net.dirtcraft.spongediscordlib.users.platform.PlatformUser;
import net.dirtcraft.spongediscordlib.users.DiscordMember;
import net.dirtcraft.discordlink.users.discord.WrappedMember;

public class GuildMember extends WrappedMember implements DiscordMember
{
    PlatformUser user;
    PermissionResolver permissions;
    boolean retrievedPlayer;
    boolean retrievedPermissions;
    private final RoleManager roleManager;
    private DiscordChannelImpl privateChannel;
    private final Database storage;
    private Set<DiscordRole> roles;
    private DiscordRole highestRank;
    
    public GuildMember(final Database storage, final RoleManager roleManager, final Member member) {
        super(member);
        this.roleManager = roleManager;
        this.storage = storage;
        final Collection<Role> discordRoles = (Collection<Role>)member.getRoles();
        this.roles = new HashSet<DiscordRole>();
        this.highestRank = (DiscordRole)roleManager.getRoles().stream().filter(e -> discordRoles.contains(e.getRole())).map(DiscordRole::ordinal).reduce(Integer::min).map(roleManager::getRole).orElse(DiscordRoles.NONE);
        final Collection collection;
        roleManager.getRoles().stream().filter(e -> (e.isStaff() && this.highestRank.getStaffLevel() >= e.getStaffLevel()) || collection.contains(e.getRole())).forEach(this.roles::add);
    }
    
    public boolean hasInGamePermission(final String permission) {
        if (this.hasRole(DiscordRoles.DIRTY)) {
            return true;
        }
        if (!this.retrievedPlayer) {
            this.getPlayerData();
        }
        if (this.user == null) {
            return false;
        }
        if (!this.retrievedPermissions) {
            this.permissions = PermissionProvider.INSTANCE.getPermission(this.user.getUUID()).orElse(null);
        }
        if (this.permissions == null) {
            return false;
        }
        final boolean perm = this.permissions.hasPermission(permission);
        return perm;
    }
    
    public Optional<PlatformPlayer> getPlayer() {
        if (!this.retrievedPlayer) {
            return this.getPlayerData().flatMap((Function<? super PlatformUser, ? extends Optional<? extends PlatformPlayer>>)PlatformProvider::getPlayer);
        }
        return Optional.ofNullable(this.user).flatMap((Function<? super PlatformUser, ? extends Optional<? extends PlatformPlayer>>)PlatformProvider::getPlayer);
    }
    
    public Optional<PlatformUser> getPlayerData() {
        if (!this.retrievedPlayer) {
            final Optional<PlatformUser> optData = this.storage.getVerificationData(this.getId()).flatMap((Function<? super Verification.VerificationData, ? extends Optional<?>>)Verification.VerificationData::getUUID).flatMap((Function<? super Object, ? extends Optional<? extends PlatformUser>>)PlatformProvider::getPlayerOffline);
            optData.ifPresent(u -> this.user = u);
            this.retrievedPlayer = true;
            return optData;
        }
        return Optional.ofNullable(this.user);
    }
    
    public void sendPrivateMessage(final MessageEmbed embed) {
        this.member.getUser().openPrivateChannel().queue(dm -> dm.sendMessage(embed).queue());
    }
    
    public void sendPrivateMessage(final String message) {
        this.member.getUser().openPrivateChannel().queue(dm -> dm.sendMessage((CharSequence)message).queue());
    }
    
    public DiscordChannelImpl getPrivateChannel() {
        if (this.privateChannel == null) {
            final PrivateChannel channel = (PrivateChannel)this.getUser().openPrivateChannel().complete();
            this.privateChannel = DiscordLink.get().getChannelManager().getChannel(channel.getIdLong(), true);
        }
        return this.privateChannel;
    }
    
    public ConsoleSource getPrivateSource(final String command) {
        final DiscordResponder responder = this.getPrivateChannel().getCommandResponder(this, command);
        return DiscordResponder.getSender(responder);
    }
    
    public boolean isStaff() {
        return this.roles.contains(DiscordRoles.STAFF);
    }
    
    public boolean isMuted() {
        return this.roles.contains(DiscordRoles.MUTED);
    }
    
    public boolean isDonor() {
        return this.roles.contains(DiscordRoles.DONOR);
    }
    
    public boolean isBoosting() {
        return this.roles.contains(DiscordRoles.NITRO);
    }
    
    public boolean isVerified() {
        return this.roles.contains(DiscordRoles.VERIFIED);
    }
    
    public boolean hasRole(final DiscordRole role) {
        return this.roles.contains(role);
    }
    
    public String getChevron() {
        return this.highestRank.getChevron();
    }
    
    public String getNameStyle() {
        return this.highestRank.getStyle();
    }
    
    public DiscordRole getHighestRank() {
        return this.highestRank;
    }
    
    public void setRoleIfAbsent(final DiscordRole role) {
        try {
            final Guild guild = this.getGuild();
            final Role discordRole = role.getRole();
            if (discordRole == null || this.hasRole(role)) {
                return;
            }
            guild.addRoleToMember(this.member, discordRole).submit();
        }
        catch (Exception ex) {}
    }
    
    public void removeRoleIfPresent(final DiscordRole role) {
        try {
            final Guild guild = this.getGuild();
            final Role discordRole = role.getRole();
            if (discordRole == null || !this.hasRole(role)) {
                return;
            }
            guild.removeRoleFromMember(this.member, discordRole).submit();
        }
        catch (Exception ex) {}
    }
    
    public void tryChangeNickname(final String name) {
        try {
            if (name == null) {
                return;
            }
            this.getGuild().modifyNickname((Member)this, name).submit();
        }
        catch (Exception ex) {}
    }
    
    public void reloadRoles() {
        final Collection<Role> discordRoles = (Collection<Role>)this.member.getRoles();
        this.roles = new HashSet<DiscordRole>();
        this.highestRank = (DiscordRole)this.roleManager.getRoles().stream().filter(e -> discordRoles.contains(e.getRole())).map(DiscordRole::ordinal).reduce(Integer::min).map(this.roleManager::getRole).orElse(DiscordRoles.NONE);
        final Collection collection;
        this.roleManager.getRoles().stream().filter(e -> (e.isStaff() && this.highestRank.getStaffLevel() <= e.getStaffLevel()) || collection.contains(e.getRole())).forEach(this.roles::add);
    }
    
    Member getWrappedMember() {
        return this.member;
    }
}
