package org.example.server.handler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.example.server.ServerApp;

public class ServerHandler implements Runnable {
    private static final String FILE_NAME_FOR_COMMAND = "Commands.txt";
    private static final String FILE_NAME_FOR_LOGGING = "LogFile.txt";
    private static final String ALPHABETIC_PATTERN = ".*[a-zA-Z]+.*";
    private static final String NUMERIC_PATTERN = "\\d+";
    private static final int MULTIPLIER = 1000;
    private static final int COUNT_OF_THREADS = 1;
    private static final int PERIOD = 10;

    private final Socket socket;

    public ServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        ScheduledExecutorService scheduler = null;
        try (BufferedReader inputFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter outputToClient = new PrintWriter(socket.getOutputStream(), true)) {
            scheduler = Executors.newScheduledThreadPool(COUNT_OF_THREADS);
            scheduler.scheduleAtFixedRate(() -> {
                ServerApp.incrementClientCounter(socket);
                int counter = ServerApp.getClientCounter(socket);
                sendPeriodicMessage(counter);
            }, 0, PERIOD, TimeUnit.SECONDS);

            String input;
            while ((input = inputFromClient.readLine()) != null) {
                System.out.println("Received from client: " + input);

                String response = processClientInput(input);
                outputToClient.println(response);

                writeToLogFile("Received: " + input, "Server response: " + response);
            }
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while handling client's request: "
                    + e.getMessage(), e);

        } finally {
            if (scheduler != null) {
                scheduler.shutdown();
            }
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

    private void writePeriodicMessageToLogFile(String sent) {
        try (FileWriter writer = new FileWriter(FILE_NAME_FOR_LOGGING, true)) {
            writer.write(sent + "\n");
        } catch (IOException e) {
            throw new RuntimeException("Can't write the periodic message to the logFile: "
                    + FILE_NAME_FOR_LOGGING, e);
        }
    }

    private void sendPeriodicMessage(int counter) {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        String message = "Counter " + counter + ", Time " + currentTime;
        try (PrintWriter outputToClient = new PrintWriter(socket.getOutputStream(), true)) {
//            outputToClient.println(message);
            writePeriodicMessageToLogFile(message);
        } catch (IOException e) {
            throw new RuntimeException("Failed to send periodic message to the client. Please "
                    + "check the connection and try again.", e);
        }
    }
}