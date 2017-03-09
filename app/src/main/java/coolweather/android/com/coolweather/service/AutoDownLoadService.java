package coolweather.android.com.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;

import coolweather.android.com.coolweather.gson.Weather;
import coolweather.android.com.coolweather.util.HttpUtil;
import coolweather.android.com.coolweather.util.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 1.自动下载"曾经打开过的"城市的的JSON数据
 * 
 * 2.自动下载图片
 * 
 * 3.每8小时下载一次
 * 
 * 4.这里只是自动下载,所以是"每次打开"都是最新的,并不是说网络已更新这里也更新
 * 
 * 5.再显示天气情况的活动在加载天气时启动该服务即可
 */
public class AutoDownLoadService extends Service {
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		downLoadWeather();
		downLoadPic();

		// 8小时就执行一次自己
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		// 8小时
		long triggerAtMillis = 60 * 1000 * 60 * 8;

		//启动自己
		Intent intentShelf = new Intent(this, AutoDownLoadService.class);
		PendingIntent pendingIntent = PendingIntent.getService(this, 0, intentShelf, 0);
		alarmManager.cancel(pendingIntent);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingIntent);

		return super.onStartCommand(intent, flags, startId);
	}

	private void downLoadWeather() {
		// 如果有缓存,即曾经打开过,就去读取该城市的id,并重新下载该城市的最新信息,并保存
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		String weatherStr = sp.getString("weather", null);
		if (weatherStr != null) {
			// 先解析
			Weather weather = Utility.handleWeatherResponse(weatherStr);
			String weatherId = weather.basic.weatherId;
			String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId
					+ "&&key=b0f50aa612384ba7bef78e63c560138b";

			HttpUtil.sendOkhttpRequest(weatherUrl, new Callback() {
				@Override
				public void onResponse(Call call, Response response) throws IOException {
					String responseStr = response.body().string();
					Weather weaterNew = Utility.handleWeatherResponse(responseStr);
					if (weaterNew != null && weaterNew.status.equals("ok")) {
						SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
								AutoDownLoadService.this).edit();
						editor.putString("weather", responseStr);
						editor.apply();
					}
				}

				@Override
				public void onFailure(Call call, IOException e) {
					e.printStackTrace();
				}
			});
		}
	}

	private void downLoadPic() {
		String picUrl = "http://guolin.tech/api/bing_pic";
		// 请求该地址,返回一个真实的图片地址,保存这个真实的图片地址即可
		HttpUtil.sendOkhttpRequest(picUrl, new Callback() {
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				String responseStr = response.body().string();
				SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(AutoDownLoadService.this).edit();
				edit.putString("bing_pic", responseStr);
				edit.apply();
			}

			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
			}
		});
	}
}
