// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.users.discord;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Emote;
import java.awt.Color;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.ClientType;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import java.util.List;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import javax.annotation.Nullable;
import java.time.OffsetDateTime;
import net.dv8tion.jda.api.JDA;
import java.util.Collection;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.Permission;
import java.util.EnumSet;
import net.dv8tion.jda.api.entities.Guild;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Member;

public class WrappedMember implements Member
{
    protected final Member member;
    
    public WrappedMember(final Member member) {
        this.member = member;
    }
    
    @Nonnull
    public User getUser() {
        return this.member.getUser();
    }
    
    @Nonnull
    public Guild getGuild() {
        return this.member.getGuild();
    }
    
    @Nonnull
    public EnumSet<Permission> getPermissions() {
        return (EnumSet<Permission>)this.member.getPermissions();
    }
    
    @Nonnull
    public EnumSet<Permission> getPermissions(@Nonnull final GuildChannel channel) {
        return (EnumSet<Permission>)this.member.getPermissions(channel);
    }
    
    @Nonnull
    public EnumSet<Permission> getPermissionsExplicit() {
        return (EnumSet<Permission>)this.member.getPermissionsExplicit();
    }
    
    @Nonnull
    public EnumSet<Permission> getPermissionsExplicit(@Nonnull final GuildChannel channel) {
        return (EnumSet<Permission>)this.member.getPermissionsExplicit(channel);
    }
    
    public boolean hasPermission(@Nonnull final Permission... permissions) {
        return this.member.hasPermission(permissions);
    }
    
    public boolean hasPermission(@Nonnull final Collection<Permission> permissions) {
        return this.member.hasPermission((Collection)permissions);
    }
    
    public boolean hasPermission(@Nonnull final GuildChannel channel, @Nonnull final Permission... permissions) {
        return this.member.hasPermission(channel, permissions);
    }
    
    public boolean hasPermission(@Nonnull final GuildChannel channel, @Nonnull final Collection<Permission> permissions) {
        return this.member.hasPermission(channel, (Collection)permissions);
    }
    
    public JDA getJDA() {
        return this.member.getJDA();
    }
    
    @Nonnull
    public OffsetDateTime getTimeJoined() {
        return this.member.getTimeJoined();
    }
    
    public boolean hasTimeJoined() {
        return this.member.hasTimeJoined();
    }
    
    @Nullable
    public OffsetDateTime getTimeBoosted() {
        return this.member.getTimeBoosted();
    }
    
    public GuildVoiceState getVoiceState() {
        return this.member.getVoiceState();
    }
    
    @Nonnull
    public List<Activity> getActivities() {
        return (List<Activity>)this.member.getActivities();
    }
    
    @Nonnull
    public OnlineStatus getOnlineStatus() {
        return this.member.getOnlineStatus();
    }
    
    @Nonnull
    public OnlineStatus getOnlineStatus(@Nonnull final ClientType type) {
        return this.member.getOnlineStatus(type);
    }
    
    @Nonnull
    public EnumSet<ClientType> getActiveClients() {
        return (EnumSet<ClientType>)this.member.getActiveClients();
    }
    
    public String getNickname() {
        return this.member.getNickname();
    }
    
    @Nonnull
    public String getEffectiveName() {
        return this.member.getEffectiveName();
    }
    
    @Nonnull
    public List<Role> getRoles() {
        return (List<Role>)this.member.getRoles();
    }
    
    public Color getColor() {
        return this.member.getColor();
    }
    
    public int getColorRaw() {
        return this.member.getColorRaw();
    }
    
    public boolean canInteract(@Nonnull final Member member) {
        return this.member.canInteract(member);
    }
    
    public boolean canInteract(@Nonnull final Role role) {
        return this.member.canInteract(role);
    }
    
    public boolean canInteract(@Nonnull final Emote emote) {
        return this.member.canInteract(emote);
    }
    
    @Nullable
    public TextChannel getDefaultChannel() {
        return this.member.getDefaultChannel();
    }
    
    @Nonnull
    public String getAsMention() {
        return this.member.getAsMention();
    }
    
    public boolean isOwner() {
        return this.member.isOwner();
    }
    
    public boolean isFake() {
        return this.member.isFake();
    }
    
    public long getIdLong() {
        return this.member.getIdLong();
    }
}
