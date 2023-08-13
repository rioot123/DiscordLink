// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.storage.tables;

import java.util.Optional;
import java.sql.Timestamp;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ChronoUnit;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;

public abstract class Votes
{
    protected abstract Connection getConnection();
    
    @Nullable
    public String getLastKnownUsername(final String uuid) {
        try (final Connection connection = this.getConnection();
             final PreparedStatement ps = connection.prepareStatement("SELECT Username FROM votedata WHERE UUID = ?")) {
            ps.setString(1, uuid);
            final ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                return null;
            }
            final String username = rs.getString("Username");
            rs.close();
            return username;
        }
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }
    
    public void updateUsername(final UUID uuid, final String username) {
        try (final Connection connection = this.getConnection();
             final PreparedStatement ps = connection.prepareStatement("UPDATE votedata SET Username = ?, Last_Vote = ? WHERE UUID = ?")) {
            ps.setString(1, username);
            ps.setTimestamp(2, Timestamp.from(Instant.now().minus(1L, (TemporalUnit)ChronoUnit.DAYS)));
            ps.setString(3, uuid.toString());
            ps.executeUpdate();
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
    
    public void createVoteRecord(final UUID uuid, final String username) {
        try (final Connection connection = this.getConnection();
             final PreparedStatement ps = connection.prepareStatement("INSERT INTO votedata (UUID, Username, Last_Vote) VALUES (?, ?, ?)")) {
            ps.setString(1, uuid.toString());
            ps.setString(2, username);
            ps.setTimestamp(3, Timestamp.from(Instant.now().minus(1L, (TemporalUnit)ChronoUnit.DAYS)));
            ps.execute();
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
    
    public Optional<VoterData> getPlayerVoteData(final UUID uuid) {
        try (final Connection connection = this.getConnection();
             final PreparedStatement ps = connection.prepareStatement("SELECT * FROM votedata WHERE UUID = ?")) {
            ps.setString(1, uuid.toString());
            final ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return Optional.empty();
            }
            final int votes = rs.getInt("Votes");
            final Timestamp lastVote = rs.getTimestamp("Last_Vote");
            final String username = rs.getString("Username");
            rs.close();
            return Optional.of(new VoterData(uuid, votes, username, lastVote));
        }
        catch (SQLException exception) {
            exception.printStackTrace();
            return Optional.empty();
        }
    }
    
    public static class VoterData
    {
        private final Timestamp lastVote;
        private final String username;
        
        private VoterData(final UUID uuid, final int votes, final String username, final Timestamp lastVote) {
            this.lastVote = lastVote;
            this.username = username;
        }
        
        public String getUsername() {
            if (this.username == null || this.username.trim().isEmpty()) {
                return null;
            }
            return this.username;
        }
        
        public boolean hasVotedInPastWeek() {
            return this.lastVote != null && this.lastVote.toInstant().isAfter(Instant.now().minus(8L, (TemporalUnit)ChronoUnit.DAYS));
        }
    }
}
