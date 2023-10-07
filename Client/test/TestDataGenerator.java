package test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class TestDataGenerator {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java TestDataGenerator <Client ID>");
        }

        String CLIENT_ID = args[0];

        String[] ids = { "IDS60901", "IDS60902", "IDS60903", "IDS60904", "IDS60905", "IDS60906", "IDS60907", "IDS60908",
                "IDS60909", "IDS60910" };

        try {
            PrintWriter writer = new PrintWriter(new FileWriter("resources/Client-" + CLIENT_ID + ".txt"));

            for (int i = 0; i < 20; i++) {
                int rnd = new Random().nextInt(ids.length);
                writer.println(ids[rnd]);
            }

            writer.close();
            System.out.println("Test data generated successfully for Client " + CLIENT_ID);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}