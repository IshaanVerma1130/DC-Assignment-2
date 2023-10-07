package src;

public class WeatherData {
    private String id;
    private String name;
    private String state;
    private String time_zone;
    private String lat;
    private String lon;
    private String local_date_time;
    private String local_date_time_full;
    private String air_temp;
    private String apparent_t;
    private String cloud;
    private String dewpt;
    private String press;
    private String rel_hum;
    private String wind_dir;
    private String wind_spd_kmh;
    private String wind_spd_kt;
    private String time_added;

    public WeatherData() {
        this.id = null;
        this.name = null;
        this.state = null;
        this.time_zone = null;
        this.lat = null;
        this.lon = null;
        this.local_date_time = null;
        this.local_date_time_full = null;
        this.air_temp = null;
        this.apparent_t = null;
        this.cloud = null;
        this.dewpt = null;
        this.press = null;
        this.rel_hum = null;
        this.wind_dir = null;
        this.wind_spd_kmh = null;
        this.wind_spd_kt = null;
        this.time_added = null;
    }

    // Getter and Setter for 'id'
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter and Setter for 'name'
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter for 'state'
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    // Getter and Setter for 'time_zone'
    public String getTime_zone() {
        return time_zone;
    }

    public void setTime_zone(String time_zone) {
        this.time_zone = time_zone;
    }

    // Getter and Setter for 'lat'
    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    // Getter and Setter for 'lon'
    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    // Getter and Setter for 'local_date_time'
    public String getLocal_date_time() {
        return local_date_time;
    }

    public void setLocal_date_time(String local_date_time) {
        this.local_date_time = local_date_time;
    }

    // Getter and Setter for 'local_date_time_full'
    public String getLocal_date_time_full() {
        return local_date_time_full;
    }

    public void setLocal_date_time_full(String local_date_time_full) {
        this.local_date_time_full = local_date_time_full;
    }

    // Getter and Setter for 'air_temp'
    public String getAir_temp() {
        return air_temp;
    }

    public void setAir_temp(String air_temp) {
        this.air_temp = air_temp;
    }

    // Getter and Setter for 'apparent_t'
    public String getApparent_t() {
        return apparent_t;
    }

    public void setApparent_t(String apparent_t) {
        this.apparent_t = apparent_t;
    }

    // Getter and Setter for 'cloud'
    public String getCloud() {
        return cloud;
    }

    public void setCloud(String cloud) {
        this.cloud = cloud;
    }

    // Getter and Setter for 'dewpt'
    public String getDewpt() {
        return dewpt;
    }

    public void setDewpt(String dewpt) {
        this.dewpt = dewpt;
    }

    // Getter and Setter for 'press'
    public String getPress() {
        return press;
    }

    public void setPress(String press) {
        this.press = press;
    }

    // Getter and Setter for 'rel_hum'
    public String getRel_hum() {
        return rel_hum;
    }

    public void setRel_hum(String rel_hum) {
        this.rel_hum = rel_hum;
    }

    // Getter and Setter for 'wind_dir'
    public String getWind_dir() {
        return wind_dir;
    }

    public void setWind_dir(String wind_dir) {
        this.wind_dir = wind_dir;
    }

    // Getter and Setter for 'wind_spd_kmh'
    public String getWind_spd_kmh() {
        return wind_spd_kmh;
    }

    public void setWind_spd_kmh(String wind_spd_kmh) {
        this.wind_spd_kmh = wind_spd_kmh;
    }

    // Getter and Setter for 'wind_spd_kt'
    public String getWind_spd_kt() {
        return wind_spd_kt;
    }

    public void setWind_spd_kt(String wind_spd_kt) {
        this.wind_spd_kt = wind_spd_kt;
    }

    // Getter and Setter for 'time_added'
    public String getTime_added() {
        return time_added;
    }

    public void setTime_added(Long timeAdded) {
        this.time_added = Long.toString(timeAdded);
    }
}
