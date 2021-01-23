package io.github.profjb58.territorial.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.profjb58.territorial.Territorial;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class UuidUtils {

    private static final String MOJANG_UUID_API = "https://api.mojang.com/users/profiles/minecraft/";

    public static UUID getUUIDFromPlayer(String playerName) {
        UUID uuid;

        try {
            URL uuidGetRequest = new URL(MOJANG_UUID_API + playerName);
            HttpURLConnection connection = (HttpURLConnection) uuidGetRequest.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if(responseCode != 200) {
                Territorial.logger.warn("Failed to get UUID for the player, Api response code: " + responseCode);
            }

            StringBuilder inline = new StringBuilder();
            Scanner scanner = new Scanner(uuidGetRequest.openStream());

            while(scanner.hasNext()) {
                inline.append(scanner.nextLine());
            }

            scanner.close();
            JsonParser parser = new JsonParser();
            JsonObject uuidObject = (JsonObject) parser.parse(inline.toString());
            String id = uuidObject.get("id").getAsString();

            String idFormatted = id.substring(0, 8) + "-" + id.substring(8, 12) + "-"
                    + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20, 32);

            uuid = UUID.fromString(idFormatted);

        } catch (IOException e) {
            uuid = null;
        }

        return uuid;
    }
}
