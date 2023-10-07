package src;

import java.io.*;
import java.net.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.Handler;

public class AggregationServer {
    private static long lamportTimestamp = 1;
    private static int NUM_THREADS = 10;
    private static final PriorityBlockingQueue<ClientRequest> requestQueue = new PriorityBlockingQueue<>(10,
            new Comparator<ClientRequest>() {
                @Override
                public int compare(ClientRequest request1, ClientRequest request2) {
                    return Long.compare(request1.timestamp, request2.timestamp);
                }
            });

    private static final Lock lamportLock = new ReentrantLock();
    private static final Condition queueNotEmpty = lamportLock.newCondition();
    private static Logger logger = null;

    public static void main(String[] args) {
        int PORT = 4567;

        try {
            logger = Logger.getLogger(AggregationServer.class.getName());

            String logFilePath = "logs/logFile.log";
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

            logger.info("Log File for Aggregation Server");

        } catch (IOException e) {
            System.out.println("Error creating logger.");
            e.printStackTrace();
        }

        if (args.length > 0 && args.length == 1) {
            PORT = Integer.parseInt(args[0]);
        }

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        ScheduledExecutorService deletionScheduler = Executors.newScheduledThreadPool(1);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("Server listening on port " + PORT);
            for (int i = 0; i < NUM_THREADS; i++) {
                Thread processorThread = new Thread(new RequestProcessor());
                processorThread.start();
                logger.info("Starting processing thread No. " + i);
            }

            deletionScheduler.scheduleAtFixedRate(AggregationServer::cleanupOldRequests, 0, 45, TimeUnit.SECONDS);
            logger.info("Starting periodic deletion thread");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.severe(e.getMessage());
        } finally {
            executor.shutdown();
        }
    }

    private static void cleanupOldRequests() {
        lamportLock.lock();
        try {
            Modifier.removeOldData();
        } finally {
            lamportLock.unlock();
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
                    logger.info("New Client Connection. Requested City: " + id);
                    handleGetRequest(clientTimestamp, id, clientSocket);
                } else if ("PUT".equalsIgnoreCase(requestMethod)) {
                    char[] body = new char[Integer.parseInt(headers.get("Content-Length"))];
                    in.read(body);
                    String jsonRequest = new String(body);
                    logger.info("New CS Connection. JSON Body:\r\n" + jsonRequest);
                    handlePutRequest(clientTimestamp, jsonRequest, clientSocket);
                }
            } catch (SocketException e) {
                logger.severe("Connection to socket closed unexpectedly.\r\n" + e.getMessage());
                try {
                    in.close();
                    clientSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
                logger.severe(e.getMessage());
            } finally {
                lamportLock.unlock();
            }
        }
    }

    private static void handleGetRequest(long clientTimestamp, String id,
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
                logger.info("Response sent to Client immidiately");
            } else {
                out.write("WAIT\r\n".getBytes());
                enqueueRequest("GET", clientTimestamp, id, clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handlePutRequest(long clientTimestamp, String jsonRequest,
            Socket clientSocket) {
        try {
            OutputStream out = clientSocket.getOutputStream();
            if (lamportTimestamp >= clientTimestamp) {
                Modifier.putEntry(jsonRequest);
                String jsonResponse = "HTTP/1.1 200 OK\r\n" +
                        "TIME-STAMP: " + lamportTimestamp + "\r\n";
                out.write("OK\r\n".getBytes());
                out.write(jsonResponse.getBytes());
                out.close();
                clientSocket.close();
                logger.info("Response sent to CS immidiately");
            } else {
                out.write("WAIT\r\n".getBytes());
                enqueueRequest("PUT", clientTimestamp, jsonRequest, clientSocket);
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
                logger.info("Response sent to Client after queueing");
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
                logger.info("Response sent to CS after queueing");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
