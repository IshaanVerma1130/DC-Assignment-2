package src;

import java.util.List;

public class Parser {
    // Method to update or add a new WeatherData entry in the list
    public static void put(List<WeatherData> weatherDataList, WeatherData body) {
        boolean found = false;
        for (WeatherData entry : weatherDataList) {
            if (entry.getId().equals(body.getId())) {
                found = true;
                // Update the fields of the existing entry with non-null values from the request
                if (body.getAir_temp() != null) {
                    entry.setAir_temp(body.getAir_temp());
                }
                if (body.getApparent_t() != null) {
                    entry.setApparent_t(body.getApparent_t());
                }
                if (body.getCloud() != null) {
                    entry.setCloud(body.getCloud());
                }
                if (body.getDewpt() != null) {
                    entry.setDewpt(body.getDewpt());
                }
                if (body.getLat() != null) {
                    entry.setLat(body.getLat());
                }
                if (body.getLocal_date_time() != null) {
                    entry.setLocal_date_time(body.getLocal_date_time());
                }
                if (body.getLocal_date_time_full() != null) {
                    entry.setLocal_date_time_full(body.getLocal_date_time_full());
                }
                if (body.getLon() != null) {
                    entry.setLon(body.getLon());
                }
                if (body.getName() != null) {
                    entry.setName(body.getName());
                }
                if (body.getPress() != null) {
                    entry.setPress(body.getPress());
                }
                if (body.getRel_hum() != null) {
                    entry.setRel_hum(body.getRel_hum());
                }
                if (body.getState() != null) {
                    entry.setState(body.getState());
                }
                if (body.getTime_zone() != null) {
                    entry.setTime_zone(body.getTime_zone());
                }
                if (body.getWind_dir() != null) {
                    entry.setWind_dir(body.getWind_dir());
                }
                if (body.getWind_spd_kmh() != null) {
                    entry.setWind_spd_kmh(body.getWind_spd_kmh());
                }
                if (body.getWind_spd_kt() != null) {
                    entry.setWind_spd_kt(body.getWind_spd_kt());
                }
                // This loop ensures that if an entry with the same ID exists, it is updated.
                break; // Exit the loop after updating the entry
            }
        }
        if (!found) {
            // If no existing entry with the same ID was found, add the new entry to the
            // list
            weatherDataList.add(body);
        }
    }

    // Method to retrieve a WeatherData entry based on the provided ID
    public static WeatherData get(List<WeatherData> weatherDataList, String id) {
        for (WeatherData entry : weatherDataList) {
            if (entry.getId().equals(id)) {
                // Return the entry with the matching ID
                return entry;
            }
        }

        // If no matching entry is found, return null
        return null;
    }
}
