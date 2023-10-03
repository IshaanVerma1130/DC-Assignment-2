public class Utils {
    public static Response createresponse(WeatherData body) {
        Response res = new Response();
        res.setAir_temp(body.getAir_temp());
        res.setApparent_t(body.getApparent_t());
        res.setCloud(body.getCloud());
        res.setDewpt(body.getDewpt());
        res.setId(body.getId());
        res.setLat(body.getLat());
        res.setLocal_date_time(body.getLocal_date_time());
        res.setLocal_date_time_full(body.getLocal_date_time_full());
        res.setLon(body.getLon());
        res.setName(body.getName());
        res.setPress(body.getPress());
        res.setRel_hum(body.getRel_hum());
        res.setState(body.getState());
        res.setTime_zone(body.getTime_zone());
        res.setWind_dir(body.getWind_dir());
        res.setWind_spd_kmh(body.getWind_spd_kmh());
        res.setWind_spd_kt(body.getWind_spd_kt());

        return res;
    }
}
