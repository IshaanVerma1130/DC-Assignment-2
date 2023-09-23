import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class AggregationServer {
    private static long lamportTimestamp = 1;
    private static int NUM_THREADS = 10;
    private static final BlockingQueue<ClientRequest> requestQueue = new LinkedBlockingQueue<>();
    private static final Lock lamportLock = new ReentrantLock();
    private static final Condition queueNotEmpty = lamportLock.newCondition();

    public static void main(String[] args) {
        int PORT = 4567;

        if (args.length > 0 && args.length == 1) {
            PORT = Integer.parseInt(args[0]);
        }

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server listening on port " + PORT);
            for (int i = 0; i < NUM_THREADS; i++) {
                Thread processorThread = new Thread(new RequestProcessor());
                processorThread.start();
            }
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    static class ClientRequest {
        String type;
        long timestamp;
        String data;
        Socket clientSocket;

        ClientRequest(String type, long timestamp, String data, Socket clientSocket) {
            this.type = type;
            this.timestamp = timestamp;
            this.data = data;
            this.clientSocket = clientSocket;
        }
    }

    static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            BufferedReader in = null;
            try {
                System.out.println("New connection");
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String inputLine;
                StringBuilder request = new StringBuilder();
                String requestMethod = "";
                Map<String, String> headers = new HashMap<>();

                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.isEmpty()) {
                        break;
                    }
                    request.append(inputLine).append("\r\n");

                    if (requestMethod.isEmpty()) {
                        String[] parts = inputLine.split(" ");
                        if (parts.length >= 1) {
                            requestMethod = parts[0].trim();
                        }
                    }

                    String[] headerParts = inputLine.split(": ");
                    if (headerParts.length == 2) {
                        headers.put(headerParts[0], headerParts[1]);
                    }
                }

                long clientTimestamp = Integer.parseInt(headers.get("TIME-STAMP"));

                lamportLock.lock();
                lamportTimestamp++;

                if ("GET".equalsIgnoreCase(requestMethod)) {
                    String id = headers.get("CITY-ID");
                    handleGetRequest(lamportTimestamp, clientTimestamp, id, clientSocket);
                } else if ("PUT".equalsIgnoreCase(requestMethod)) {
                    char[] body = new char[Integer.parseInt(headers.get("Content-Length"))];
                    in.read(body);
                    String jsonRequest = new String(body);
                    handlePutRequest(lamportTimestamp, clientTimestamp, jsonRequest, clientSocket);
                }
            } catch (SocketException e) {
                System.out.println("Connection to socket closed unexpectedly.");
                try {
                    in.close();
                    clientSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                lamportLock.unlock();
            }
        }
    }

    private static void handleGetRequest(long lamportTimestamp, long clientTimestamp, String id,
            Socket clientSocket) {
        try {
            OutputStream out = clientSocket.getOutputStream();

            if (lamportTimestamp >= clientTimestamp) {
                String response = Modifier.getEntry(id);
                String jsonResponse = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: application/json\r\n" +
                        "TIME-STAMP: " + lamportTimestamp + "\r\n" +
                        "Content-Length: " + response.length() + "\r\n\r\n" +
                        response;
                out.write("OK\r\n".getBytes());
                out.write(jsonResponse.getBytes());
                out.close();
                clientSocket.close();
            } else {
                out.write("WAIT\r\n".getBytes());
                enqueueRequest("GET", lamportTimestamp, id, clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handlePutRequest(long timestamp, long clientTimestamp, String jsonRequest,
            Socket clientSocket) {
        try {
            OutputStream out = clientSocket.getOutputStream();
            if (timestamp >= clientTimestamp) {
                Modifier.putEntry(jsonRequest);
                String jsonResponse = "HTTP/1.1 200 OK\r\n" +
                        "TIME-STAMP: " + lamportTimestamp + "\r\n";
                out.write("OK\r\n".getBytes());
                out.write(jsonResponse.getBytes());
                out.close();
                clientSocket.close();
            } else {
                out.write("WAIT\r\n".getBytes());
                enqueueRequest("PUT", timestamp, jsonRequest, clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void enqueueRequest(String type, long timestamp, String data, Socket clientSocket) {
        ClientRequest request = new ClientRequest(type, timestamp, data, clientSocket);
        requestQueue.add(request);

        lamportLock.lock();
        queueNotEmpty.signalAll();
        lamportLock.unlock();
    }

    static class RequestProcessor implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    lamportLock.lock();
                    while (requestQueue.isEmpty()) {
                        queueNotEmpty.await();
                    }
                    try {
                        ClientRequest request = requestQueue.take();

                        if (request != null) {
                            if (request.type.equals("GET")) {
                                processGetRequest(request);
                            } else if (request.type.equals("PUT")) {
                                processPutRequest(request);
                            }
                        }
                    } finally {
                        lamportLock.unlock();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }

        private void processGetRequest(ClientRequest request) {
            String id = request.data;
            try {
                String response = Modifier.getEntry(id);
                String jsonResponse = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: application/json\r\n" +
                        "TIME-STAMP: " + lamportTimestamp + "\r\n" +
                        "Content-Length: " + response.length() + "\r\n\r\n" +
                        response;
                OutputStream out = request.clientSocket.getOutputStream();
                out.write("PROCESSING\r\n".getBytes());
                out.write(jsonResponse.getBytes());
                out.close();
                request.clientSocket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void processPutRequest(ClientRequest request) {
            String jsonRequest = request.data;
            try {
                Modifier.putEntry(jsonRequest);
                String jsonResponse = "HTTP/1.1 200 OK\r\n" +
                        "TIME-STAMP: " + lamportTimestamp + "\r\n";
                OutputStream out = request.clientSocket.getOutputStream();
                out.write("PROCESSING\r\n".getBytes());
                out.write(jsonResponse.getBytes());
                out.close();
                request.clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
