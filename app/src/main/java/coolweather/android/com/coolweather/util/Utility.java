package coolweather.android.com.coolweather.util;

import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import coolweather.android.com.coolweather.db.City;
import coolweather.android.com.coolweather.db.County;
import coolweather.android.com.coolweather.db.Province;
import coolweather.android.com.coolweather.gson.Weather;

public class Utility {
	/**
	 * 解析服务器返回的json的"省级"数据,并存入数据库 http://guolin.tech/api/china [ { id: 1, name: "北京" }, { id: 2, name: "上海" },
	 * 
	 * @param response
	 * @return
	 */
	public static boolean handleProvinceResponse(String response) {
		if (!TextUtils.isEmpty(response)) {
			try {
				JSONArray allProvinces = new JSONArray(response);
				for (int i = 0; i < allProvinces.length(); i++) {
					JSONObject province = allProvinces.getJSONObject(i);
					int id = province.getInt("id");
					String name = province.getString("name");
					Province p = new Province();
					p.setProvinceCode(id);
					p.setProvinceName(name);
					p.save();
				}
				return true;
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			}

		}
		return false;
	}

	/**
	 * 解析服务器返回的json的"市级"数据,并存入数据库 http://guolin.tech/api/china/24 [ { id: 226, name: "南宁" }, { id: 227, name: "崇左" },
	 * 
	 * @param response
	 * @param provinceId
	 *            还要有个所属省份id
	 * @return
	 */
	public static boolean handleCityResponse(String response, int provinceId) {
		if (!TextUtils.isEmpty(response)) {

			try {
				JSONArray allCities = new JSONArray(response);
				for (int i = 0; i < allCities.length(); i++) {
					JSONObject city = allCities.getJSONObject(i);
					int id = city.getInt("id");
					String name = city.getString("name");

					City c = new City();
					c.setCityCode(id);
					c.setCityName(name);
					c.setProvinceId(provinceId);
					c.save();
				}
				return true;

			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			}

		}
		return false;
	}

	/**
	 * 解析服务器返回的json的"县级"数据,并存入数据库 http://guolin.tech/api/china/24/228
	 *
	 * [ { id: 1650, name: "柳州", weather_id: "CN101300301" }, { id: 1651, name: "柳城", weather_id: "CN101300302" },
	 * 
	 * @param response
	 * @param cityId
	 *            所属城市id
	 * @return
	 */
	public static boolean handleCountyResponse(String response, int cityId) {
		if (!TextUtils.isEmpty(response)) {

			try {
				JSONArray allCounties = new JSONArray(response);
				for (int i = 0; i < allCounties.length(); i++) {
					JSONObject county = allCounties.getJSONObject(i);
					String name = county.getString("name");
					String weatherId = county.getString("weather_id");

					County c = new County();
					c.setCountyName(name);
					c.setWeatherId(weatherId);
					c.setCityId(cityId);
					c.save();
				}
				return true;

			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			}

		}
		return false;
	}

	/**
	 * 将服务器返回的json数据解析成Weather实体类
	 *
	 * 查询详细天气： 广东->深圳 http://guolin.tech/api/weather?cityid=CN101280601&&key=b0f50aa612384ba7bef78e63c560138b
	 *
	 * { HeWeather: [ { aqi: {}, basic: {}, daily_forecast: [], hourly_forecast: [], now: {}, status: "ok", suggestion:
	 * {} } ] }
	 */
	public static Weather handleWeatherResponse(String response) {
		Weather weather = null;
		try {
			// ｛｝=》只有一个weather对象
			JSONObject jsonObject = new JSONObject(response);
			// {[]}只有一个数组
			JSONArray heWeatherArray = jsonObject.getJSONArray("HeWeather");
			// [ { aqi: {}, basic: {}, daily_forecast: [], hourly_forecast: [], now: {}, status: "ok", suggestion:* {} }
			// ]
			JSONObject weatherObject = heWeatherArray.getJSONObject(0);
			// 为了获取“{ aqi: {}, basic: {}, daily_forecast: [], hourly_forecast: [], now: {}, status: "ok", suggestion:*
			// {} }”
			String weatherStr = weatherObject.toString();
			Gson gson = new Gson();
			weather = gson.fromJson(weatherStr, Weather.class);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return weather;
	}
}
