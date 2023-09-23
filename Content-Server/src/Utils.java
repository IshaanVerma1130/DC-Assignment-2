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

}
