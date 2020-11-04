package net.dirtcraft.discord.discordlink.Storage;

import net.dirtcraft.discord.dirtdatabaselib.DirtDatabaseLib;

import javax.annotation.Nullable;
import java.sql.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

public class Database {

    private Connection getConnection() {
        return DirtDatabaseLib.getConnection("playerdata", null);
    }

    public boolean isVerified(long discordId) {
        ResultSet rs = null;
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM verification WHERE discordid = ? AND uuid IS NOT NULL")) {
            ps.setString(1, String.valueOf(discordId));
            rs = ps.executeQuery();

            return rs.next() && rs.getString("uuid").length() == 36;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return  false;
        } finally {
            if (rs != null) {
                try {rs.close();} catch (SQLException ignored) {}
            }
        }
    }

    public boolean isVerified(UUID uuid) {
        boolean result;
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM verification WHERE uuid = ?")) {

            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();

            result = rs.next();

            rs.close();

        } catch (SQLException exception) {
            exception.printStackTrace();
            result = false;
        }

        return result;
    }

    public boolean validCode(String code) {
        boolean result;
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM verification WHERE code = ?")) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                rs.close();
                return false;
            } else {
                result = rs.getString("uuid") == null;
                rs.close();
            }

            return result;

        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public void updateRecord(String code, UUID uuid) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE verification SET uuid = ?, code = NULL WHERE code = ?")) {
            ps.setString(1, uuid.toString());
            ps.setString(2, code);

            ps.executeUpdate();

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void deleteRecord(UUID uuid) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM verification WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());

            ps.executeUpdate();

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void deleteRecord(String discordId) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM verification WHERE discordid = ?")) {
            ps.setString(1, discordId);

            ps.executeUpdate();

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Nullable
    public String getDiscordUser(UUID uuid) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT discordid FROM verification WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                rs.close();
                return null;
            } else {
                String discordID = rs.getString("discordid");
                rs.close();
                return discordID;
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public Optional<UUID> getUUID(String discordId) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT uuid FROM verification WHERE discordid = ?")) {

            ps.setString(1, discordId);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return Optional.empty();
            final String uuid = rs.getString("uuid");

            return Optional.of(UUID.fromString(uuid));

        } catch (SQLException exception) {
            exception.printStackTrace();
            return Optional.empty();
        }
    }

    @Nullable
    public String getUUIDfromDiscordID(String discordID) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT uuid FROM verification WHERE discordid = ?")) {

            ps.setString(1, discordID);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                rs.close();
                return null;
            } else {
                String uuid = rs.getString("uuid");
                rs.close();
                return uuid;
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public Optional<VoterData> getPlayerVoteData(UUID uuid){
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM votedata WHERE UUID = ?")) {

            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return Optional.empty();

            final int votes = rs.getInt("Votes");
            final Timestamp lastVote = rs.getTimestamp("Last_Vote");
            final String username = rs.getString("Username");

            rs.close();

            return Optional.of(new VoterData(uuid, votes, username, lastVote));

        } catch (SQLException exception) {
            exception.printStackTrace();
            return Optional.empty();
        }
    }

    public void updateUsername(UUID uuid, String username)  {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE votedata SET Username = ?, Last_Vote = ? WHERE UUID = ?")) {

            ps.setString(1, username);
            ps.setTimestamp(2, Timestamp.from(Instant.now().minus(1, ChronoUnit.DAYS)));
            ps.setString(3, uuid.toString());

            ps.executeUpdate();

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void createVoteRecord(UUID uuid, String username)  {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("INSERT INTO votedata (UUID, Username, Last_Vote) VALUES (?, ?, ?)")) {

            ps.setString(1, uuid.toString());
            ps.setString(2, username);
            ps.setTimestamp(3, Timestamp.from(Instant.now().minus(1, ChronoUnit.DAYS)));

            ps.execute();

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static class VoterData {
        private final Timestamp lastVote;
        private final String username;

        private VoterData(UUID uuid, int votes, String username, Timestamp lastVote){
            this.lastVote = lastVote;
            this.username = username;
        }

        public String getUsername(){
            if (username == null || username.trim().isEmpty()) return null;
            else return username;
        }

        public boolean hasVotedInPastWeek(){
            return lastVote != null && lastVote.toInstant().isAfter(Instant.now().minus(8, ChronoUnit.DAYS));
        }
    }
}