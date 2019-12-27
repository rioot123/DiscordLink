package net.dirtcraft.discord.discordlink.API;

import net.dirtcraft.discord.discordlink.Commands.DiscordCommand;
import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.user.UserStorageService;

import javax.annotation.Nullable;
import java.awt.*;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DiscordSource implements Member {
    private final Member member;
    private staffRoles staffRank;
    private boolean verified;
    private boolean donor;
    private boolean nitro;

    public static Optional<DiscordSource> fromPlayer(Player player){
        String memberID =  DiscordLink.getInstance().getStorage().getDiscordUser(player.getUniqueId());
        if (memberID == null) return Optional.empty();
        final Guild guild = DiscordLink.getGuild();
        final DiscordSource profile = new DiscordSource(guild.getMemberById(memberID));
        return Optional.of(profile);
    }

    public static Optional<DiscordSource> fromPlayer(org.spongepowered.api.entity.living.player.User player){
        String memberID =  DiscordLink.getInstance().getStorage().getDiscordUser(player.getUniqueId());
        if (memberID == null) return Optional.empty();
        final Guild guild = DiscordLink.getGuild();
        final DiscordSource profile = new DiscordSource(guild.getMemberById(memberID));
        return Optional.of(profile);
    }

    public static Optional<DiscordSource> fromPlayerId(UUID player){
        String memberID =  DiscordLink.getInstance().getStorage().getDiscordUser(player);
        if (memberID == null) return Optional.empty();
        final Guild guild = DiscordLink.getGuild();
        final DiscordSource profile = new DiscordSource(guild.getMemberById(memberID));
        return Optional.of(profile);
    }

    public DiscordSource(Member member){
        this.member = member;
        this.staffRank = staffRoles.NONE;
        member.getRoles().forEach(role -> {
            JDA jda = member.getJDA();
            for (staffRoles staffRank : staffRoles.values()){
                if (staffRank != staffRoles.NONE && member.getRoles().contains(jda.getRoleById(staffRank.getID())) && this.staffRank.ordinal() > staffRank.ordinal()) this.staffRank = staffRank;
            }
            if (member.getRoles().contains(jda.getRoleById(PluginConfiguration.Roles.verifiedRoleID))) verified = true;
            if (member.getRoles().contains(jda.getRoleById(PluginConfiguration.Roles.donatorRoleID))) donor = true;
            if (member.getRoles().contains(jda.getRoleById(PluginConfiguration.Roles.nitroRoleID))) nitro = true;
        });
    }

    public Optional<Player> getPlayer(){
        final String playerId = DiscordLink.getInstance().getStorage().getUUIDfromDiscordID(member.getUser().getId());
        if (playerId == null) return Optional.empty();
        final UUID uuid = UUID.fromString(playerId);
        return Sponge.getServer().getPlayer(uuid);
    }

    public Optional<org.spongepowered.api.entity.living.player.User> getSpongeUser(){
        final UserStorageService userStorage = Sponge.getGame().getServiceManager().provideUnchecked(UserStorageService.class);
        final String playerId = DiscordLink.getInstance().getStorage().getUUIDfromDiscordID(member.getUser().getId());
        if (playerId == null) return Optional.empty();

        final UUID uuid = UUID.fromString(playerId);
        return userStorage.get(uuid);
    }

    public boolean isOwner() {
        return staffRank.ordinal() <= staffRoles.OWNER.ordinal();
    }

    public boolean isDirty() {
        return staffRank.ordinal() <= staffRoles.DIRTY.ordinal();
    }

    public boolean isAdmin() {
        return staffRank.ordinal() <= staffRoles.ADMIN.ordinal();
    }

    public boolean isStaff() {
        return staffRank.ordinal() <= staffRoles.STAFF.ordinal();
    }

    public boolean isVerified() {
        return verified;
    }

    public boolean isDonor() {
        return donor;
    }

    public boolean isNitro() {
        return nitro;
    }

    public void sendMessage(MessageEmbed embed) {
        member.getUser().openPrivateChannel().queue(dm-> dm.sendMessage(embed).queue());
    }

    public void sendMessage(String message) {
        member.getUser().openPrivateChannel().queue(dm-> dm.sendMessage(message).queue());
    }

    public boolean hasPermission(DiscordRoles role){
        switch (role){
            case OWNER: return isOwner();
            case DIRTY: return isDirty();
            case ADMIN: return isAdmin();
            case STAFF: return isStaff();
            case NITRO: return isNitro();
            case DONOR: return isDonor();
            case VERIFIED: return isVerified();
            default: return true;
        }
    }

    public boolean hasPermission(DiscordCommand command){
        return command.hasPermission(this);
    }

    @Override
    public User getUser() {
        return member.getUser();
    }

    @Override
    public Guild getGuild() {
        return member.getGuild();
    }

    @Override
    public List<Permission> getPermissions() {
        return member.getPermissions();
    }

    @Override
    public boolean hasPermission(Permission... permissions) {
        return member.hasPermission(permissions);
    }

    @Override
    public boolean hasPermission(Collection<Permission> permissions) {
        return member.hasPermission(permissions);
    }

    @Override
    public boolean hasPermission(Channel channel, Permission... permissions) {
        return member.hasPermission(channel, permissions);
    }

    @Override
    public boolean hasPermission(Channel channel, Collection<Permission> permissions) {
        return member.hasPermission(channel, permissions);
    }

    @Override
    public JDA getJDA() {
        return member.getJDA();
    }

    @Override
    public OffsetDateTime getJoinDate() {
        return member.getJoinDate();
    }

    @Override
    public GuildVoiceState getVoiceState() {
        return member.getVoiceState();
    }

    @Override
    public Game getGame() {
        return member.getGame();
    }

    @Override
    public OnlineStatus getOnlineStatus() {
        return member.getOnlineStatus();
    }

    @Override
    public String getNickname() {
        return member.getNickname();
    }

    @Override
    public String getEffectiveName() {
        return member.getEffectiveName();
    }

    @Override
    public List<Role> getRoles() {
        return member.getRoles();
    }

    @Override
    public Color getColor() {
        return member.getColor();
    }

    @Override
    public int getColorRaw() {
        return member.getColorRaw();
    }

    @Override
    public List<Permission> getPermissions(Channel channel) {
        return member.getPermissions(channel);
    }

    @Override
    public boolean canInteract(Member member) {
        return member.canInteract(member);
    }

    @Override
    public boolean canInteract(Role role) {
        return member.canInteract(role);
    }

    @Override
    public boolean canInteract(Emote emote) {
        return member.canInteract(emote);
    }

    @Nullable
    @Override
    public TextChannel getDefaultChannel() {
        return member.getDefaultChannel();
    }

    @Override
    public String getAsMention() {
        return member.getAsMention();
    }

    private enum staffRoles {
        OWNER,
        DIRTY,
        ADMIN,
        STAFF,
        NONE;

        public String getID(){
            switch (this) {
                case OWNER: return PluginConfiguration.Roles.ownerRoleID;
                case DIRTY: return PluginConfiguration.Roles.dirtyRoleID;
                case ADMIN: return PluginConfiguration.Roles.adminRoleID;
                case STAFF: return PluginConfiguration.Roles.staffRoleID;
                default: return null;
            }
        }
    }

}
