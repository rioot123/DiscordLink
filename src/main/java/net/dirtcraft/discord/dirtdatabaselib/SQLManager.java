package net.dirtcraft.discord.dirtdatabaselib;

import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLManager {

    private static SqlService sqlService = new SqlService();

    static {
        sqlService.buildConnectionCache();
    }

    public static void close(){
        try {
            sqlService.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    public static Connection getConnection(String database, Path directory) {
        try {
            return sqlService.getDataSource(getURI(database, directory)).getConnection();
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    private static String getURI(String database, Path directory) {
        return "jdbc:mariadb://" + PluginConfiguration.Database.IP +
                ":" + PluginConfiguration.Database.PORT +
                "/" + database +
                "?user=" + PluginConfiguration.Database.USER +
                "&password=" + PluginConfiguration.Database.PASS;
    }

}