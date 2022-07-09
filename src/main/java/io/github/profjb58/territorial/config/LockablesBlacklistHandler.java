package io.github.profjb58.territorial.config;

import com.mojang.datafixers.util.Pair;
import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.api.LockablesBlacklist;
import io.github.profjb58.territorial.util.task.AsyncTask;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.lwjgl.system.CallbackI;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class LockablesBlacklistHandler {

    private static final String configDirectory;
    private static final List<String> blacklistDefaults;
    private static final Set<String> blacklistSet;

    private File blacklistFile;
    private final ScheduledExecutorService scheduler;

    private static final TranslatableText BLOCK_ADD_SUCCESS = new TranslatableText("message.territorial.blacklist.add_block_success");
    private static final TranslatableText BLOCK_REMOVE_SUCCESS = new TranslatableText("message.territorial.blacklist.remove_block_success");
    private static final TranslatableText FILE_IO_WRITE_ERROR = new TranslatableText("message.territorial.blacklist.write_error");
    private static final TranslatableText FILE_IO_REMOVE_ERROR = new TranslatableText("message.territorial.blacklist.remove_error");

    public LockablesBlacklistHandler(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
        this.blacklistFile = new File(configDirectory + "/lockables_blacklist.txt");

        try {
            var configPath = Path.of(configDirectory);

            if(Files.notExists(configPath))
                Files.createDirectory(configPath);
            if(blacklistFile.createNewFile()) {
                var fileWriter = new FileWriter(blacklistFile);
                for(String blacklistedBlock : blacklistDefaults)
                    fileWriter.append(blacklistedBlock).append("\n");
                fileWriter.close();
            }
            read();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean addBlock(Block block, MinecraftServer server, @Nullable ServerPlayerEntity source) {
        String blockRegistryKey = getRegistryKey(block);

        if(blacklistSet.add(blockRegistryKey)) {
            new AsyncTask(new Identifier(Territorial.MOD_ID, "blacklist_add_block_task"),
                    () -> {
                        try {
                            writeAsync(blockRegistryKey);
                            if(source != null)
                                server.execute(() -> source.sendMessage(BLOCK_ADD_SUCCESS, false));
                        } catch (IOException e) {
                            if(source != null)
                                server.execute(() -> source.sendMessage(FILE_IO_WRITE_ERROR, false));
                            else
                                Territorial.LOGGER.error("Failed to add a block to blacklist and save changes to lockables_blacklist.txt");
                            e.printStackTrace();
                        }
                    }, null).submit(scheduler);
            return true;
        }
        return false;
    }

    public boolean removeBlock(Block block, MinecraftServer server, @Nullable ServerPlayerEntity source) {
        String blockRegistryKey = getRegistryKey(block);

        if(blacklistSet.remove(blockRegistryKey)) {
            new AsyncTask(new Identifier(Territorial.MOD_ID, "blacklist_remove_block_task"),
                    () -> {
                        try {
                            removeAsync(blockRegistryKey);
                            if(source != null)
                                server.execute(() -> source.sendMessage(BLOCK_REMOVE_SUCCESS, false));
                        } catch (IOException e) {
                            if(source != null)
                                server.execute(() -> source.sendMessage(FILE_IO_REMOVE_ERROR, false));
                            else
                                Territorial.LOGGER.error("Failed to remove a block from the blacklist and save changes to lockables_blacklist.txt");
                            e.printStackTrace();
                        }
                    }, null).submit(scheduler);
            return true;
        }
        return false;
    }

    private void writeAsync(String blockRegistryKey) throws IOException {
        var writer = new BufferedWriter(new FileWriter(blacklistFile, true));
        writer.write(blockRegistryKey + "\n");
        writer.close();
    }

    private void removeAsync(String blockRegistryKey) throws IOException {
        var tempFile = new File(configDirectory + "/lockables_temp.txt");
        var writer = new BufferedWriter(new FileWriter(tempFile, true));
        var reader = new BufferedReader(new FileReader(blacklistFile));
        boolean tempFileCreated = tempFile.createNewFile();
        String currentLine;

        while((currentLine = reader.readLine()) != null) {
            String trimmedLine = currentLine.trim();
            if(trimmedLine.equals(blockRegistryKey)) continue;
            writer.write(currentLine + System.getProperty("line.separator"));
        }
        writer.close();
        reader.close();
        boolean deleteSuccessful = blacklistFile.delete();
        boolean renameSuccessful = tempFile.renameTo(blacklistFile);

        if(tempFileCreated || !deleteSuccessful || !renameSuccessful)
            throw new IOException();
    }

    private void read() throws IOException {
        var scanner = new Scanner(blacklistFile);
        while(scanner.hasNextLine())
            blacklistSet.add(scanner.nextLine());
        scanner.close();
    }

    @Nullable
    private static String getRegistryKey(Block block) {
        var optionalKey = Registry.BLOCK.getKey(block);
        return optionalKey.map(blockRegistryKey -> blockRegistryKey.getValue().toString()).orElse(null);
    }

    public static boolean isBlacklisted(Block block) { return blacklistSet.contains(getRegistryKey(block)); }

    public List<String> asList() { return blacklistSet.stream().toList(); }

    private static List<String> getColouredBlockList(String[] colouredBlocks) {
        List<String> colouredBlocksList = new ArrayList<>();
        for(String colouredBlock : colouredBlocks) {
            var split = colouredBlock.split(":");
            for(var dyeColour : DyeColor.values())
                colouredBlocksList.add(split[0] + ":" + dyeColour.getName() + "_" + split[1]);
        }
        return colouredBlocksList;
    }

    static {
        configDirectory = FabricLoader.getInstance().getConfigDir().toString() + "/" + Territorial.MOD_ID;
        blacklistSet = new LinkedHashSet<>();
        blacklistDefaults = new ArrayList<>();
        blacklistDefaults.addAll(getColouredBlockList(LockablesBlacklist.COLOURED_BLOCKS));
        blacklistDefaults.addAll(List.of(LockablesBlacklist.VANILLA_BLOCKS));
        blacklistDefaults.addAll(List.of(LockablesBlacklist.TERRITORIAL_BLOCKS));
        blacklistDefaults.addAll(getColouredBlockList(LockablesBlacklist.MODDED_COLOURED_BLOCKS));
        blacklistDefaults.addAll(List.of(LockablesBlacklist.MODDED_BLOCKS));
    }
}
