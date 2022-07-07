package io.github.profjb58.territorial.config;

import com.mojang.datafixers.util.Pair;
import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.api.LockablesBlacklist;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.util.DyeColor;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class LockablesBlacklistHandler implements Runnable {

    private static final String configDirectory;
    private static final List<String> blacklistDefaults;
    private static final Set<String> blacklistSet;

    public enum QueueOperation { WRITE, REMOVE }

    private File blacklistFile;
    private final BlockingQueue<Pair<QueueOperation, String>> queue;
    private final AtomicBoolean threadActive;

    public LockablesBlacklistHandler() {
        this.blacklistFile = new File(configDirectory + "/lockables_blacklist.txt");
        this.queue = new LinkedBlockingQueue<>();
        this.threadActive = new AtomicBoolean(true);

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
        new Thread(this, "Lockables Blacklist File IO (Territorial)").start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> threadActive.set(false)));
    }

    @Override
    public void run() {
        try {
            while(threadActive.get()) {
                var element = queue.take();
                var queueOperation = element.getFirst();
                var registryKey = element.getSecond();

                if(queueOperation == QueueOperation.WRITE) {
                    var writer = new BufferedWriter(new FileWriter(blacklistFile, true));
                    writer.write(registryKey + "\n");
                    writer.close();
                }
                else {
                    var tempFile = new File(configDirectory + "/lockables_temp.txt");
                    var writer = new BufferedWriter(new FileWriter(tempFile, true));
                    var reader = new BufferedReader(new FileReader(blacklistFile));
                    boolean tempFileCreated = tempFile.createNewFile();
                    String currentLine;

                    while((currentLine = reader.readLine()) != null) {
                        String trimmedLine = currentLine.trim();
                        if(trimmedLine.equals(registryKey)) continue;
                        writer.write(currentLine + System.getProperty("line.separator"));
                    }
                    writer.close();
                    reader.close();
                    boolean deleteSuccessful = blacklistFile.delete();
                    boolean renameSuccessful = tempFile.renameTo(blacklistFile);

                    if(!tempFileCreated || !deleteSuccessful || !renameSuccessful)
                        throw new IOException();
                }
            }
        }
        catch (InterruptedException | IOException e) {
            Territorial.LOGGER.error("Failed to add/remove a block from blacklist and save changes to lockables_blacklist.txt");
            Territorial.LOGGER.error("You can manually modify this file instead of using commands if this remains an issue");
            e.printStackTrace();
        }
    }

    private void read() throws IOException {
        var scanner = new Scanner(blacklistFile);
        while(scanner.hasNextLine())
            blacklistSet.add(scanner.nextLine());
        scanner.close();
    }

    public boolean addBlock(Block block) {
        if(blacklistSet.add(getRegistryKey(block))) {
            queue.add(Pair.of(QueueOperation.WRITE, getRegistryKey(block)));
            return true;
        }
        return false;
    }

    public boolean removeBlock(Block block) {
        if(blacklistSet.remove(getRegistryKey(block))) {
            queue.add(Pair.of(QueueOperation.REMOVE, getRegistryKey(block)));
            return true;
        }
        return false;
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
