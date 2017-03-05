package coolweather.android.com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * basic: { city: "深圳", cnty: "中国", id: "CN101280601", lat: "22.544000", lon: "114.109000", update: { loc:
 * "2017-03-05 17:49", utc: "2017-03-05 09:49" } },
 */
public class Basic {
    //以前简单的字段直接用city 然后同样的set和get，这里为了更好的阅读采用序列化名字“转换”和识别建立了映射关系。
    @SerializedName("city")
	public String cityName;

    @SerializedName("id")
    public String weatherId;

    @SerializedName("update")
    public Update mUpdate;

    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }

}
