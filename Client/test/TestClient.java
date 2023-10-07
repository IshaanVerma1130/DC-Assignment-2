package test;

import java.io.File;
import java.io.IOException;

import src.GETClient;

public class TestClient {
    public static void main(String[] args) {
        int numberOfClient = 8;
        String[][] clientArgs = new String[numberOfClient][3];

        String SERVER_URL = "localhost";
        String PORT = "4567";

        for (int i = 1; i <= numberOfClient; i++) {
            String[] arr = { SERVER_URL, PORT, Integer.toString(i) };
            clientArgs[i - 1] = arr;

            try {
                String resourceFilePath = "resources/Client-" + i + ".txt";
                File yourFile = new File(resourceFilePath);
                yourFile.createNewFile();

                String[] genArr = { Integer.toString(i) };
                TestDataGenerator.main(genArr);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Thread[] threads = new Thread[numberOfClient];

        for (int i = 0; i < numberOfClient; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                GETClient.main(clientArgs[index]);
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
