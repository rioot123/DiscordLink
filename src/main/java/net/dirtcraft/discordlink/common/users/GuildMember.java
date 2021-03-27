package net.dirtcraft.discordlink.common.users;

import net.dirtcraft.discordlink.forge.DiscordLink;
import net.dirtcraft.discordlink.common.channels.DiscordChannelImpl;
import net.dirtcraft.discordlink.common.commands.sources.ConsoleSource;
import net.dirtcraft.discordlink.common.commands.sources.DiscordResponder;
import net.dirtcraft.discordlink.common.storage.Database;
import net.dirtcraft.discordlink.common.storage.tables.Verification;
import net.dirtcraft.discordlink.common.users.discord.WrappedMember;
import net.dirtcraft.discordlink.common.users.permission.PermissionProvider;
import net.dirtcraft.discordlink.common.users.permission.subject.PermissionResolver;
import net.dirtcraft.discordlink.forge.platform.PlatformProvider;
import net.dirtcraft.discordlink.api.users.DiscordMember;
import net.dirtcraft.discordlink.api.users.platform.PlatformPlayer;
import net.dirtcraft.discordlink.api.users.platform.PlatformUser;
import net.dirtcraft.discordlink.api.users.roles.DiscordRole;
import net.dirtcraft.discordlink.api.users.roles.DiscordRoles;
import net.dirtcraft.discordlink.api.users.roles.RoleManager;
import net.dv8tion.jda.api.entities.*;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class GuildMember extends WrappedMember implements DiscordMember {
    PlatformUser user;
    PermissionResolver permissions;
    boolean retrievedPlayer;
    boolean retrievedPermissions;
    private PlatformProvider platformProvider;
    private final RoleManager roleManager;
    private DiscordChannelImpl privateChannel;
    private final Database storage;
    private Set<DiscordRole> roles;
    private DiscordRole highestRank;

    public GuildMember(Database storage, RoleManager roleManager, PlatformProvider provider, Member member){
        super(member);
        this.roleManager = roleManager;
        this.platformProvider = provider;
        this.storage = storage;
        Collection<Role> discordRoles = member.getRoles();
        roles = new HashSet<>();
        highestRank = roleManager.getRoles().stream()
                .filter(e->discordRoles.contains(e.getRole()))
                .map(DiscordRole::ordinal)
                .reduce(Integer::min)
                .map(roleManager::getRole)
                .orElse(DiscordRoles.NONE);

        roleManager.getRoles().stream()
                .filter(e->(e.isStaff() && highestRank.getStaffLevel() >= e.getStaffLevel()) || (discordRoles.contains(e.getRole())))
                .forEach(roles::add);
    }

    public boolean hasInGamePermission(String permission){
        if (hasRole(DiscordRoles.DIRTY)) return true;
        if (!retrievedPlayer) getPlayerData();
        if (user == null) return false;
        if (!retrievedPermissions) permissions = PermissionProvider.INSTANCE
                .getPermission(user.getUUID())
                .orElse(null);
        if (permissions == null) return false;
        boolean perm =  permissions.hasPermission(permission);
        return perm;
    }

    @Override
    public Optional<PlatformPlayer> getPlayer(){
        if (!retrievedPlayer) return getPlayerData().flatMap(platformProvider::getPlayer);
        else return Optional.ofNullable(user).flatMap(platformProvider::getPlayer);
    }

    @Override
    public Optional<PlatformUser> getPlayerData(){
        if (!retrievedPlayer) {
            final Optional<PlatformUser> optData = storage.getVerificationData(getId())
                    .flatMap(Verification.VerificationData::getUUID)
                    .flatMap(platformProvider::getPlayerOffline);
            optData.ifPresent(u->this.user = u);
            retrievedPlayer = true;
            return optData;
        } else return Optional.ofNullable(user);
    }

    @Override
    public void sendPrivateMessage(MessageEmbed embed) {
        member.getUser().openPrivateChannel().queue(dm-> dm.sendMessage(embed).queue());
    }

    @Override
    public void sendPrivateMessage(String message) {
        member.getUser().openPrivateChannel().queue(dm-> dm.sendMessage(message).queue());
    }

    public DiscordChannelImpl getPrivateChannel() {
        if (privateChannel == null) {
            PrivateChannel channel = getUser().openPrivateChannel().complete();
            privateChannel = DiscordLink.get().getChannelManager()
                    .getChannel(channel.getIdLong(), true);
        }
        return privateChannel;
    }

    public ConsoleSource getPrivateSource(String command) {
        DiscordResponder responder = getPrivateChannel().getCommandResponder(this, command);
        return DiscordResponder.getSender(responder);
    }

    @Override
    public boolean isStaff(){
        return roles.contains(DiscordRoles.STAFF);
    }

    @Override
    public boolean isMuted(){
        return roles.contains(DiscordRoles.MUTED);
    }

    @Override
    public boolean isDonor(){
        return roles.contains(DiscordRoles.DONOR);
    }

    @Override
    public boolean isBoosting(){
        return roles.contains(DiscordRoles.NITRO);
    }

    @Override
    public boolean isVerified(){
        return roles.contains(DiscordRoles.VERIFIED);
    }

    @Override
    public boolean hasRole(DiscordRole role){
        return roles.contains(role);
    }

    @Override
    public String getChevron(){
        return highestRank.getChevron();
    }

    @Override
    public String getNameStyle(){
        return highestRank.getStyle();
    }

    @Override
    @NonNull public DiscordRole getHighestRank(){
        return highestRank;
    }

    @Override
    public void setRoleIfAbsent(DiscordRole role){
        try {
            Guild guild = getGuild();
            Role discordRole = role.getRole();
            if (discordRole == null || hasRole(role)) return;
            guild.addRoleToMember(member, discordRole).submit();
        } catch (Exception ignored){}
    }

    @Override
    public void removeRoleIfPresent(DiscordRole role){
        try {
            Guild guild = getGuild();
            Role discordRole = role.getRole();
            if (discordRole == null || !hasRole(role)) return;
            guild.removeRoleFromMember(member, discordRole).submit();
        } catch (Exception ignored){}
    }

    @Override
    public void tryChangeNickname(String name){
        try {
            if (name == null) return;
            getGuild().modifyNickname(this, name).submit();
        } catch (Exception ignored){ }
    }

    @Override
    public void reloadRoles(){
        Collection<Role> discordRoles = member.getRoles();
        roles = new HashSet<>();
        highestRank = roleManager.getRoles().stream()
                .filter(e->discordRoles.contains(e.getRole()))
                .map(DiscordRole::ordinal)
                .reduce(Integer::min)
                .map(roleManager::getRole)
                .orElse(DiscordRoles.NONE);

        roleManager.getRoles().stream()
                .filter(e->(e.isStaff() && highestRank.getStaffLevel() <= e.getStaffLevel()) || (discordRoles.contains(e.getRole())))
                .forEach(roles::add);
    }

    Member getWrappedMember(){
        return member;
    }

}
