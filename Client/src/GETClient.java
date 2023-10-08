package src;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.Handler;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GETClient {
    static int clientTimestamp = 0;

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java GETClient <SERVER URL> <PORT> <CLIENT ID>");
            return;
        }

        Logger logger = null;
        String SERVER_URL = args[0];
        Integer PORT = Integer.parseInt(args[1]);
        Integer CLIENT_ID = Integer.parseInt(args[2]);

        try {
            logger = Logger.getLogger(GETClient.class.getName() + "-" + CLIENT_ID);

            String logFilePath = "logs/logFile-" + CLIENT_ID + ".log";
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

            logger.info("Log File for Client " + CLIENT_ID + "\r\n");

        } catch (IOException e) {
            System.out.println("Error creating logger. Client " + CLIENT_ID);
            e.printStackTrace();
        }

        int maxRetries = 3;
        int retryCount = 0;
        String input;

        try {
            String fileDirectory = "resources/";
            String fileName = "Client-" + CLIENT_ID + ".txt";
            BufferedReader br = new BufferedReader(new FileReader(fileDirectory + fileName));

            while ((input = br.readLine()) != null) {
                input = input.trim();
                boolean success = false;

                while (!success && retryCount < maxRetries) {
                    Socket socket = new Socket(SERVER_URL, PORT);
                    OutputStream out = socket.getOutputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    String requestString = Utils.generateGetRequest(SERVER_URL, clientTimestamp, input);

                    out.write(requestString.getBytes());
                    out.flush();

                    logger.info("\r\nSending reqeuest to AS.\r\n" + requestString);

                    String responseTag = in.readLine();

                    if (responseTag.equals("OK")) {
                        logger.info("Server processed request immidiately.\r\n");
                        processRequest(in, logger);
                        success = true;

                    } else if (responseTag.equals("WAIT")) {
                        logger.info("Waiting for server.\r\n");

                        String waitResponse = in.readLine();
                        if (waitResponse.equals("PROCESSING")) {
                            processRequest(in, logger);
                            logger.info(
                                    "Client updated timestamp after processing request: " + clientTimestamp + "\r\n");
                            success = true;
                        }
                    } else {
                        logger.severe("Error processing request.");
                        retryCount++;
                    }

                    out.close();
                    in.close();
                    socket.close();

                    try {
                        Thread.sleep(1000); // Sleep for 1000 milliseconds (1 second)
                    } catch (InterruptedException e) {
                        // Handle the InterruptedException if needed
                    }
                }

                if (!success) {
                    logger.severe("Maximum retries reached.");
                }
                retryCount = 0;
            }
            br.close();
        } catch (ConnectException e) {
            logger.severe(e.toString());
        } catch (FileNotFoundException e) {
            logger.severe(e.toString());
        } catch (IOException e) {
            logger.severe(e.toString());
        }
        logger.info("Shutting down Client " + CLIENT_ID);
    }

    private static void updateClientTimestamp(int serverTimestamp) {
        clientTimestamp = Math.max(clientTimestamp, serverTimestamp) + 1;
    }

    private static void processRequest(BufferedReader in, Logger logger) {
        Map<String, String> headers = getHeaders(in);

        int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));

        WeatherData responseObject = getJsonData(in, contentLength);
        String printString = Utils.printResponse(responseObject);
        System.out.println(printString);

        logger.info("Response from server:\r\n" + printString);

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

    private static WeatherData getJsonData(BufferedReader in, int contentLength) {
        StringBuilder jsonResponse = new StringBuilder();
        int totalRead = 0;
        try {
            while (totalRead < contentLength) {
                int bytesRead = in.read();
                if (bytesRead == -1) {
                    break;
                }
                jsonResponse.append((char) bytesRead);
                totalRead++;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            WeatherData responseObject = objectMapper.readValue(jsonResponse.toString(),
                    WeatherData.class);

            return responseObject;
        } catch (JsonMappingException e) {
            System.out.println("Error while mapping JSON to object.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error reading JSON data.");
            e.printStackTrace();
        }
        return null;
    }
}