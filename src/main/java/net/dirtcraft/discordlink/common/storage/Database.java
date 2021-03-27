package net.dirtcraft.discordlink.common.storage;

import net.dirtcraft.dirtdatabaselib.DirtDatabaseLib;
import net.dirtcraft.discordlink.common.storage.tables.Verification;
import net.dirtcraft.discordlink.forge.platform.PlatformProvider;

import java.sql.Connection;

public class Database extends Verification {

    public Database(PlatformProvider provider){
        super(provider);
    }

    protected Connection getConnection() {
        return DirtDatabaseLib.getConnection("playerdata", null);
    }

}
