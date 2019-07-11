package net.dirtcraft.discord.discordlink.Database;

import net.dirtcraft.discord.discordlink.Data.PunishmentType;
import net.dirtcraft.plugin.dirtdatabaselib.DirtDatabaseLib;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Storage {

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

    @Nullable
    public String getLastKnownUsername(String uuid) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT Username FROM votedata WHERE UUID = ?")) {

            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                rs.close();
                return null;
            } else {
                String username = rs.getString("Username");
                rs.close();
                return username;
            }


        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
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

    public PunishmentType uuidIsPunished(String uuid) {
        try (Connection connection = getPunishementConnection();
             PreparedStatement banPS = connection.prepareStatement("SELECT uuid FROM litebans_bans WHERE uuid = ? AND active = TRUE");
            PreparedStatement mutePS = connection.prepareStatement("SELECT uuid FROM litebans_mutes WHERE uuid = ? AND active = TRUE")) {

            banPS.setString(1, uuid);
            ResultSet banRS = banPS.executeQuery();
            if (banRS.next()) {
                banRS.close();
                return PunishmentType.BANNED;
            }

            mutePS.setString(1, uuid);

            ResultSet muteRS = mutePS.executeQuery();
            if (muteRS.next()) {
                muteRS.close();
                return PunishmentType.MUTED;
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return PunishmentType.NONE;
    }

    private Connection getPunishementConnection() {
        return DirtDatabaseLib.getConnection("bans", null);
    }

    private Connection getConnection() {
        return DirtDatabaseLib.getConnection("playerdata", null);
    }
}
