import java.io.IOException;
import java.util.List;
import java.io.File;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Modifier {
    static ObjectMapper objectMapper = new ObjectMapper();

    public static void putEntry(String req) {
        try {
            File dataFile = new File("resources/data.json");
            WeatherData body = objectMapper.readValue(req, WeatherData.class);

            List<WeatherData> weatherDataList = objectMapper.readValue(dataFile,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, WeatherData.class));

            Parser.put(weatherDataList, body);
            objectMapper.writeValue(dataFile, weatherDataList);

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

            File dataFile = new File("resources/data.json");

            List<WeatherData> weatherDataList = objectMapper.readValue(dataFile,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, WeatherData.class));

            WeatherData result = Parser.get(weatherDataList, id);
            response = objectMapper.writeValueAsString(result);

        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
