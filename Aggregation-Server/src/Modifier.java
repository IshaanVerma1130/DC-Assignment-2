package src;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.io.File;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Modifier {
    static ObjectMapper objectMapper = new ObjectMapper();
    private static final String DATA_FILE_PATH = "resources/data.json";
    private static final String TEMP_FILE_PATH = "resources/temp_data.json";

    // Method to add a new entry to the data
    public static void putEntry(String req) {
        try {
            File dataFile = new File(DATA_FILE_PATH);
            File tempFile = new File(TEMP_FILE_PATH);

            tempFile.createNewFile();

            // Read the existing data from the data file
            List<WeatherData> weatherDataList = objectMapper.readValue(dataFile,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, WeatherData.class));

            // Deserialize the request body and add a timestamp
            WeatherData body = objectMapper.readValue(req, WeatherData.class);
            long currentTimeMillis = System.currentTimeMillis();
            body.setTime_added(currentTimeMillis);

            // Add the new data to the list and write it back to the data file
            Parser.put(weatherDataList, body);
            objectMapper.writeValue(tempFile, weatherDataList);

            // Rename the temp file to replace the original data file
            tempFile.renameTo(dataFile);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to retrieve an entry based on the given ID
    public static String getEntry(String id) {
        String response = null;
        try {
            Response tempObject = new Response();
            tempObject.setId(id);
            response = objectMapper.writeValueAsString(tempObject);

            File dataFile = new File(DATA_FILE_PATH);

            // Read the existing data from the data file
            List<WeatherData> weatherDataList = objectMapper.readValue(dataFile,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, WeatherData.class));

            // Get the requested data and format the response
            WeatherData result = Parser.get(weatherDataList, id);

            if (result != null) {
                response = objectMapper.writeValueAsString(Utils.createresponse(result));
            }
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    // Method to get the current timestamp in a specific format
    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm:ss a");
        Date currentDate = new Date();
        String formattedDate = sdf.format(currentDate);

        return formattedDate;
    }

    // Method to remove old data entries based on a time threshold
    public static void removeOldData() {
        try {
            System.out.println(getCurrentTime());
            File dataFile = new File(DATA_FILE_PATH);
            File tempFile = new File(TEMP_FILE_PATH);

            tempFile.createNewFile();

            // Read the existing data from the data file
            List<WeatherData> weatherDataList = objectMapper.readValue(dataFile,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, WeatherData.class));

            long currentTimeMillis = System.currentTimeMillis();
            long thresholdMillis = 30 * 1000; // 30 seconds in milliseconds

            ArrayList<WeatherData> newWeatherDataList = new ArrayList<WeatherData>();

            // Iterate through the data and remove items older than 30 seconds
            Iterator<WeatherData> iterator = weatherDataList.iterator();
            while (iterator.hasNext()) {
                WeatherData weatherData = iterator.next();
                long timeAddedMillis = Long.parseLong(weatherData.getTime_added());
                if (timeAddedMillis != 0 && (currentTimeMillis - timeAddedMillis) > thresholdMillis) {
                    System.out.println("Removed old data from the data storage with City ID: " + weatherData.getId());
                } else {
                    newWeatherDataList.add(weatherData);
                }
            }

            // Write the filtered data to the temp file and replace the original data file
            objectMapper.writeValue(tempFile, newWeatherDataList);

            if (tempFile.renameTo(dataFile)) {
                System.out.println("Periodic deletion complete.\r\n");
            } else {
                System.err.println("Failed to delete data.");
            }

        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
