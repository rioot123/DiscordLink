package net.dirtcraft.discord.discordlink.Database;

import net.dirtcraft.plugin.dirtdatabaselib.DirtDatabaseLib;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Storage {

    public boolean isVerified(UUID uuid) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM verification WHERE uuid = '" + uuid + "'");
             ResultSet rs = ps.executeQuery()) {

            return rs.next();

        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean validCode(String code) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM verification WHERE code = '" + code + "'");
             ResultSet rs = ps.executeQuery()) {

            if (!rs.next()) return false;

            return rs.getString("uuid") == null;

        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public void updateRecord(String code, UUID uuid) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE verification SET uuid = '" + uuid + "' WHERE code = '" + code + "'")) {

            ps.executeUpdate();

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void deleteRecord(UUID uuid) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM verification WHERE uuid = '" + uuid + "'")) {

            ps.executeUpdate();

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Nullable
    public String getDiscordUser(UUID uuid) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT discordid FROM verification WHERE uuid = '" + uuid + "'");
             ResultSet rs = ps.executeQuery()) {

            if (!rs.next()) return null;

            return rs.getString("discordid");

        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    private Connection getConnection() {
        return DirtDatabaseLib.getConnection(null, "playerdata", null);
    }
}
