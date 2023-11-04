package org.example.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.example.server.handler.ServerHandler;

public class ServerApp {
    public static void main(String[] args) {
        int port = 8888;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started. Waiting for client...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ServerHandler serverHandler = new ServerHandler(clientSocket);
                serverHandler.run();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
