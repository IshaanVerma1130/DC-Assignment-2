package src;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CS {
    static int clientTimestamp = 0;

    public static void main(String[] args) {
        // Check if the correct number of command-line arguments is provided
        if (args.length != 3) {
            System.out.println("Usage: java CS <SERVER URL> <PORT> <CS ID>");
            return;
        }

        Logger logger = null;
        String SERVER_URL = args[0];
        Integer PORT = Integer.parseInt(args[1]);
        Integer CS_ID = Integer.parseInt(args[2]);

        try {
            // Create a logger for this CS instance
            logger = Logger.getLogger(CS.class.getName() + "-" + CS_ID);

            // Define the path for the log file
            String logFilePath = "logs/logFile-" + CS_ID + ".log";

            // Create the log file if it doesn't exist
            File yourFile = new File(logFilePath);
            yourFile.createNewFile();

            // Create a FileHandler to handle log entries
            Handler logHandler = new FileHandler(logFilePath);

            // Set the desired logging level for the FileHandler
            logHandler.setLevel(Level.INFO);

            // Create a SimpleFormatter to format log messages
            SimpleFormatter formatter = new SimpleFormatter();

            // Assign the formatter to the FileHandler
            logHandler.setFormatter(formatter);

            // Add the FileHandler to the Logger
            logger.addHandler(logHandler);

            // Log initialization information
            logger.info("Log File for CS " + CS_ID + "\r\n");

        } catch (IOException e) {
            System.out.println("Error creating logger. CS " + CS_ID);
            e.printStackTrace();
        }

        int maxRetries = 3;
        int retryCount = 0;

        logger.info("Starting CS " + CS_ID + "\r\n");

        try {
            // Generate a list of WeatherData entries
            List<WeatherData> entries = Utils.generateJson(CS_ID);

            // Create an ObjectMapper for JSON processing
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayNode jsonEntries = objectMapper.createArrayNode();

            // Convert WeatherData entries to JSON objects and add them to the array
            for (WeatherData entry : entries) {
                ObjectNode jsonEntry = objectMapper.valueToTree(entry);
                jsonEntries.add(jsonEntry);
            }

            if (jsonEntries.isArray()) {
                for (JsonNode jsonObject : jsonEntries) {
                    boolean success = false;

                    while (!success && retryCount < maxRetries) {
                        // Establish a socket connection to the server
                        Socket socket = new Socket(SERVER_URL, PORT);
                        OutputStream out = socket.getOutputStream();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        // Generate a POST request
                        String requestString = Utils.generatePostRequest(SERVER_URL, clientTimestamp,
                                jsonObject.toString());

                        // Send the request to the server
                        out.write(requestString.getBytes());
                        out.flush();

                        // Log the sent request
                        logger.info("\r\nSending request to AS.\r\n" + requestString + "\r\n");

                        // Read the server's response tag
                        String responseTag = in.readLine();

                        if (responseTag.equals("OK")) {
                            logger.info("Server processed request immediately.\r\n");
                            processRequest(in);
                            success = true;

                        } else if (responseTag.equals("WAIT")) {
                            logger.info("Waiting for server.\r\n");

                            // Read the "PROCESSING" response
                            String waitResponse = in.readLine();
                            if (waitResponse.equals("PROCESSING")) {
                                processRequest(in);
                                logger.info(
                                        "CS updated timestamp after processing request: " + clientTimestamp + "\r\n");
                                success = true;
                            }
                        } else {
                            logger.severe("Error processing request.");
                            retryCount++;
                        }

                        // Close socket, input stream, and output stream
                        out.close();
                        in.close();
                        socket.close();

                        try {
                            // Sleep for 1000 milliseconds (1 second)
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            // Handle the InterruptedException if needed
                        }
                    }

                    if (!success) {
                        logger.severe("Maximum retries reached.");
                    }
                    retryCount = 0;
                }
            }
        } catch (ConnectException e) {
            logger.severe(e.toString());
        } catch (FileNotFoundException e) {
            logger.severe(e.getMessage());
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
        // Log shutdown information
        logger.info("CS " + CS_ID + " shutting down.");
    }

    private static void updateClientTimestamp(int serverTimestamp) {
        // Update the client timestamp based on the server timestamp
        clientTimestamp = Math.max(clientTimestamp, serverTimestamp) + 1;
    }

    private static void processRequest(BufferedReader in) {
        // Extract and process headers from the server's response
        Map<String, String> headers = getHeaders(in);

        int serverTimestamp = Integer.parseInt(headers.getOrDefault("TIME-STAMP", "0"));
        updateClientTimestamp(serverTimestamp);
    }

    private static Map<String, String> getHeaders(BufferedReader in) {
        // Extract and parse headers from the server's response
        Map<String, String> headers = new HashMap<>();
        String line;
        try {
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                String[] headerParts = line.split(": ", 2);
                if (headerParts.length == 2) {
                    headers.put(headerParts[0], headerParts[1]);
                }
            }
            return headers;
        } catch (IOException e) {
            System.out.println("Error reading headers.");
            e.printStackTrace();
        }
        return null;
    }
}
