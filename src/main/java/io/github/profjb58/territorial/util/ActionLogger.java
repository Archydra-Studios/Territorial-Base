package io.github.profjb58.territorial.util;

import io.github.profjb58.territorial.Territorial;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Environment(EnvType.SERVER)
public class ActionLogger {

    public enum LogType {
        INFO,
        WARNING,
        ERROR
    }

    public enum LogModule {
        LOCKS,
        CLAIMS,
        TRAPS,
        BLANK
    }

    static final String LOGS_DIRECTORY = FabricLoader.getInstance().getGameDir() + "/logs/";

    public ActionLogger() {
        File logFile = new File(LOGS_DIRECTORY + "territorial.log");

        try {
            // Add in additional loggers if required here...
            if(logFile.createNewFile()) {
                Territorial.LOGGER.info("Logger files created");
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void write(LogType type, String message) {
        this.write(type, LogModule.BLANK, message);
    }

    public void write(LogType type, LogModule module, String message) {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[HH:mm:ss] - [dd/MM/yyyy]");
        String formattedDateTime = dateTime.format(formatter);

        try {
            FileWriter logWriter = new FileWriter(LOGS_DIRECTORY + "territorial.log", true);
            String typeSpecifier, moduleSpecifier;

            typeSpecifier = switch (type) {
                case INFO -> " [INFO] ";
                case WARNING -> " [WARNING] ";
                case ERROR -> " [ERROR] ";
            };

            moduleSpecifier = switch (module) {
                case CLAIMS -> "(claims) - ";
                case LOCKS -> "(locks) - ";
                case TRAPS -> "(traps) - ";
                case BLANK -> "- ";
            };

            logWriter.write(formattedDateTime + typeSpecifier + moduleSpecifier + message + "\n");
            logWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
