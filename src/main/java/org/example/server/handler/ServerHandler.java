package org.example.server.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerHandler {
    private static final String FILE_NAME_FOR_COMMAND = "Commands.txt";
    private static final String ALPHABETIC_PATTERN = ".*[a-zA-Z]+.*";
    private static final String NUMERIC_PATTERN = "\\d+";
    private static final int MULTIPLIER = 1000;
    private static final Logger LOGGER = LogManager.getLogger(ServerHandler.class.getName());

    private final Socket socket;

    public ServerHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket
                .getInputStream()));
             PrintWriter outputToClient = new PrintWriter(socket.getOutputStream(), true)) {
            String input;
            while ((input = reader.readLine()) != null) {
                String response = processClientInput(input);
                outputToClient.println(response);
                LOGGER.info("Received: " + input + ", Server answer: " + response);
            }
        } catch (IOException e) {
            LOGGER.error("The client finished connect with the server");
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
        return "Server answer: " + command;
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
        return "Server answer: " + result;
    }

    private String multiplyByThousand(String message) {
        int number = Integer.parseInt(message);
        return "Server answer: " + (number * MULTIPLIER);
    }

    private String processCommand(String input) {
        try {
            List<String> commands = Files.readAllLines(Path.of(FILE_NAME_FOR_COMMAND));
            for (String command : commands) {
                String[] parts = command.split(":");
                if (parts[0].equals(input)) {
                    return parts[1];
                }
            }
        } catch (IOException e) {
            LOGGER.error("An error occurred while trying to read from a file: "
                    + FILE_NAME_FOR_COMMAND, e);
        }
        return "Command not found";
    }
}
