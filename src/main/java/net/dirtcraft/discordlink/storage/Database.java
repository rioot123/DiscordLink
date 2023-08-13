// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.storage;

import java.nio.file.Path;
import net.dirtcraft.plugin.dirtdatabaselib.DirtDatabaseLib;
import java.sql.Connection;
import net.dirtcraft.discordlink.storage.tables.Verification;

public class Database extends Verification
{
    @Override
    protected Connection getConnection() {
        return DirtDatabaseLib.getConnection("playerdata", (Path)null);
    }
}
