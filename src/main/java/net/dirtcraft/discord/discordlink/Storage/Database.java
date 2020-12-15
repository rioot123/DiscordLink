package net.dirtcraft.discord.discordlink.Storage;

import net.dirtcraft.discord.dirtdatabaselib.DirtDatabaseLib;
import net.dirtcraft.discord.discordlink.Storage.tables.Mutes;
import net.dirtcraft.discord.discordlink.Storage.tables.Verification;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class Database extends Verification {

    protected Connection getConnection() {
        return DirtDatabaseLib.getConnection("playerdata", null);
    }

}