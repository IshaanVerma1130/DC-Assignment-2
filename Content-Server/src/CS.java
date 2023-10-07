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
        if (args.length != 3) {
            System.out.println("Usage: java CS <SERVER URL> <PORT> <CS ID>");
            return;
        }

        Logger logger = null;
        String SERVER_URL = args[0];
        Integer PORT = Integer.parseInt(args[1]);
        Integer CS_ID = Integer.parseInt(args[2]);

        try {
            logger = Logger.getLogger(CS.class.getName() + "-" + CS_ID);

            String logFilePath = "logs/logFile-" + CS_ID + ".log";
            File yourFile = new File(logFilePath);
            yourFile.createNewFile();

            Handler logHandler = new FileHandler(logFilePath);

            // Set the desired logging level for the FileHandler
            logHandler.setLevel(Level.INFO);

            // Create a SimpleFormatter to format log messages
            SimpleFormatter formatter = new SimpleFormatter();

            // Assign the formatter to the FileHandler
            logHandler.setFormatter(formatter);

            // Add the FileHandler to the Logger
            logger.addHandler(logHandler);

            logger.info("Log File for CS " + CS_ID);

        } catch (IOException e) {
            System.out.println("Error creating logger. CS " + CS_ID);
            e.printStackTrace();
        }

        int maxRetries = 3;
        int retryCount = 0;

        try {
            List<WeatherData> entries = Utils.generateJson(CS_ID);

            ObjectMapper objectMapper = new ObjectMapper();
            ArrayNode jsonEntries = objectMapper.createArrayNode();

            for (WeatherData entry : entries) {
                ObjectNode jsonEntry = objectMapper.valueToTree(entry);
                jsonEntries.add(jsonEntry);
            }

            if (jsonEntries.isArray()) {
                for (JsonNode jsonObject : jsonEntries) {
                    boolean success = false;

                    while (!success && retryCount < maxRetries) {
                        Socket socket = new Socket(SERVER_URL, PORT);
                        OutputStream out = socket.getOutputStream();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        logger.info("Starting CS " + CS_ID);

                        String requestString = Utils.generatePostRequest(SERVER_URL, clientTimestamp,
                                jsonObject.toString());

                        out.write(requestString.getBytes());
                        out.flush();

                        logger.info("Sending reqeuest to AS.\r\n" + requestString);

                        String responseTag = in.readLine();

                        if (responseTag.equals("OK")) {
                            logger.info("Server processed request immidiately.");
                            processRequest(in);
                            success = true;

                        } else if (responseTag.equals("WAIT")) {
                            logger.info("Waiting for server.");

                            String waitResponse = in.readLine();
                            if (waitResponse.equals("PROCESSING")) {
                                processRequest(in);
                                logger.info("CS updated timestamp after processing request: " + clientTimestamp);
                                success = true;
                            }
                        } else {
                            logger.severe("Error processing request.");
                            retryCount++;
                        }

                        out.close();
                        in.close();
                        socket.close();
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
        logger.info("CS " + CS_ID + " shutting down.");
    }

    private static void updateClientTimestamp(int serverTimestamp) {
        clientTimestamp = Math.max(clientTimestamp, serverTimestamp) + 1;
    }

    private static void processRequest(BufferedReader in) {
        Map<String, String> headers = getHeaders(in);

        int serverTimestamp = Integer.parseInt(headers.getOrDefault("TIME-STAMP", "0"));
        updateClientTimestamp(serverTimestamp);
    }

    private static Map<String, String> getHeaders(BufferedReader in) {
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