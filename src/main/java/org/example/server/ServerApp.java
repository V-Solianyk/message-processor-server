package org.example.server;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.server.handler.ServerHandler;

public class ServerApp {
    private static final int PORT = 3344;
    private static final int COUNT_OF_THREADS = 1;
    private static final int PERIOD = 10;
    private static final String DATE_PATTERN = "dd.MM.yyyy HH:mm:ss";
    private static int counter = 0;
    private static final Logger LOGGER = LogManager.getLogger(ServerApp.class.getName());
    private static final ScheduledExecutorService executor = Executors
            .newScheduledThreadPool(COUNT_OF_THREADS);

    public static void main(String[] args) {
        LOGGER.info("Server started. Waiting for client...");
        try (ServerSocket serverSocket = new ServerSocket(PORT);
             Socket clientSocket = serverSocket.accept();
             OutputStream output = clientSocket.getOutputStream();
             PrintWriter printWriter = new PrintWriter(output, true)) {
            executor.scheduleAtFixedRate(() -> sendPeriodicMessageToTheClients(printWriter),
                    0, PERIOD, TimeUnit.SECONDS);
            ServerHandler serverHandler = new ServerHandler(clientSocket);
            serverHandler.run();
        } catch (IOException e) {
            LOGGER.error("An error occurred while running the server or accepting" +
                    " client connections: " + e.getMessage(), e);
        }
    }

    private static void sendPeriodicMessageToTheClients(PrintWriter printWriter) {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter
                .ofPattern(DATE_PATTERN));
        String message = "Counter " + counter++ + ", Time " + currentTime;
        printWriter.println(message);
    }
}
