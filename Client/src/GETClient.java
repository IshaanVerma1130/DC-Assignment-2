import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GETClient {
    static int clientTimestamp = 0;

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java GETClient <SERVER URL> <PORT> <CLIENT ID>");
            return;
        }

        String SERVER_URL = args[0];
        Integer PORT = Integer.parseInt(args[1]);
        Integer CLIENT_ID = Integer.parseInt(args[2]);

        // String SERVER_URL = "localhost";
        // Integer PORT = 4567;
        // Integer CLIENT_ID = 1;

        String fileDirectory = "resources/";
        String fileName = "Client-" + CLIENT_ID + ".txt";

        String input;
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileDirectory + fileName));
            while ((input = br.readLine()) != null) {
                input = input.trim();

                Socket socket = new Socket(SERVER_URL, PORT);
                OutputStream out = socket.getOutputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String requestString = Utils.generateGetRequest(SERVER_URL, clientTimestamp, input);

                out.write(requestString.getBytes());
                out.flush();

                String responseTag = in.readLine();

                if (responseTag.equals("OK")) {
                    System.out.println("Server processed request.");
                    processRequest(in);

                } else if (responseTag.equals("WAIT")) {
                    System.out.println("Waiting for server response...");

                    String waitResponse = in.readLine();
                    if (waitResponse.equals("PROCESSING")) {
                        System.out.println("Server processed request.");
                        processRequest(in);
                    }
                }
                out.close();
                in.close();
                socket.close();
            }
            br.close();
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

        int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));

        WeatherData responseObject = getJsonData(in, contentLength);
        Utils.printResponse(responseObject);

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