// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.storage.tables;

import java.util.function.Consumer;
import net.dirtcraft.discordlink.users.UserManagerImpl;
import net.dirtcraft.spongediscordlib.users.DiscordMember;
import net.dirtcraft.discordlink.utility.Utility;
import net.dv8tion.jda.api.entities.Member;
import net.dirtcraft.discordlink.users.platform.PlatformProvider;
import net.dirtcraft.spongediscordlib.users.platform.PlatformUser;
import net.dv8tion.jda.api.JDA;
import java.util.concurrent.CompletableFuture;
import net.dv8tion.jda.api.requests.RestAction;
import java.util.function.Function;
import net.dirtcraft.discordlink.DiscordLink;
import net.dv8tion.jda.api.entities.User;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.UUID;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class Verification extends Mutes
{
    public void createRecord(final String discordID, final String code) {
        try (final Connection connection = this.getConnection();
             final PreparedStatement ps = connection.prepareStatement("INSERT INTO verification (discordid, code) VALUES (?, ?)")) {
            ps.setString(1, discordID);
            ps.setString(2, code);
            ps.execute();
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
    
    public void updateRecord(final String code, final UUID uuid) {
        try (final Connection connection = this.getConnection();
             final PreparedStatement ps = connection.prepareStatement("UPDATE verification SET uuid = ?, code = NULL WHERE code = ?")) {
            ps.setString(1, uuid.toString());
            ps.setString(2, code);
            ps.executeUpdate();
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
    
    public void deleteRecord(final UUID uuid) {
        try (final Connection connection = this.getConnection();
             final PreparedStatement ps = connection.prepareStatement("DELETE FROM verification WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
    
    public void deleteRecord(final String discordId) {
        try (final Connection connection = this.getConnection();
             final PreparedStatement ps = connection.prepareStatement("DELETE FROM verification WHERE discordid = ?")) {
            ps.setString(1, discordId);
            ps.executeUpdate();
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
    
    public Optional<VerificationData> getPendingData(final String code) {
        try (final Connection connection = this.getConnection();
             final PreparedStatement ps = connection.prepareStatement("SELECT * FROM verification WHERE code = ?")) {
            ps.setString(1, code);
            try (final ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                final String uuid = rs.getString("uuid");
                final String discordId = rs.getString("discordid");
                return Optional.of(new VerificationData(uuid, discordId, code));
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
            return Optional.empty();
        }
    }
    
    public Optional<VerificationData> getVerificationData(final String discordID) {
        try (final Connection connection = this.getConnection();
             final PreparedStatement ps = connection.prepareStatement("SELECT * FROM verification WHERE discordid = ?")) {
            ps.setString(1, discordID);
            try (final ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                final String code = rs.getString("code");
                final String uuid = rs.getString("uuid");
                final String discordId = rs.getString("discordid");
                return Optional.of(new VerificationData(uuid, discordId, code));
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
            return Optional.empty();
        }
    }
    
    public Optional<VerificationData> getVerificationData(final UUID id) {
        try (final Connection connection = this.getConnection();
             final PreparedStatement ps = connection.prepareStatement("SELECT * FROM verification WHERE uuid = ?")) {
            ps.setString(1, id.toString());
            try (final ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                final String code = rs.getString("code");
                final String uuid = rs.getString("uuid");
                final String discordId = rs.getString("discordid");
                return Optional.of(new VerificationData(uuid, discordId, code));
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
            return Optional.empty();
        }
    }
    
    public class VerificationData
    {
        final UUID uuid;
        final Long discordId;
        final String code;
        
        VerificationData(final String uuid, final String discord, final String code) {
            this.discordId = ((discord == null) ? null : Long.valueOf(Long.parseLong(discord)));
            this.uuid = ((uuid == null) ? null : UUID.fromString(uuid));
            this.code = code;
        }
        
        public Optional<UUID> getUUID() {
            return Optional.ofNullable(this.uuid);
        }
        
        public Optional<Long> getDiscordId() {
            return Optional.ofNullable(this.discordId);
        }
        
        public Optional<String> getCode() {
            return Optional.ofNullable(this.code);
        }
        
        public Optional<User> getDiscordUser() {
            final DiscordLink discordLink = DiscordLink.get();
            final JDA jda = discordLink.getJda();
            return this.getDiscordId().map((Function<? super Long, ?>)jda::retrieveUserById).map((Function<? super Object, ?>)RestAction::submit).map((Function<? super Object, ? extends User>)CompletableFuture::join);
        }
        
        public Optional<PlatformUser> getMinecraftUser() {
            return this.getUUID().flatMap((Function<? super UUID, ? extends Optional<? extends PlatformUser>>)PlatformProvider::getPlayerOffline);
        }
        
        public Optional<Member> getMember() {
            return this.getDiscordId().flatMap((Function<? super Long, ? extends Optional<? extends Member>>)Utility::getMemberById);
        }
        
        public Optional<DiscordMember> getGuildMember() {
            final DiscordLink discordLink = DiscordLink.get();
            final UserManagerImpl userManager = discordLink.getUserManager();
            return this.getMember().map((Function<? super Member, ? extends DiscordMember>)userManager::getMember);
        }
        
        public Optional<String> getName() {
            final Optional<String> name = this.getMinecraftUser().flatMap((Function<? super PlatformUser, ? extends Optional<? extends String>>)PlatformUser::getNameIfPresent);
            if (name.isPresent()) {
                return name;
            }
            return this.getUUID().map((Function<? super UUID, ?>)UUID::toString).map((Function<? super Object, ? extends String>)Verification.this::getLastKnownUsername);
        }
        
        public void deleteRecord() {
            if (this.getUUID().isPresent()) {
                this.getUUID().ifPresent(Verification.this::deleteRecord);
            }
            else {
                this.getDiscordId().map((Function<? super Long, ?>)String::valueOf).ifPresent((Consumer<? super Object>)Verification.this::deleteRecord);
            }
        }
    }
}
