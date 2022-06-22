package io.github.profjb58.territorial.database;

import io.github.profjb58.territorial.Territorial;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.DoorBlock;
import org.apache.commons.lang3.tuple.Triple;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Database /*implements Runnable */ {

    /*private static final String URL = "jdbc:h2:" + FabricLoader.getInstance().getGameDir().toString() + "/territorial";

    private Connection connection = null;
    private static Database dbInstance = null;
    private final Thread dbThread;
    private BlockingQueue<Triple<Object, CompletableFuture<QueryResult>, StackTraceElement[]>> queue;

    private volatile boolean running = false;

    private Database() {
        dbThread = new Thread(this, "Territorial Database Thread");
        dbThread.setDaemon(true);
        dbThread.start();
    }

    private void connect() throws SQLException, ClassNotFoundException{
        Class.forName("org.h2.Driver");
        connection = DriverManager.getConnection(URL);
        running = true;
        Territorial.LOGGER.info("H2 database engine URL: " + URL);
    }

    public void close() {
        running = false;
    }

    @Override
    public void run() {
        ResultSet result = null;
        try {
            connect();
            while (running) {
                Triple<Object, CompletableFuture<QueryResult>, StackTraceElement[]> element = queue.poll(20, TimeUnit.MILLISECONDS);
                if(element != null) {
                    try {
                        result = connection.createStatement().executeQuery(element.getLeft());


                    }
                    catch(Exception e) {
                        Territorial.LOGGER.error("Stacktrace before database thread execution: ");
                        for(StackTraceElement ste : element.getRight()) {
                            Territorial.LOGGER.error(ste.toString());
                        }
                    }
                }
            }
        }
        catch(ClassNotFoundException e) {
            Territorial.LOGGER.error("Failed to find a valid driver for the H2 database engine");
            e.printStackTrace();
        }
        catch(SQLException e) {
            Territorial.LOGGER.warn("Failed to connect to the H2 database engine... Some features of the mod will be disabled");
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            // Poll queue exception
        }
        finally {
            if(connection != null) {
                try { connection.close(); }
                catch(SQLException e) {
                    Territorial.LOGGER.warn("Failed to close connection to the H2 database engine...");
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    public static Database getInstance() {
        if(dbInstance == null) {
            dbInstance = new Database();
        }
        return dbInstance;
    }

    public CompletableFuture<QueryResult> queueQuery(String query) {
        CompletableFuture<QueryResult> future = new CompletableFuture<>();
        queue.add(Triple.of(query, future, Thread.currentThread().getStackTrace()));
        return future;
    }*/
}
