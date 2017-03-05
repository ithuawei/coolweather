package coolweather.android.com.coolweather.gson;

/**
 * 包含多个同样的，是个集合，所以等下Gson要弄成集合
 * */

import com.google.gson.annotations.SerializedName;

/**
 * daily_forecast: [ { astro: { mr: "11:42", ms: "00:10", sr: "06:41", ss: "18:28" }, cond: { code_d: "101", code_n:
 * "300", txt_d: "多云", txt_n: "阵雨" }, date: "2017-03-05", hum: "84", pcpn: "0.2", pop: "100", pres: "1013", tmp: { max:
 * "25", min: "18" }, uv: "9", vis: "8", wind: { deg: "117", dir: "无持续风向", sc: "微风", spd: "8" } }, { astro: {
 * mr:"12:35", ms: "01:11", sr: "06:40", ss: "18:29" }, cond: { code_d: "305", code_n: "305", txt_d: "小雨", txt_n: "小雨"
 * }, date: "2017-03-06", hum: "76", pcpn: "0.1", pop: "53", pres: "1014", tmp: { max: "24", min: "17" }, uv: "9", vis:
 * "8", wind: { deg: "74", dir: "无持续风向", sc: "微风", spd: "5" } }, { astro: { mr: "13:32", ms: "02:09", sr: "06:39", ss:
 * "18:29" }, cond: { code_d: "305", code_n: "305", txt_d: "小雨", txt_n: "小雨" }, date: "2017-03-07", hum: "73", pcpn:
 * "0.6", pop: "47", pres: "1018", tmp: { max: "22", min: "16" }, uv: "10", vis: "8", wind: { deg: "67", dir: "东北风", sc:
 * "3-4", spd: "10" } } ],
 */
public class Forecast {
    public String date;
    @SerializedName("tmp")
    public Temperature mTemperature;
    @SerializedName("cond")
    public More mMore;

    public class Temperature {
        public String max;
        public String min;
    }
    public class More {
        @SerializedName("txt_d")
        public String info;
    }
}
