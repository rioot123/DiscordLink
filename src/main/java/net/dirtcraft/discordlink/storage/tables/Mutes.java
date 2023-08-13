// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.storage.tables;

import java.util.ArrayList;
import java.util.List;
import net.dirtcraft.discordlink.utility.Utility;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRoles;
import java.sql.ResultSet;
import java.util.Optional;
import java.time.Instant;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Function;
import net.dirtcraft.spongediscordlib.users.platform.PlatformUser;
import java.sql.Timestamp;
import net.dirtcraft.spongediscordlib.users.DiscordMember;
import java.sql.Connection;
import java.util.UUID;

public abstract class Mutes extends Votes
{
    private final UUID DEFAULT;
    
    public Mutes() {
        this.DEFAULT = new UUID(0L, 0L);
    }
    
    @Override
    protected abstract Connection getConnection();
    
    public void registerMute(final UUID staff, final DiscordMember subject, final Timestamp expires, final String reason) {
        try (final Connection connection = this.getConnection();
             final PreparedStatement ps = connection.prepareStatement("INSERT INTO discordmutedata (submitterMinecraft, subjectDiscord, subjectMinecraft, expires, reason) VALUES (?, ?, ?, ?, ?)")) {
            final String subjectUuid = subject.getPlayerData().map(PlatformUser::getUUID).map((Function<? super Object, ? extends String>)UUID::toString).orElse(null);
            ps.setString(1, (staff == null) ? this.DEFAULT.toString() : staff.toString());
            ps.setLong(2, subject.getIdLong());
            ps.setString(3, subjectUuid);
            ps.setTimestamp(4, expires);
            ps.setString(5, reason);
            ps.execute();
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
    
    public void registerMute(final long staff, final DiscordMember subject, final Timestamp expires, final String reason) {
        try (final Connection connection = this.getConnection();
             final PreparedStatement ps = connection.prepareStatement("INSERT INTO discordmutedata (submitterDiscord, subjectDiscord, subjectMinecraft, expires, reason) VALUES (?, ?, ?, ?, ?)")) {
            final String subjectUuid = subject.getPlayerData().map(PlatformUser::getUUID).map((Function<? super Object, ? extends String>)UUID::toString).orElse(null);
            ps.setLong(1, staff);
            ps.setLong(2, subject.getIdLong());
            ps.setString(3, subjectUuid);
            ps.setTimestamp(4, expires);
            ps.setString(5, reason);
            ps.execute();
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
    
    public void deactivateMute(final UUID staff, final long subject) {
        try (final Connection connection = this.getConnection();
             final PreparedStatement ps = connection.prepareStatement("UPDATE discordmutedata SET active = ?, removedByMinecraft = ?, removed = ? WHERE subjectDiscord = ? AND active = ?")) {
            ps.setBoolean(1, false);
            ps.setString(2, (staff == null) ? this.DEFAULT.toString() : staff.toString());
            ps.setTimestamp(3, Timestamp.from(Instant.now()));
            ps.setLong(4, subject);
            ps.setBoolean(5, true);
            ps.executeUpdate();
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
    
    public void deactivateMute(final long staff, final long subject) {
        try (final Connection connection = this.getConnection();
             final PreparedStatement ps = connection.prepareStatement("UPDATE discordmutedata SET active = ?, removedByDiscord = ?, removed = ? WHERE subjectDiscord = ? AND active = ?")) {
            ps.setBoolean(1, false);
            ps.setLong(2, staff);
            ps.setTimestamp(3, Timestamp.from(Instant.now()));
            ps.setLong(4, subject);
            ps.setBoolean(5, true);
            ps.executeUpdate();
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
    
    public Optional<MuteData> hasActiveMute(final long discordId) {
        try (final Connection connection = this.getConnection();
             final PreparedStatement ps = connection.prepareStatement("SELECT * FROM discordmutedata WHERE subjectDiscord = ? AND active = ?")) {
            ps.setLong(1, discordId);
            ps.setBoolean(2, true);
            try (final ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    final long id = rs.getLong("id");
                    final long submitterDiscord = rs.getLong("submitterDiscord");
                    final String submitterMinecraft = rs.getString("submitterMinecraft");
                    final long subjectDiscord = rs.getLong("subjectDiscord");
                    final String subjectMinecraft = rs.getString("subjectMinecraft");
                    final long removedByDiscord = rs.getLong("removedByDiscord");
                    final String removedByMinecraft = rs.getString("removedByMinecraft");
                    final Timestamp submitted = rs.getTimestamp("submitted");
                    final Timestamp removed = rs.getTimestamp("removed");
                    final Timestamp expires = rs.getTimestamp("expires");
                    final String reason = rs.getString("reason");
                    final boolean active = rs.getBoolean("active");
                    final MuteData data = new MuteData(id, submitterDiscord, submitterMinecraft, subjectDiscord, subjectMinecraft, removedByDiscord, removedByMinecraft, submitted, removed, expires, reason, active);
                    return Optional.of(data);
                }
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        return Optional.empty();
    }
    
    public void deactivateExpiredMutes() {
        this.getActiveMutes().stream().filter(MuteData::expired).peek(e -> Utility.removeRoleIfPresent(e.subjectDiscord, DiscordRoles.MUTED)).forEach(e -> this.setMuteExpired(e.id));
    }
    
    public List<MuteData> getActiveMutes() {
        final List<MuteData> entries = new ArrayList<MuteData>();
        try (final Connection connection = this.getConnection();
             final PreparedStatement ps = connection.prepareStatement("SELECT * FROM discordmutedata WHERE active = ?")) {
            ps.setBoolean(1, true);
            try (final ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final long id = rs.getLong("id");
                    final long submitterDiscord = rs.getLong("submitterDiscord");
                    final String submitterMinecraft = rs.getString("submitterMinecraft");
                    final long subjectDiscord = rs.getLong("subjectDiscord");
                    final String subjectMinecraft = rs.getString("subjectMinecraft");
                    final long removedByDiscord = rs.getLong("removedByDiscord");
                    final String removedByMinecraft = rs.getString("removedByMinecraft");
                    final Timestamp submitted = rs.getTimestamp("submitted");
                    final Timestamp removed = rs.getTimestamp("removed");
                    final Timestamp expires = rs.getTimestamp("expires");
                    final String reason = rs.getString("reason");
                    final boolean active = rs.getBoolean("active");
                    final MuteData data = new MuteData(id, submitterDiscord, submitterMinecraft, subjectDiscord, subjectMinecraft, removedByDiscord, removedByMinecraft, submitted, removed, expires, reason, active);
                    entries.add(data);
                }
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        return entries;
    }
    
    public void setMuteExpired(final long id) {
        try (final Connection connection = this.getConnection();
             final PreparedStatement ps = connection.prepareStatement("UPDATE discordmutedata SET active = ? WHERE id = ?")) {
            ps.setBoolean(1, false);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void buildMuteTable() {
        final String statment = "CREATE TABLE IF NOT EXISTS `discordmutedata` (\n\t`id` BIGINT(20) NOT NULL AUTO_INCREMENT,\n\t`submitterDiscord` BIGINT(20) NULL DEFAULT NULL,\n\t`submitterMinecraft` CHAR(36) NULL DEFAULT NULL COLLATE 'latin1_swedish_ci',\n\t`subjectDiscord` BIGINT(20) NOT NULL,\n\t`subjectMinecraft` CHAR(36) NULL DEFAULT NULL COLLATE 'latin1_swedish_ci',\n\t`removedByDiscord` BIGINT(20) NULL DEFAULT NULL,\n\t`removedByMinecraft` CHAR(36) NULL DEFAULT NULL COLLATE 'latin1_swedish_ci',\n\t`submitted` TIMESTAMP NOT NULL DEFAULT current_timestamp(),\n\t`removed` TIMESTAMP NULL DEFAULT NULL,\n\t`expires` TIMESTAMP NULL DEFAULT NULL,\n\t`reason` VARCHAR(2000) NOT NULL COLLATE 'latin1_swedish_ci',\n\t`active` BIT(1) NOT NULL DEFAULT b'1',\n\tINDEX `id` (`id`) USING BTREE\n);";
        try (final Connection connection = this.getConnection();
             final PreparedStatement ps = connection.prepareStatement(statment)) {
            ps.execute();
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
    
    public static class MuteData
    {
        private final long id;
        private final long submitterDiscord;
        private final UUID submitterMinecraft;
        private final long subjectDiscord;
        private final UUID subjectMinecraft;
        private final long removedByDiscord;
        private final UUID removedByMinecraft;
        private final Timestamp submitted;
        private final Timestamp removed;
        private final Timestamp expires;
        private final String reason;
        private final boolean active;
        
        MuteData(final long id, final long submitterDiscord, final String submitterMinecraft, final long subjectDiscord, final String subjectMinecraft, final long removedByDiscord, final String removedByMinecraft, final Timestamp submitted, final Timestamp removed, final Timestamp expires, final String reason, final boolean active) {
            this.id = id;
            this.submitterDiscord = submitterDiscord;
            this.submitterMinecraft = ((submitterMinecraft == null) ? null : UUID.fromString(submitterMinecraft));
            this.subjectDiscord = subjectDiscord;
            this.subjectMinecraft = ((subjectMinecraft == null) ? null : UUID.fromString(subjectMinecraft));
            this.removedByDiscord = removedByDiscord;
            this.removedByMinecraft = ((removedByMinecraft == null) ? null : UUID.fromString(removedByMinecraft));
            this.submitted = submitted;
            this.removed = removed;
            this.expires = expires;
            this.reason = reason;
            this.active = active;
        }
        
        public long getId() {
            return this.id;
        }
        
        public boolean isActive() {
            return this.active;
        }
        
        public boolean expired() {
            if (this.expires == null) {
                return false;
            }
            final Timestamp now = Timestamp.from(Instant.now());
            return now.after(this.expires);
        }
        
        public String getReason() {
            return this.reason;
        }
        
        public long getTarget() {
            return this.subjectDiscord;
        }
        
        public Optional<Long> getSubmitter() {
            return Optional.ofNullable((this.submitterDiscord > 0L) ? Long.valueOf(this.submitterDiscord) : null);
        }
        
        public Optional<Long> getRemovedBy() {
            return Optional.ofNullable((this.removedByDiscord > 0L) ? Long.valueOf(this.removedByDiscord) : null);
        }
        
        public Optional<UUID> getTargetUUID() {
            return Optional.ofNullable(this.subjectMinecraft);
        }
        
        public Optional<UUID> getSubmitterUUID() {
            return Optional.ofNullable(this.submitterMinecraft);
        }
        
        public Optional<UUID> getRemovedUUID() {
            return Optional.ofNullable(this.removedByMinecraft);
        }
        
        public Optional<Timestamp> getRemoved() {
            return Optional.ofNullable(this.removed);
        }
        
        public Optional<Timestamp> getExpires() {
            return Optional.ofNullable(this.expires);
        }
        
        public Timestamp getSubmitted() {
            return this.submitted;
        }
    }
}
