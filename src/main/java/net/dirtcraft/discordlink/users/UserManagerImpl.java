// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.users;

import net.dirtcraft.discordlink.channels.DiscordChannelImpl;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dirtcraft.spongediscordlib.users.platform.PlatformPlayer;
import net.dirtcraft.discordlink.users.platform.PlatformProvider;
import net.dirtcraft.spongediscordlib.users.platform.PlatformUser;
import net.dirtcraft.discordlink.utility.Utility;
import net.dirtcraft.discordlink.storage.tables.Verification;
import java.util.UUID;
import java.util.ListIterator;
import java.util.function.Function;
import net.dirtcraft.spongediscordlib.users.roles.RoleManager;
import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.spongediscordlib.users.DiscordMember;
import java.util.Optional;
import java.util.ArrayList;
import net.dirtcraft.discordlink.storage.Database;
import net.dirtcraft.discordlink.users.discord.RoleManagerImpl;
import net.dirtcraft.discordlink.channels.ChannelManagerImpl;
import java.util.List;
import net.dirtcraft.spongediscordlib.users.UserManager;

public class UserManagerImpl implements UserManager
{
    private static final String UUID_REGEX = "(?i)[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
    private static final String UID_REGEX = "<?@?!?(\\d+)>?";
    private static final long CACHE_DURATION = 900000L;
    private final List<CachedMember> userCache;
    private final ChannelManagerImpl channelManager;
    private final RoleManagerImpl roleManager;
    private final Database storage;
    
    public UserManagerImpl(final ChannelManagerImpl channelManager, final RoleManagerImpl roleManager, final Database storage) {
        this.userCache = new ArrayList<CachedMember>();
        this.channelManager = channelManager;
        this.roleManager = roleManager;
        this.storage = storage;
    }
    
    public Optional<DiscordMember> getMember(final long id) {
        final ListIterator<CachedMember> memberCache = this.userCache.listIterator();
        while (memberCache.hasNext()) {
            final CachedMember cached = memberCache.next();
            if (cached.matches(id)) {
                return Optional.of((DiscordMember)cached.getMember());
            }
            if (!cached.expired()) {
                continue;
            }
            memberCache.remove();
        }
        final Optional<GuildMember> optMember = Optional.ofNullable(DiscordLink.get().getChannelManager().getGuild().getMemberById(id)).map(m -> new GuildMember(this.storage, this.roleManager, m));
        optMember.ifPresent(m -> this.userCache.add(new CachedMember(m)));
        return optMember.map((Function<? super GuildMember, ? extends DiscordMember>)DiscordMember.class::cast);
    }
    
    public Optional<DiscordMember> getMember(final UUID player) {
        final ListIterator<CachedMember> memberCache = this.userCache.listIterator();
        while (memberCache.hasNext()) {
            final CachedMember cached = memberCache.next();
            if (cached.matches(player)) {
                return Optional.of((DiscordMember)cached.getMember());
            }
            if (!cached.expired()) {
                continue;
            }
            memberCache.remove();
        }
        final Optional<GuildMember> profile = this.storage.getVerificationData(player).flatMap((Function<? super Verification.VerificationData, ? extends Optional<?>>)Verification.VerificationData::getDiscordId).flatMap((Function<? super Object, ? extends Optional<?>>)Utility::getMemberById).map(member -> new GuildMember(this.storage, this.roleManager, member));
        profile.ifPresent(m -> {
            this.userCache.add(new CachedMember(m));
            m.retrievedPlayer = true;
            m.user = PlatformProvider.getPlayerOffline(player).orElse(null);
            return;
        });
        return profile.map((Function<? super GuildMember, ? extends DiscordMember>)DiscordMember.class::cast);
    }
    
    private Optional<DiscordMember> getMemberByIgn(final String s) {
        return PlatformProvider.getPlayerOffline(s, false).map((Function<? super PlatformUser, ?>)PlatformUser::getUUID).flatMap((Function<? super Object, ? extends Optional<? extends DiscordMember>>)this::getMember);
    }
    
    public Optional<DiscordMember> getMember(final String s) {
        if (s.matches("<?@?!?(\\d+)>?")) {
            final long discordId = Long.parseLong(s.replaceAll("<?@?!?(\\d+)>?", "$1"));
            return this.getMember(discordId);
        }
        if (s.matches("(?i)[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")) {
            final UUID uuid = UUID.fromString(s);
            return this.getMember(uuid);
        }
        return this.getMemberByIgn(s);
    }
    
    public Optional<PlatformUser> getUser(final String s) {
        if (s.matches("<?@?!?(\\d+)>?")) {
            final long discordId = Long.parseLong(s.replaceAll("<?@?!?(\\d+)>?", "$1"));
            return Optional.ofNullable(this.channelManager.getGuild().getMemberById(discordId)).map(m -> new GuildMember(this.storage, this.roleManager, m)).flatMap((Function<? super Object, ? extends Optional<? extends PlatformUser>>)GuildMember::getPlayerData);
        }
        return PlatformProvider.getPlayerOffline(s);
    }
    
    public Optional<PlatformUser> getUser(final UUID uuid) {
        return PlatformProvider.getPlayerOffline(uuid);
    }
    
    public List<PlatformPlayer> getPlayers() {
        return PlatformProvider.getPlayers();
    }
    
    public MessageSourceImpl getMember(final MessageReceivedEvent event) {
        final boolean isPrivate = event.getMessage().isFromType(ChannelType.PRIVATE);
        final long channelId = event.getChannel().getIdLong();
        final long memberId = event.getAuthor().getIdLong();
        final DiscordChannelImpl channel = this.channelManager.getChannel(channelId, isPrivate);
        MessageSourceImpl newSource = null;
        final ListIterator<CachedMember> memberCache = this.userCache.listIterator();
        while (memberCache.hasNext()) {
            final CachedMember cached = memberCache.next();
            if (cached.matches(memberId)) {
                final GuildMember previous = cached.getMember();
                newSource = new MessageSourceImpl(this.storage, previous.getWrappedMember(), channel, this.roleManager, event);
                if (previous.user != null) {
                    newSource.user = previous.user;
                    newSource.retrievedPlayer = previous.retrievedPlayer;
                }
                if (previous.permissions != null) {
                    newSource.permissions = previous.permissions;
                    newSource.retrievedPermissions = previous.retrievedPermissions;
                }
                cached.member = newSource;
            }
            else {
                if (!cached.expired()) {
                    continue;
                }
                memberCache.remove();
            }
        }
        if (newSource != null) {
            return newSource;
        }
        final Member member = (Member)this.channelManager.getGuild().retrieveMember(event.getAuthor()).complete();
        final MessageSourceImpl source = new MessageSourceImpl(this.storage, member, channel, this.roleManager, event);
        this.userCache.add(new CachedMember((GuildMember)source));
        return source;
    }
    
    public GuildMember getMember(final Member member) {
        return new GuildMember(this.storage, this.roleManager, member);
    }
    
    private static class CachedMember
    {
        private long lastAccessed;
        private UUID uuid;
        private long discordId;
        private GuildMember member;
        
        private CachedMember(final GuildMember member) {
            this.lastAccessed = System.currentTimeMillis();
            this.discordId = member.getIdLong();
            this.member = member;
        }
        
        private boolean matches(final UUID uuid) {
            return this.uuid != null && this.uuid.equals(uuid);
        }
        
        private boolean matches(final long discordId) {
            return this.discordId > 0L && this.discordId == discordId;
        }
        
        private GuildMember getMember() {
            this.lastAccessed = System.currentTimeMillis();
            if (this.member.user != null) {
                this.uuid = this.member.user.getUUID();
            }
            return this.member;
        }
        
        private boolean expired() {
            return System.currentTimeMillis() - this.lastAccessed < 900000L;
        }
    }
}
