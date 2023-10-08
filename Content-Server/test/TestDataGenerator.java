package test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class TestDataGenerator {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java TestDataGenerator <CS ID>");
        }

        String CS_ID = args[0];

        String[] ids = { "IDS60901", "IDS60902", "IDS60903", "IDS60904", "IDS60905", "IDS60906", "IDS60907", "IDS60908",
                "IDS60909", "IDS60910" };
        String[] timeZones = { "CST", "EST", "PST", "GMT", "JST", "IST", "ECT", "AEDT" };
        Map<String, String> idStateMap = createIdStateMap();
        Map<String, String> idCityMap = createIdCityMap();

        try {
            PrintWriter writer = new PrintWriter(new FileWriter("resources/CS-" + CS_ID + ".txt"));

            for (String id : ids) {
                String state = idStateMap.get(id);
                String city = idCityMap.get(id);

                writer.println("id: " + id);
                writer.println("name: " + city);
                writer.println("state: " + state);
                writer.println("time_zone: " + getRandomTimeZone(timeZones));
                writer.println("lat: " + getRandomDouble(-90, 90));
                writer.println("lon: " + getRandomDouble(-180, 180));
                writer.println("local_date_time: " + getRandomDateTime());
                writer.println("local_date_time_full: " + getRandomFullDateTime());
                writer.println("air_temp: " + getRandomDouble(-10, 40));
                writer.println("apparent_t: " + getRandomDouble(-10, 40));
                writer.println("cloud: " + getRandomCloudCondition());
                writer.println("dewpt: " + getRandomDouble(-10, 30));
                writer.println("press: " + getRandomDouble(900, 1100));
                writer.println("rel_hum: " + getRandomInt(0, 100));
                writer.println("wind_dir: " + getRandomWindDirection());
                writer.println("wind_spd_kmh: " + getRandomInt(0, 30));
                writer.println("wind_spd_kt: " + getRandomInt(0, 20));
            }

            writer.close();
            System.out.println("Test data generated successfully for CS " + CS_ID);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static double getRandomDouble(double min, double max) {
        return min + Math.random() * (max - min);
    }

    private static int getRandomInt(int min, int max) {
        return (int) (min + Math.random() * (max - min + 1));
    }

    private static String getRandomTimeZone(String[] timeZones) {
        int randomIndex = getRandomInt(0, timeZones.length - 1);
        return timeZones[randomIndex];
    }

    private static String getRandomDateTime() {
        return getRandomInt(1, 31) + "/" + getRandomInt(1, 12) + ":" + getRandomInt(0, 59) + "pm";
    }

    private static String getRandomFullDateTime() {
        return "2023" + getRandomInt(1, 12) + getRandomInt(100, 999) +
                getRandomInt(0, 23) + getRandomInt(0, 59) + getRandomInt(0, 59);
    }

    private static String getRandomCloudCondition() {
        String[] conditions = { "Clear", "Partly cloudy", "Mostly cloudy", "Overcast" };
        int randomIndex = getRandomInt(0, conditions.length - 1);
        return conditions[randomIndex];
    }

    private static String getRandomWindDirection() {
        String[] directions = { "N", "NE", "E", "SE", "S", "SW", "W", "NW" };
        int randomIndex = getRandomInt(0, directions.length - 1);
        return directions[randomIndex];
    }

    private static Map<String, String> createIdStateMap() {
        Map<String, String> idStateMap = new HashMap<>();
        idStateMap.put("IDS60901", "SA");
        idStateMap.put("IDS60902", "NSW");
        idStateMap.put("IDS60903", "QLD");
        idStateMap.put("IDS60904", "VIC");
        idStateMap.put("IDS60905", "WA");
        idStateMap.put("IDS60906", "SA");
        idStateMap.put("IDS60907", "NSW");
        idStateMap.put("IDS60908", "QLD");
        idStateMap.put("IDS60909", "VIC");
        idStateMap.put("IDS60910", "WA");
        return idStateMap;
    }

    private static Map<String, String> createIdCityMap() {
        Map<String, String> idCityMap = new HashMap<>();
        idCityMap.put("IDS60901", "Adelaide");
        idCityMap.put("IDS60902", "Sydney");
        idCityMap.put("IDS60903", "Brisbane");
        idCityMap.put("IDS60904", "Melbourne");
        idCityMap.put("IDS60905", "Perth");
        idCityMap.put("IDS60906", "Mount Gambier");
        idCityMap.put("IDS60907", "Newcastle");
        idCityMap.put("IDS60908", "Gold Coast");
        idCityMap.put("IDS60909", "Geelong");
        idCityMap.put("IDS60910", "Fremantle");
        return idCityMap;
    }
}