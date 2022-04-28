package io.github.profjb58.territorial.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.yggdrasil.response.MinecraftProfilePropertiesResponse;
import io.github.profjb58.territorial.Territorial;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class UuidUtils {
    private static final String MOJANG_UUID_API = "https://api.mojang.com/users/profiles/minecraft/";

    @Nullable
    @Environment(EnvType.CLIENT)
    public static UUID findUuid(String playerName) {
        UUID uuid = null;
        CompletableFuture<UUID> uuidFuture = CompletableFuture.supplyAsync(() -> UuidUtils.getUuidFromPlayer(playerName));
        try {
            uuid = uuidFuture.get();
        } catch (InterruptedException | ExecutionException ignored) { }
        return uuid;
    }

    private static UUID getUuidFromPlayer(String playerName) {
        UUID uuid;

        try {
            URL uuidGetRequest = new URL(MOJANG_UUID_API + playerName);
            var httpURLConnection = (HttpURLConnection) uuidGetRequest.openConnection();
            httpURLConnection.setRequestMethod("GET");

            int responseCode = httpURLConnection.getResponseCode();
            if(responseCode != 200)
                Territorial.LOGGER.warn("Failed to get UUID for the player, Api response code: " + responseCode);

            var stringBuilder = new StringBuilder();
            var scanner = new Scanner(uuidGetRequest.openStream());
            while(scanner.hasNext()) {
                stringBuilder.append(scanner.nextLine());
            }
            scanner.close();

            var uuidObject = (JsonObject) JsonParser.parseString(stringBuilder.toString());
            String id = uuidObject.get("id").getAsString();

            // Format into a proper uuid (accepted) uuid format complete with '-' characters
            String idFormatted = id.substring(0, 8) + "-" + id.substring(8, 12) + "-"
                    + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20, 32);

            uuid = UUID.fromString(idFormatted);

        } catch (IOException e) {
            uuid = null;
        }

        return uuid;
    }

    public static class LootStack {

        public static NbtCompound create() {
            NbtCompound compound = new NbtCompound();
            compound.putUuid("territorial_stack_id", UUID.randomUUID());
            return compound;
        }

        @Nullable
        public static UUID getUuid(ItemStack itemStack) {
            NbtCompound compound = itemStack.getNbt();
            if(compound != null && compound.contains("territorial_stack_id")) {
                return itemStack.getOrCreateNbt().getUuid("territorial_stack_id");
            }
            return null;
        }
    }
}
