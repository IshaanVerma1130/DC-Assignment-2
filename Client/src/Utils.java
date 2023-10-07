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

    static public void printResponse(WeatherData data) {
        System.out.println("id: " + data.getId());
        System.out.println("name: " + data.getName());
        System.out.println("state: " + data.getState());
        System.out.println("time_zone: " + data.getTime_zone());
        System.out.println("lat: " + data.getLat());
        System.out.println("lon: " + data.getLon());
        System.out.println("local_date_time: " + data.getLocal_date_time());
        System.out.println("local_date_time_full: " + data.getLocal_date_time_full());
        System.out.println("air_temp: " + data.getAir_temp());
        System.out.println("apparent_t: " + data.getApparent_t());
        System.out.println("cloud: " + data.getCloud());
        System.out.println("dewpt: " + data.getDewpt());
        System.out.println("press: " + data.getPress());
        System.out.println("rel_hum: " + data.getRel_hum());
        System.out.println("wind_dir: " + data.getWind_dir());
        System.out.println("wind_spd_kmh: " + data.getWind_spd_kmh());
        System.out.println("wind_spd_kt: " + data.getWind_spd_kt());
        System.out.println();
    }
}
