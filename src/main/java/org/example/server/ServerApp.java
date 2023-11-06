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
import org.example.server.handler.ServerHandler;

public class ServerApp {
    private static final int PORT = 3344;
    private static final int COUNT_OF_THREADS = 1;
    private static final int PERIOD = 10;
    private static final String DATE_PATTERN = "dd.MM.yyyy HH:mm:ss";
    private static final String FILE_NAME_FOR_LOGGING = "LogFile.txt";
    private static int counter = 0;
    private static final ScheduledExecutorService executor = Executors
            .newScheduledThreadPool(COUNT_OF_THREADS);

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Waiting for client...");
            Socket clientSocket = serverSocket.accept();
            executor.scheduleAtFixedRate(() -> sendPeriodicMessageToTheClients(clientSocket),
                    0, PERIOD, TimeUnit.SECONDS);
            ServerHandler serverHandler = new ServerHandler(clientSocket);
                serverHandler.run();

        } catch (IOException e) {
            throw new RuntimeException("An error occurred while running the server or accepting" +
                    " client connections: " + e.getMessage(), e);
        }
    }

    private static void writePeriodicMessageToLogFile(String message) {
        try (FileWriter writer = new FileWriter(FILE_NAME_FOR_LOGGING, true)) {
            writer.write(message + "\n");
        } catch (IOException e) {
            throw new RuntimeException("Can't write the periodic message to the logFile: "
                    + FILE_NAME_FOR_LOGGING, e);
        }
    }

    private static void sendPeriodicMessageToTheClients(Socket socket) {
        try (OutputStream output = socket.getOutputStream();
             PrintWriter writer = new PrintWriter(output, true)) {
            String currentTime = LocalDateTime.now().format(DateTimeFormatter
                    .ofPattern(DATE_PATTERN));
            String message = "Counter " + counter++ + ", Time " + currentTime;
            writer.println(message);
            writePeriodicMessageToLogFile(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
