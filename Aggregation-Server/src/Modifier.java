import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Modifier {
    static ObjectMapper objectMapper = new ObjectMapper();
    private static final String DATA_FILE_PATH = "resources/data.json";
    private static final String TEMP_FILE_PATH = "resources/temp_data.json";

    public static void putEntry(String req) {
        try {
            File dataFile = new File(DATA_FILE_PATH);
            File tempFile = new File(TEMP_FILE_PATH);

            try {
                if (tempFile.createNewFile()) {
                    System.out.println("Temp file created successfully for PUT.");
                } else {
                    System.out.println("File already exists.");
                }
            } catch (IOException e) {
                System.err.println("An error occurred while creating the file: " + e.getMessage());
            }

            List<WeatherData> weatherDataList = objectMapper.readValue(dataFile,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, WeatherData.class));

            WeatherData body = objectMapper.readValue(req, WeatherData.class);
            long currentTimeMillis = System.currentTimeMillis();
            body.setTime_added(currentTimeMillis);

            Parser.put(weatherDataList, body);
            objectMapper.writeValue(tempFile, weatherDataList);

            if (tempFile.renameTo(dataFile)) {
                System.out.println("Data updated successfully.");
            } else {
                System.err.println("Failed to update data.");
            }
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getEntry(String id) {
        String response = null;
        try {

            File dataFile = new File(DATA_FILE_PATH);

            List<WeatherData> weatherDataList = objectMapper.readValue(dataFile,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, WeatherData.class));

            WeatherData result = Parser.get(weatherDataList, id);
            response = objectMapper.writeValueAsString(Utils.createresponse(result));

        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static void removeOldData() {
        try {
            File dataFile = new File(DATA_FILE_PATH);
            File tempFile = new File(TEMP_FILE_PATH);

            try {
                if (tempFile.createNewFile()) {
                    System.out.println("Temp file created successfully for Deletion.");
                } else {
                    System.out.println("File already exists.");
                }
            } catch (IOException e) {
                System.err.println("An error occurred while creating the file: " + e.getMessage());
            }

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

            objectMapper.writeValue(tempFile, newWeatherDataList);

            if (tempFile.renameTo(dataFile)) {
                System.out.println("Data updated successfully.");
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
