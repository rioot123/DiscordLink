package net.dirtcraft.discordlink.users;

import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.discordlink.api.users.UserManager;
import net.dirtcraft.discordlink.channels.ChannelManagerImpl;
import net.dirtcraft.discordlink.channels.DiscordChannelImpl;
import net.dirtcraft.discordlink.storage.Database;
import net.dirtcraft.discordlink.storage.tables.Verification;
import net.dirtcraft.discordlink.users.discord.RoleManagerImpl;
import net.dirtcraft.discordlink.users.platform.PlatformPlayerImpl;
import net.dirtcraft.discordlink.users.platform.PlatformProvider;
import net.dirtcraft.discordlink.users.platform.PlatformUserImpl;
import net.dirtcraft.discordlink.utility.Utility;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserManagerImpl implements UserManager {
    private final ChannelManagerImpl channelManager;
    private final RoleManagerImpl roleManager;
    private final Database storage;

    public UserManagerImpl(ChannelManagerImpl channelManager, RoleManagerImpl roleManager, Database storage){
        this.channelManager = channelManager;
        this.roleManager = roleManager;
        this.storage = storage;
    }

    @Override
    public Optional<PlatformUserImpl> getUser(String s){
        if (s.matches("<?@?!?(\\d+)>?")){
            long discordId = Long.parseLong(s.replaceAll("<?@?!?(\\d+)>?", "$1"));
            return Optional.ofNullable(channelManager.getGuild().getMemberById(discordId))
                    .map(m->new GuildMember(storage, roleManager, m))
                    .flatMap(GuildMember::getPlayerData);
        } else {
            return PlatformProvider.getPlayerOffline(s);
        }
    }

    @Override
    public Optional<PlatformUserImpl> getUser(UUID uuid){
        return PlatformProvider.getPlayerOffline(uuid);
    }

    @Override
    public Optional<GuildMember> getMember(long id){
        return Optional.ofNullable(DiscordLink.get().getChannelManager().getGuild().getMemberById(id))
                .map(m->new GuildMember(storage, roleManager, m));
    }

    @Override
    public Optional<GuildMember> getMember(UUID player){
        final Optional<GuildMember> profile =  storage.getVerificationData(player)
                .flatMap(Verification.VerificationData::getDiscordId)
                .flatMap(Utility::getMemberById)
                .map(member->new GuildMember(storage, roleManager, member));
        profile.ifPresent(member-> {
            member.retrievedPlayer = true;
            member.user = PlatformProvider.getPlayerOffline(player)
                    .orElse(null);
        });
        return profile;
    }

    @Override
    public Optional<GuildMember> getMember(String s){
        if (s.matches("<?@?!?(\\d+)>?")){
            long discordId = Long.parseLong(s.replaceAll("<?@?!?(\\d+)>?", "$1"));
            return Optional.ofNullable(channelManager.getGuild().getMemberById(discordId))
                    .map(m->new GuildMember(storage, roleManager, m));
        } else {
            return PlatformProvider.getPlayerOffline(s)
                    .map(PlatformUserImpl::getUUID)
                    .flatMap(storage::getVerificationData)
                    .flatMap(Verification.VerificationData::getGuildMember);
        }
    }

    @Override
    public List<PlatformPlayerImpl> getPlayers(){
        return PlatformProvider.getPlayers();
    }

    public MessageSource getMember(MessageReceivedEvent event){
        boolean isPrivate = event.getMessage().isFromType(ChannelType.PRIVATE);
        long channelId = event.getChannel().getIdLong();
        DiscordChannelImpl channel = channelManager.getChannel(channelId, isPrivate);
        return new MessageSource(storage, event.getMember(), channel, roleManager, event);
    }

    public GuildMember getMember(Member member){
        return new GuildMember(storage, roleManager, member);
    }
}
