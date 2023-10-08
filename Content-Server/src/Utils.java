package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    static public String generatePostRequest(String url, Integer timestamp, String jsonString) {
        String req = "PUT /"
                + " HTTP/1.1\r\n"
                + "Host: " + url + "\r\n"
                + "Connection: keep-alive" + "\r\n"
                + "TIME-STAMP: " + timestamp + "\r\n"
                + "Content-Type: application/json\r\n"
                + "Content-Length: " + jsonString.length() + "\r\n\r\n"
                + jsonString;

        return req;
    }

    public static List<WeatherData> generateJson(Integer CS_ID) throws IOException {
        String fileDirectory = "resources/";
        String fileName = "CS-" + CS_ID + ".txt";

        List<WeatherData> entries = new ArrayList<>();
        WeatherData currentEntry = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(fileDirectory + fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();

                    if ("id".equals(key)) {
                        if (currentEntry != null) {
                            entries.add(currentEntry);
                        }
                        currentEntry = new WeatherData();
                        currentEntry.setId(value);
                    } else if (currentEntry != null) {
                        setField(currentEntry, key, value);
                    }
                }
            }

            if (currentEntry != null) {
                entries.add(currentEntry);
            }
        }

        return entries;
    }

    private static void setField(WeatherData entry, String key, String value) {
        switch (key) {
            case "name":
                entry.setName(value);
                break;
            case "state":
                entry.setState(value);
                break;
            case "time_zone":
                entry.setTime_zone(value);
                break;
            case "lat":
                entry.setLat(value);
                break;
            case "lon":
                entry.setLon(value);
                break;
            case "local_date_time":
                entry.setLocal_date_time(value);
                break;
            case "local_date_time_full":
                entry.setLocal_date_time_full(value);
                break;
            case "air_temp":
                entry.setAir_temp(value);
                break;
            case "apparent_t":
                entry.setApparent_t(value);
                break;
            case "cloud":
                entry.setCloud(value);
                break;
            case "dewpt":
                entry.setDewpt(value);
                break;
            case "press":
                entry.setPress(value);
                break;
            case "rel_hum":
                entry.setRel_hum(value);
                break;
            case "wind_dir":
                entry.setWind_dir(value);
                break;
            case "wind_spd_kmh":
                entry.setWind_spd_kmh(value);
                break;
            case "wind_spd_kt":
                entry.setWind_spd_kt(value);
                break;
            default:
                // Handle unknown fields or ignore them
                break;
        }
    }

}
