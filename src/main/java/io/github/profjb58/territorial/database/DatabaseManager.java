package io.github.profjb58.territorial.database;

import io.github.profjb58.territorial.Territorial;
import net.fabricmc.loader.api.FabricLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

public class DatabaseManager implements Runnable {

    private Connection connection = null;
    private static DatabaseManager manager = null;

    AtomicBoolean isRunning = new AtomicBoolean(false);

    // Singleton class
    private DatabaseManager() {
        connect();
    }

    private void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            String gameDir = FabricLoader.getInstance().getGameDir().toString();
            connection = DriverManager.getConnection("jdbc:sqlite:" + gameDir + "/territorial.db");
        }
        catch(SQLException | ClassNotFoundException e) {
            Territorial.logger.warn(e.getMessage());
        }
    }

    private void close() {
        try {
            if(connection != null) {
                connection.close();
            }
        }
        catch(SQLException e) {
            Territorial.logger.warn(e.getMessage());
        }
    }

    public static DatabaseManager getInstance() {
        if(manager == null) {
            manager = new DatabaseManager();
        }
        return manager;
    }

    @Override
    public void run() {

    }
}
