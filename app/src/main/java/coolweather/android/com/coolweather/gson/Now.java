package coolweather.android.com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * now: { cond: { code: "101", txt: "多云" }, fl: "25", hum: "78", pcpn: "0", pres: "1010", tmp: "24", vis: "8", wind: {
 * deg: "190", dir: "南风", sc: "4-5", spd: "19" } },
 */
public class Now {
	@SerializedName("tmp")
	public String temperature;

	@SerializedName("cond")
    public More mMore;

	public class More {
		@SerializedName("txt")
		public String info;
	}
}
