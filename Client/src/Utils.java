package src;

public class Utils {
    static public String generateGetRequest(String url, Integer timestamp, String cityId) {
        String req = "GET /"
                + " HTTP/1.1\r\n"
                + "Host: " + url + "\r\n"
                + "Connection: keep-alive" + "\r\n"
                + "TIME-STAMP: " + timestamp + "\r\n"
                + "CITY-ID: " + cityId + "\r\n" + "\r\n";

        return req;
    }

    static public String printResponse(WeatherData data) {
        String res = "id: " + data.getId() + "\r\n" +
                "name: " + data.getName() + "\r\n" +
                "state: " + data.getState() + "\r\n" +
                "time_zone: " + data.getTime_zone() + "\r\n" +
                "lat: " + data.getLat() + "\r\n" +
                "lon: " + data.getLon() + "\r\n" +
                "local_date_time: " + data.getLocal_date_time() + "\r\n" +
                "local_date_time_full: " + data.getLocal_date_time_full() + "\r\n" +
                "air_temp: " + data.getAir_temp() + "\r\n" +
                "apparent_t: " + data.getApparent_t() + "\r\n" +
                "cloud: " + data.getCloud() + "\r\n" +
                "dewpt: " + data.getDewpt() + "\r\n" +
                "press: " + data.getPress() + "\r\n" +
                "rel_hum: " + data.getRel_hum() + "\r\n" +
                "wind_dir: " + data.getWind_dir() + "\r\n" +
                "wind_spd_kmh: " + data.getWind_spd_kmh() + "\r\n" +
                "wind_spd_kt: " + data.getWind_spd_kt() + "\r\n";
        return res;
    }
}
