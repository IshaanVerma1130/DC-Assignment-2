import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        String SERVER_URL = args[0];
        Integer PORT = Integer.parseInt(args[1]);
        Integer CS_ID = Integer.parseInt(args[2]);

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

                        String requestString = Utils.generatePostRequest(SERVER_URL, clientTimestamp,
                                jsonObject.toString());

                        out.write(requestString.getBytes());
                        out.flush();

                        String responseTag = in.readLine();

                        if (responseTag.equals("OK")) {
                            System.out.println("Server processed request.");
                            processRequest(in);
                            success = true;
                        } else if (responseTag.equals("WAIT")) {
                            System.out.println("Waiting for server response...");

                            String waitResponse = in.readLine();
                            if (waitResponse.equals("PROCESSING")) {
                                System.out.println("Server processed request.");
                                processRequest(in);
                            }
                            success = true;
                        } else {
                            System.out.println("Error response received. Retrying...");
                            retryCount++;
                        }

                        out.close();
                        in.close();
                        socket.close();
                    }

                    if (!success) {
                        System.out.println("Maximum retries reached. Unable to process request.");
                    }
                    retryCount = 0;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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