package org.example.server.handler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerHandler implements Runnable {
    private final Socket socket;

    public ServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader inputFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter outputToClient = new PrintWriter(socket.getOutputStream(), true);

            String input;
            while ((input = inputFromClient.readLine()) != null) {
                System.out.println("Received from client: " + input);

                String response = processClientInput(input);
                outputToClient.println(response);

                writeToLogFile("Received: " + input, "Server response: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String processClientInput(String input) {
        String command = processCommand(input);
        if (command.equals("Command not found")) {
            if (input.matches(".*[a-zA-Z]+.*")) {
                return modifyString(input);
            } else if (input.matches("\\d+")) {
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
        return String.valueOf(number * 1000);
    }

    private String processCommand(String command) {
        try (BufferedReader reader = new BufferedReader(new FileReader("Commands.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].equals(command)) {
                    return parts[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Command not found";
    }

    private void writeToLogFile(String received, String sent) {
        try (FileWriter writer = new FileWriter("LogFile.txt", true)) {
            writer.write(received + ", " + sent + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}