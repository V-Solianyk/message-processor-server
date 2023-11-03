package org.example.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final Integer PORT = 3306;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started. Waiting for a client...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected.");

                BufferedReader inputFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter outputToClient = new PrintWriter(clientSocket.getOutputStream(), true);

                String receivedMessage = inputFromClient.readLine();
                String processedMessage = processMessage(receivedMessage);

                outputToClient.println("Server answer: " + processedMessage);

                logMessages(receivedMessage, "Server answer: " + processedMessage);

                inputFromClient.close();
                outputToClient.close();
                clientSocket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String processMessage(String message) {
        try {
            //todo
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return message;
    }

    private static void logMessages(String received, String sent) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("LogFile.txt", true));
            writer.write("Received: " + received + ", Sent: " + sent);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}

