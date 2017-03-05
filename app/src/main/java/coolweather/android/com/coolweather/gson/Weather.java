package coolweather.android.com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * { HeWeather: [ { aqi: {}, basic: {}, daily_forecast: [], hourly_forecast: [], now: {}, status: "ok", suggestion: {} }
 * ] }
 */
public class Weather {
	public Aqi aqi;
	public Basic basic;
    //名字要转换的都要加上
    @SerializedName("daily_forecast")
	public List<Forecast> mForecasts;
	public Now now;
	public String status;
	public Suggestion suggestion;
}
