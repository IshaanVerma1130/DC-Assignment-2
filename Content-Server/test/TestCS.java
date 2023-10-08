package test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import src.CS;

public class TestCS {
    public static void main(String[] args) {
        int numberOfCS = 8;

        if (args.length == 1) {
            numberOfCS = Integer.parseInt(args[0]);
        }

        int numThreads = 6;

        String SERVER_URL = "localhost";
        String PORT = "4567";

        ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);

        for (int i = 1; i <= numberOfCS; i++) {
            String[] arr = { SERVER_URL, PORT, Integer.toString(i) };

            try {
                String resourceFilePath = "resources/CS-" + i + ".txt";
                File yourFile = new File(resourceFilePath);
                yourFile.createNewFile();

                String[] genArr = { Integer.toString(i) };
                TestDataGenerator.main(genArr);

            } catch (IOException e) {
                e.printStackTrace();
            }
            threadPool.submit(() -> CS.main(arr));
        }
        threadPool.shutdown();
    }
}
