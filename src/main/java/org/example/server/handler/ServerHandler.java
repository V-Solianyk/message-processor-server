package org.example.server.handler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerHandler {
    private static final String FILE_NAME_FOR_COMMAND = "Commands.txt";
    private static final String FILE_NAME_FOR_LOGGING = "LogFile.txt";
    private static final String ALPHABETIC_PATTERN = ".*[a-zA-Z]+.*";
    private static final String NUMERIC_PATTERN = "\\d+";
    private static final int MULTIPLIER = 1000;

    private final Socket socket;

    public ServerHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try (BufferedReader inputFromClient = new BufferedReader(new InputStreamReader(socket
                .getInputStream()));
             PrintWriter outputToClient = new PrintWriter(socket.getOutputStream(), true)) {
            String input;
                while ((input = inputFromClient.readLine()) != null) {
                    System.out.println("Received from client: " + input);
                    String response = processClientInput(input);
                    outputToClient.println(response);
                    writeToLogFile("Received: " + input, "Server answer: " + response);
                }
        } catch (IOException e) {
            System.out.println("The client finished connect with the server");
        }
    }

    private String processClientInput(String input) {
        String command = processCommand(input);
        if (command.equals("Command not found")) {
            if (input.matches(ALPHABETIC_PATTERN)) {
                return modifyString(input);
            } else if (input.matches(NUMERIC_PATTERN)) {
                return multiplyByThousand(input);
            }
        }
        return command;
    }

    private String modifyString(String message) {
        StringBuilder result = new StringBuilder();
        boolean toUpperCase = true;
        for (char c : message.toCharArray()) {
            if (Character.isLetter(c)) {
                if (toUpperCase) {
                    result.append(Character.toUpperCase(c));
                } else {
                    result.append(Character.toLowerCase(c));
                }
                toUpperCase = !toUpperCase;
            } else if (c == ' ') {
                result.append('_');
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private String multiplyByThousand(String message) {
        int number = Integer.parseInt(message);
        return String.valueOf(number * MULTIPLIER);
    }

    private String processCommand(String command) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME_FOR_COMMAND))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].equals(command)) {
                    return parts[1];
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while trying to read from a file: "
                    + FILE_NAME_FOR_COMMAND, e);
        }
        return "Command not found";
    }

    private void writeToLogFile(String received, String sent) {
        try (FileWriter writer = new FileWriter(FILE_NAME_FOR_LOGGING, true)) {
            writer.write(received + ", " + sent + "\n");
        } catch (IOException e) {
            throw new RuntimeException("Can't write the message to the logFile: "
                    + FILE_NAME_FOR_LOGGING, e);
        }
    }
}
