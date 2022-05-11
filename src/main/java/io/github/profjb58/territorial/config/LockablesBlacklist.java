package io.github.profjb58.territorial.config;

import com.mojang.datafixers.util.Pair;
import io.github.profjb58.territorial.Territorial;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.util.DyeColor;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class LockablesBlacklist implements Runnable {

    private static final String CONFIG_DIRECTORY;
    private static final List<String> BLACKLIST_DEFAULTS;

    private File blacklistFile;
    private final Set<String> blacklist;

    public enum QueueOperation { WRITE, REMOVE }
    private final BlockingQueue<Pair<QueueOperation, String>> queue;
    private final AtomicBoolean threadActive;

    public LockablesBlacklist() {
        this.blacklistFile = new File(CONFIG_DIRECTORY + "/lockables_blacklist.txt");
        this.blacklist = new LinkedHashSet<>();
        this.queue = new LinkedBlockingQueue<>();
        this.threadActive = new AtomicBoolean(true);

        try {
            if(Files.notExists(Paths.get(CONFIG_DIRECTORY)))
                Files.createDirectory(Paths.get(CONFIG_DIRECTORY));
            if(blacklistFile.createNewFile()) {
                var fileWriter = new FileWriter(blacklistFile);
                for(String blacklistedBlock : BLACKLIST_DEFAULTS)
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
                    boolean deleteSuccessful, renameSuccessful, tempFileCreated;
                    var tempFile = new File(CONFIG_DIRECTORY + "/lockables_temp.txt");
                    var writer = new BufferedWriter(new FileWriter(tempFile, true));
                    var reader = new BufferedReader(new FileReader(blacklistFile));
                    tempFileCreated = tempFile.createNewFile();
                    String currentLine;

                    while((currentLine = reader.readLine()) != null) {
                        String trimmedLine = currentLine.trim();
                        if(trimmedLine.equals(registryKey)) continue;
                        writer.write(currentLine + System.getProperty("line.separator"));
                    }
                    writer.close();
                    reader.close();
                    deleteSuccessful = blacklistFile.delete();
                    renameSuccessful = tempFile.renameTo(blacklistFile);

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
            blacklist.add(scanner.nextLine());
        scanner.close();
    }

    public boolean addBlock(Block block) {
        if(blacklist.add(getRegistryKey(block))) {
            queue.add(Pair.of(QueueOperation.WRITE, getRegistryKey(block)));
            return true;
        }
        return false;
    }

    public boolean removeBlock(Block block) {
        if(blacklist.remove(getRegistryKey(block))) {
            queue.add(Pair.of(QueueOperation.REMOVE, getRegistryKey(block)));
            return true;
        }
        return false;
    }

    @Nullable
    private String getRegistryKey(Block block) {
        var optionalKey = Registry.BLOCK.getKey(block);
        return optionalKey.map(blockRegistryKey -> blockRegistryKey.getValue().toString()).orElse(null);
    }

    public boolean isBlacklisted(Block block) { return blacklist.contains(getRegistryKey(block)); }

    public List<String> asList() { return blacklist.stream().toList(); }

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
        String[] colouredBlocks = {
                "minecraft:bed", "minecraft:banner"
        };
        String[] singleEntryBlocks = {
                "minecraft:beacon", "minecraft:comparator", "minecraft:conduit", "minecraft:daylight_detector",
                "minecraft:end_gateway", "minecraft:end_portal", "minecraft:piston", "minecraft:sticky_piston",
                "minecraft:sculk_sensor", "minecraft:bell", "territorial:laser_transmitter", "territorial:laser_receiver",
                "territorial:boundary_beacon"
        };

        CONFIG_DIRECTORY = FabricLoader.getInstance().getConfigDir().toString() + "/" + Territorial.MOD_ID;
        BLACKLIST_DEFAULTS = new ArrayList<>();
        BLACKLIST_DEFAULTS.addAll(getColouredBlockList(colouredBlocks));
        BLACKLIST_DEFAULTS.addAll(List.of(singleEntryBlocks));
    }
}
