package coolweather.android.com.coolweather.gson;

/**
 * aqi: { city: { aqi: "53", co: "1", no2: "46", o3: "66", pm10: "55", pm25: "31", qlty: "良", so2: "8" } },
 */
public class Aqi {
	public AqiCity city;

	public class AqiCity {
		public String aqi;
		public String  pm25;
	}

}
