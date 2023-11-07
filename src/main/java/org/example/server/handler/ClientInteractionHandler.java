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

public class ClientInteractionHandler {
    private static final Logger LOGGER = LogManager.getLogger(ClientInteractionHandler
            .class.getName());
    private static final String FILE_NAME_FOR_COMMAND = "src/main/resources/Commands.txt";
    private static final String NUMERIC_PATTERN = "\\d+";
    private static final int MULTIPLIER = 1000;

    private final Socket socket;

    public ClientInteractionHandler(Socket socket) {
        this.socket = socket;
    }

    public void handleClientInput() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket
                .getInputStream()));
                PrintWriter outputToClient = new PrintWriter(socket.getOutputStream(), true)) {
            manageClientCommunication(reader, outputToClient);
        } catch (IOException e) {
            LOGGER.error("The client finished connection with the server");
        }
    }

    private void manageClientCommunication(BufferedReader reader,
                                           PrintWriter outputToClient) throws IOException {
        String input;
        while ((input = reader.readLine()) != null) {
            String response = processClientInput(input);
            outputToClient.println(response);
            LOGGER.info(String.format("Received: %s, %s", input, response));
        }
    }

    private String processClientInput(String input) {
        String command = processCommand(input);
        if (command.equals("Command not found")) {
            if (input.matches(NUMERIC_PATTERN)) {
                return multiplyByThousand(input);
            } else {
                return modifyString(input);
            }
        }
        return "Server answer: " + command;
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

    private String multiplyByThousand(String message) {
        int number = Integer.parseInt(message);
        return "Server answer: " + (number * MULTIPLIER);
    }

    private String modifyString(String message) {
        StringBuilder result = new StringBuilder();
        boolean toUpperCase = true;
        for (char symbol : message.toCharArray()) {
            if (Character.isLetter(symbol)) {
                if (toUpperCase) {
                    result.append(Character.toUpperCase(symbol));
                } else {
                    result.append(Character.toLowerCase(symbol));
                }
                toUpperCase = !toUpperCase;
            } else if (symbol == ' ') {
                result.append('_');
            } else {
                result.append(symbol);
            }
        }
        return "Server answer: " + result;
    }
}
