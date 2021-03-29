package net.dirtcraft.discordlink.common.users;

import net.dirtcraft.discordlink.forge.DiscordLink;
import net.dirtcraft.discordlink.common.channels.ChannelManagerImpl;
import net.dirtcraft.discordlink.common.channels.DiscordChannelImpl;
import net.dirtcraft.discordlink.common.storage.Database;
import net.dirtcraft.discordlink.common.storage.tables.Verification;
import net.dirtcraft.discordlink.common.users.discord.RoleManagerImpl;
import net.dirtcraft.discordlink.forge.platform.PlatformProvider;
import net.dirtcraft.discordlink.common.utility.Utility;
import net.dirtcraft.discordlink.api.users.DiscordMember;
import net.dirtcraft.discordlink.api.users.UserManager;
import net.dirtcraft.discordlink.api.users.platform.PlatformPlayer;
import net.dirtcraft.discordlink.api.users.platform.PlatformUser;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;

public class UserManagerImpl implements UserManager {
    private static final String UUID_REGEX = "(?i)[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
    private static final String UID_REGEX = "<?@?!?(\\d+)>?";
    private static final long CACHE_DURATION = 1000 * 60 * 15;
    private final List<CachedMember> userCache;
    private final PlatformProvider platformProvider;
    private final ChannelManagerImpl channelManager;
    private final RoleManagerImpl roleManager;
    private final Database storage;

    public UserManagerImpl(ChannelManagerImpl channelManager, RoleManagerImpl roleManager, Database storage, PlatformProvider provider){
        this.userCache = new ArrayList<>();
        this.channelManager = channelManager;
        this.platformProvider = provider;
        this.roleManager = roleManager;
        this.storage = storage;
    }

    @Override
    public Optional<DiscordMember> getMember(long id){
        ListIterator<CachedMember> memberCache = userCache.listIterator();
        while (memberCache.hasNext()){
            CachedMember cached = memberCache.next();
            if (cached.matches(id)) return Optional.of(cached.getMember());
            else if (cached.expired()) memberCache.remove();
        }
        Optional<GuildMember> optMember = Optional.ofNullable(DiscordLink.get().getChannelManager().getGuild().getMemberById(id))
                .map(m->new GuildMember(storage, roleManager, platformProvider, m));
        optMember.ifPresent(m->userCache.add(new CachedMember(m)));
        return optMember.map(DiscordMember.class::cast);
    }

    @Override
    public Optional<DiscordMember> getMember(UUID player){
        ListIterator<CachedMember> memberCache = userCache.listIterator();
        while (memberCache.hasNext()){
            CachedMember cached = memberCache.next();
            if (cached.matches(player)) return Optional.of(cached.getMember());
            else if (cached.expired()) memberCache.remove();
        }
        final Optional<GuildMember> profile =  storage.getVerificationData(player)
                .flatMap(Verification.VerificationData::getDiscordId)
                .flatMap(Utility::getMemberById)
                .map(member->new GuildMember(storage, roleManager, platformProvider, member));
        profile.ifPresent(m-> {
            userCache.add(new CachedMember(m));
            m.retrievedPlayer = true;
            m.user = platformProvider.getPlayerOffline(player)
                    .orElse(null);
        });
        return profile.map(DiscordMember.class::cast);
    }

    private Optional<DiscordMember> getMemberByIgn(String s){
        return platformProvider.getPlayerOffline(s, false)
                .map(PlatformUser::getUUID).flatMap(this::getMember);
    }

    @Override
    public Optional<DiscordMember> getMember(String s){
        if (s.matches(UID_REGEX)) {
            long discordId = Long.parseLong(s.replaceAll(UID_REGEX, "$1"));
            return getMember(discordId);
        } else if (s.matches(UUID_REGEX)) {
            UUID uuid = UUID.fromString(s);
            return getMember(uuid);
        } else {
            return getMemberByIgn(s);
        }
    }

    @Override
    public Optional<PlatformUser> getUser(String s){
        if (s.matches(UID_REGEX)){
            long discordId = Long.parseLong(s.replaceAll(UID_REGEX, "$1"));
            return Optional.ofNullable(channelManager.getGuild().getMemberById(discordId))
                    .map(m->new GuildMember(storage, roleManager, platformProvider, m))
                    .flatMap(GuildMember::getPlayerData);
        } else {
            return platformProvider.getPlayerOffline(s);
        }
    }

    @Override
    public Optional<PlatformUser> getUser(UUID uuid){
        return platformProvider.getPlayerOffline(uuid);
    }

    @Override
    public List<PlatformPlayer> getPlayers(){
        return platformProvider.getPlayers();
    }

    public MessageSourceImpl getMember(MessageReceivedEvent event){
        boolean isPrivate = event.getMessage().isFromType(ChannelType.PRIVATE);
        long channelId = event.getChannel().getIdLong();
        long memberId = event.getAuthor().getIdLong();
        DiscordChannelImpl channel = channelManager.getChannel(channelId, isPrivate);

        MessageSourceImpl newSource = null;
        ListIterator<CachedMember> memberCache = userCache.listIterator();
        while (memberCache.hasNext()){
            CachedMember cached = memberCache.next();
            if (cached.matches(memberId)) {
                GuildMember previous = cached.getMember();
                newSource = new MessageSourceImpl(storage, platformProvider, previous.getWrappedMember(), channel, roleManager, event);
                if (previous.user != null){
                    newSource.user = previous.user;
                    newSource.retrievedPlayer = previous.retrievedPlayer;
                }
                if (previous.permissions != null){
                    newSource.permissions = previous.permissions;
                    newSource.retrievedPermissions = previous.retrievedPermissions;
                }
                cached.member = newSource;
            } else if (cached.expired()) memberCache.remove();
        }
        if (newSource != null) return newSource;
        Member member = channelManager.getGuild().retrieveMember(event.getAuthor()).complete();
        MessageSourceImpl source = new MessageSourceImpl(storage, platformProvider, member, channel, roleManager, event);
        userCache.add(new CachedMember(source));
        return source;
    }

    public GuildMember getMember(Member member){
        return new GuildMember(storage, roleManager, platformProvider, member);
    }

    private static class CachedMember {
        private long lastAccessed = System.currentTimeMillis();
        private UUID uuid;
        private long discordId;
        private GuildMember member;

        private CachedMember(GuildMember member){
            this.discordId = member.getIdLong();
            this.member = member;
        }

        private boolean matches(UUID uuid){
            return this.uuid != null && this.uuid.equals(uuid);
        }

        private boolean matches(long discordId){
            return this.discordId > 0 && this.discordId == discordId;
        }

        private GuildMember getMember(){
            lastAccessed = System.currentTimeMillis();
            if (member.user != null) uuid = member.user.getUUID();
            return member;
        }

        private boolean expired(){
            return System.currentTimeMillis() - lastAccessed < CACHE_DURATION;
        }
    }
}
