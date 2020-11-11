package net.dirtcraft.discord.discordlink.Utility;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class CrashDetector {
    private static final int errorEmbedColor = 10223635;

    public static void analyze(JavaPlugin plugin) {
        try {
            final String crashFolder = plugin.getServer().getWorldContainer().getCanonicalPath() + File.separator + "crash-reports";

            File[] crashReports = new File (crashFolder).listFiles();

            if (crashReports != null) {
                final int crashReportLength = plugin.getConfig().getInt("crashReportLength");
                if (crashReports.length > crashReportLength) {
                    plugin.getLogger().warning("New crash found since last restart.");
                    final String serverName = PluginConfiguration.Main.SERVER_NAME;
                    final String channelID = PluginConfiguration.CrashDetector.serverLogID;
                    final long memory = Runtime.getRuntime().maxMemory();


                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        try {
                            final String hasteLink = getHaste("DirtCraft Crash Reports...... Provided by DirtCraft!\n\n" +
                                    "- ModPack: " + serverName + "\n" +
                                    "- Total Memory Allocated: " + memory / 1024 / 1024 + " MB\n\n\n"
                                    + readCrashReport(getLastCrash(crashFolder)));

                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setColor(errorEmbedColor);
                            embed.setTitle("<:redbulletpoint:539273059631104052>**DirtCraft Server Logger**<:redbulletpoint:539273059631104052>");
                            embed.addField("__Crash Detected__", "\n**" + serverName + "** has crashed since last restart!\n", false);
                            embed.addField("__Information__", "\n[**Crash Report**](" + hasteLink + ")", false);
                            embed.setFooter("ModPack: " + serverName, null);
                            embed.setTimestamp(Instant.now());

                            DiscordLink.getJDA().getTextChannelById(channelID).sendMessage(embed.build()).queue();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    plugin.getConfig().set("crashReportLength", crashReports.length);
                    plugin.saveConfig();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static String getHaste(String string) {

        HttpURLConnection connection = null;


        try {
            connection = (HttpURLConnection) new URL("https://hastebin.com/documents").openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }

        if (connection == null) return "N/A";


        try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
            wr.write(string.getBytes(StandardCharsets.UTF_8));
            wr.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder response = new StringBuilder();
        try (BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String inputLine;
            while ((inputLine = rd.readLine()) != null) response.append(inputLine);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JsonElement json = new JsonParser().parse(response.toString());
        if (!json.isJsonObject()) {
            try {
                throw new IOException("Can't parse JSON");
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        return "https://hastebin.com/" + json.getAsJsonObject().get("key").getAsString();
    }

    private static File getLastCrash(String url) {
        File dir = new File(url);
        File[] files = dir.listFiles();

        if (files == null || files.length == 0) {
            return null;
        }

        File lastModifiedFile = files[0];

        for (int i = 1; i < files.length; i++) {
            if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                lastModifiedFile = files[i];
            }
        }
        return lastModifiedFile;
    }

    private static String readCrashReport(File file) throws IOException {

        List<String> contents = new ArrayList<>();
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        String line;
        while ((line = br.readLine()) != null) contents.add(line);

        br.close();
        fr.close();
        return String.join("\n", contents);
    }
}
