package net.dirtcraft.discordlink.common.storage;

import net.dirtcraft.dirtdatabaselib.DirtDatabaseLib;
import net.dirtcraft.discordlink.common.storage.tables.Verification;

import java.sql.Connection;

public class Database extends Verification {

    protected Connection getConnection() {
        return DirtDatabaseLib.getConnection("playerdata", null);
    }

}
