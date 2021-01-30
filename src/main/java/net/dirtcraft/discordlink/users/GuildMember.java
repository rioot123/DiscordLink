package net.dirtcraft.discordlink.users;

import net.dirtcraft.discordlink.api.users.DiscordMember;
import net.dirtcraft.discordlink.storage.Database;
import net.dirtcraft.discordlink.storage.tables.Verification;
import net.dirtcraft.discordlink.users.discord.Roles;
import net.dirtcraft.discordlink.users.discord.WrappedMember;
import net.dirtcraft.discordlink.users.platform.PlatformPlayerImpl;
import net.dirtcraft.discordlink.users.platform.PlatformUserImpl;
import net.dirtcraft.discordlink.users.platform.PlatformProvider;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;

public class GuildMember extends WrappedMember implements DiscordMember {
    PlatformUserImpl user;
    boolean retrievedPlayer;
    private final Database storage;
    private List<Roles> roles;
    private Roles highestRank;

    public GuildMember(Database storage, Member member){
        super(member);
        this.storage = storage;
        Collection<Role> discordRoles = member.getRoles();
        roles = new ArrayList<>();
        highestRank = Arrays.stream(Roles.values())
                .filter(e->discordRoles.contains(e.getRole()))
                .map(Roles::ordinal)
                .reduce(Integer::min)
                .map(integer -> Roles.values()[integer])
                .orElse(Roles.NONE);

        Arrays.stream(Roles.values())
                .filter(e->(e.isStaff() && highestRank.ordinal() <= e.ordinal()) || (discordRoles.contains(e.getRole())))
                .forEach(roles::add);
    }

    public Optional<PlatformPlayerImpl> getPlayer(){
        if (!retrievedPlayer) return getPlayerData().flatMap(PlatformProvider::getPlayer);
        else return Optional.ofNullable(user).flatMap(PlatformProvider::getPlayer);
    }

    public Optional<PlatformUserImpl> getPlayerData(){
        if (!retrievedPlayer) {
            final Optional<PlatformUserImpl> optData = storage.getVerificationData(getId())
                    .flatMap(Verification.VerificationData::getUUID)
                    .flatMap(PlatformProvider::getPlayerOffline);
            retrievedPlayer = true;
            return optData;
        } else return Optional.ofNullable(user);
    }

    public void sendMessage(MessageEmbed embed) {
        member.getUser().openPrivateChannel().queue(dm-> dm.sendMessage(embed).queue());
    }

    public void sendMessage(String message) {
        member.getUser().openPrivateChannel().queue(dm-> dm.sendMessage(message).queue());
    }

    public boolean isStaff(){
        return roles.contains(Roles.STAFF);
    }

    public boolean isMuted(){
        return roles.contains(Roles.MUTED);
    }

    public boolean isDonor(){
        return roles.contains(Roles.DONOR);
    }

    public boolean isBoosting(){
        return roles.contains(Roles.NITRO);
    }

    public boolean isVerified(){
        return roles.contains(Roles.VERIFIED);
    }

    public boolean hasRole(Roles role){
        return roles.contains(role);
    }

    public String getChevron(){
        return highestRank.getChevron();
    }

    public String getNameStyle(){
        return highestRank.getStyle();
    }

    @NonNull public Roles getHighestRank(){
        return highestRank;
    }
}
