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

    private enum LogType {
        LOCKS,
        CLAIMS
    }

    private final File logFile;
    private FileWriter logWriter;
    static final String LOGS_DIRECTORY = FabricLoader.getInstance().getGameDir() + "/logs";

    public ActionLogger() {
        this.logFile = new File(LOGS_DIRECTORY + "territorial.log");

        try {
            // Add in additional loggers if required here...
            if(logFile.createNewFile()) {
                Territorial.logger.info("Logger files created");
            }

            this.logWriter = new FileWriter(LOGS_DIRECTORY + "territorial.log", true);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void write(LogType type, String message) {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[HH:MM][dd/MM/yyyy]");
        String formattedDateTime = dateTime.format(formatter);

        if(logWriter != null) {
            try {
                String specifier = (type == LogType.LOCKS) ? " {LOCKS} - " : " {CLAIMS} - ";
                logWriter.write(formattedDateTime + specifier + message);
                logWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
