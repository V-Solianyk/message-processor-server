package org.example.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.example.server.handler.ServerHandler;

public class ServerApp {
    private static final Map<Socket, Integer> clientCounters = new ConcurrentHashMap<>();
    private static final int PORT = 3344;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Waiting for client...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ServerHandler serverHandler = new ServerHandler(clientSocket);
                clientCounters.put(clientSocket, 0);
                serverHandler.run();
            }
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while running the server or accepting" +
                    " client connections: " + e.getMessage(), e);
        }
    }

    public static int getClientCounter(Socket clientSocket) {
        return clientCounters.get(clientSocket);
    }

    public static void incrementClientCounter(Socket clientSocket) {
        clientCounters.computeIfPresent(clientSocket, (key, value) -> value + 1);
    }
}
