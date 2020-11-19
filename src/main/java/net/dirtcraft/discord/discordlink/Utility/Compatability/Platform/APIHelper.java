package net.dirtcraft.discord.discordlink.Utility.Compatability.Platform;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class APIHelper {
    private static Gson gson;
    private static Optional<URL> getAPINameQuery(UUID uuid){
        String address = String.format("https://api.mojang.com/user/profiles/%s/names", uuid.toString().replace("-", ""));
        try{
            return Optional.of(new URL(address));
        } catch (MalformedURLException e){
            return Optional.empty();
        }
    }

    private static List<MinecraftName> queryAPIName(URL url){
        URLConnection connection;
        try{
            connection = url.openConnection();
        } catch (IOException e){
            return new ArrayList<>();
        }

        try (InputStream is = connection.getInputStream();
             InputStreamReader isr = new InputStreamReader(is)){
            return getGson().fromJson(isr, new TypeToken<List<MinecraftName>>(){}.getType());
        } catch (IOException e){
            return new ArrayList<>();
        }
    }

    private static Optional<MinecraftName> getLatestUsername(List<MinecraftName> names){
        return names.stream().reduce((a,b)->a.changedToAt > b.changedToAt ? a : b);
    }

    public static Optional<String> getLatestUsername(UUID uuid){
        return getAPINameQuery(uuid)
                .map(APIHelper::queryAPIName)
                .flatMap(APIHelper::getLatestUsername)
                .map(MinecraftName::getName);
    }

    private static Gson getGson(){
        if (gson == null) gson = new GsonBuilder().create();
        return gson;
    }

    private static class MinecraftName{
        long changedToAt;
        String name;

        private String getName(){
            return name;
        }
    }
}
