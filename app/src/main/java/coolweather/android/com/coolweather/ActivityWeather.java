package coolweather.android.com.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.List;

import coolweather.android.com.coolweather.gson.Forecast;
import coolweather.android.com.coolweather.gson.Weather;
import coolweather.android.com.coolweather.service.AutoDownLoadService;
import coolweather.android.com.coolweather.util.HttpUtil;
import coolweather.android.com.coolweather.util.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ActivityWeather extends AppCompatActivity {

	private ScrollView mScWeatherLayout;
	private TextView mTvTitleCity;
	private TextView mTvUpdateTime;
	private TextView mTvDegree;
	private TextView mTvWeatherInfo;
	private LinearLayout mLlForecast;
	private TextView mTvAqi;
	private TextView mTvPM25;
	private TextView mTvComfort;
	private TextView mTvCarWash;
	private TextView mTvSport;
	private ImageView mIVBingPic;
	private SharedPreferences mSp;
	// 供Frgmend调用
	public DrawerLayout mDrawerLayout;
	public SwipeRefreshLayout mSwipeRefreshLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*--------------沉浸状态栏start--------------*/
		// 5.0以上
		if (Build.VERSION.SDK_INT >= 21) {
			View decorView = getWindow().getDecorView();
			decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
			getWindow().setStatusBarColor(Color.TRANSPARENT);
		}
		// 为了防止滚动的时候内容盖住状态栏，所以布局文件还需要加上fitsSystemWindows="true"
		/*--------------沉浸状态栏end--------------*/
		setContentView(R.layout.activity_weather);

		// 初始化控件
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swip_refresh);
		Button btnNav = (Button) findViewById(R.id.btn_nav);
		btnNav.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mDrawerLayout.openDrawer(GravityCompat.START);
			}
		});
		mIVBingPic = (ImageView) findViewById(R.id.iv_bing_pic);
		mScWeatherLayout = (ScrollView) findViewById(R.id.sc_weather);
		mTvTitleCity = (TextView) findViewById(R.id.tv_title_city);
		mTvUpdateTime = (TextView) findViewById(R.id.tv_title_update_time);
		mTvDegree = (TextView) findViewById(R.id.tv_degree);
		mTvWeatherInfo = (TextView) findViewById(R.id.tv_weather_info);

		mLlForecast = (LinearLayout) findViewById(R.id.layout_forecast);

		mTvAqi = (TextView) findViewById(R.id.tv_aqi);
		mTvPM25 = (TextView) findViewById(R.id.tv_pm25);
		mTvComfort = (TextView) findViewById(R.id.tv_comfort);
		mTvCarWash = (TextView) findViewById(R.id.tv_car_wash);
		mTvSport = (TextView) findViewById(R.id.tv_sport);

		mSp = PreferenceManager.getDefaultSharedPreferences(this);
		String weatherStr = mSp.getString("weather", null);

		if (weatherStr != null) {
			// 有缓存直接解析
			Weather weather = Utility.handleWeatherResponse(weatherStr);
			showWeatherInfo(weather);
		} else {
			// 无缓存,去获取JSON
			String weather_id = getIntent().getStringExtra("weather_id");
			mLlForecast.setVisibility(View.INVISIBLE);
			requestWeather(weather_id);
		}
		// http://cn.bing.com/az/hprichbg/rb/SteepSheep_ZH-CN8716398488_1920x1080.jpg
		String bingPicStr = mSp.getString("bing_pic", null);
		if (bingPicStr != null) {
			Glide.with(this).load(bingPicStr).into(mIVBingPic);
		} else {
			loadBingPic();
		}
	}

	/**
	 * 请求必应每日一图
	 */
	private void loadBingPic() {
		// 请求这个网址就会得到图片地址
		String requestBingPicUrl = "http://guolin.tech/api/bing_pic";
		HttpUtil.sendOkhttpRequest(requestBingPicUrl, new Callback() {
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				// 返回图片真实地址：http://cn.bing.com/az/hprichbg/rb/SteepSheep_ZH-CN8716398488_1920x1080.jpg
				final String responseStr = response.body().string();
				// 保存到本地
				SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(ActivityWeather.this).edit();
				edit.putString("bing_pic", responseStr);
				edit.apply();
				// ui线程显示
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Glide.with(ActivityWeather.this).load(responseStr).into(mIVBingPic);
					}
				});
			}

			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
			}
		});
	}

	private void showWeatherInfo(Weather weather) {
		if (weather.status.equals("ok")) {
			Intent intent = new Intent(this, AutoDownLoadService.class);
			startService(intent);
		} else {
			Toast.makeText(this, "获取信息失败", Toast.LENGTH_SHORT).show();
		}

		String cityName = weather.basic.cityName;
		/**
		 * "2017-03-05 13:49" 获取 13:49
		 */
		String updateTime = weather.basic.mUpdate.updateTime.split(" ")[1];
		String degree = weather.now.temperature + "℃";
		/**
		 * now: { cond: { code: "101", txt: "多云" }, 里的txt
		 */
		String weaterInfo = weather.now.mMore.info;
		mTvTitleCity.setText(cityName);
		mTvUpdateTime.setText(updateTime);
		mTvDegree.setText(degree);
		mTvWeatherInfo.setText(weaterInfo);

		// 清空布局内容,防止叠加
		mLlForecast.removeAllViews();
		List<Forecast> forecasts = weather.mForecasts;
		for (Forecast forecast : forecasts) {
			String date = forecast.date;
			String info = forecast.mMore.info;
			String max = forecast.mTemperature.max;
			String min = forecast.mTemperature.min;

			View inflate = LayoutInflater.from(this).inflate(R.layout.forecast_item, mLlForecast, false);
			TextView tvDate = (TextView) inflate.findViewById(R.id.tv_date);
			TextView tvInfo = (TextView) inflate.findViewById(R.id.tv_info);
			TextView tvMax = (TextView) inflate.findViewById(R.id.tv_max);
			TextView tvMin = (TextView) inflate.findViewById(R.id.tv_min);

			tvDate.setText(date);
			tvInfo.setText(info);
			tvMax.setText(max);
			tvMin.setText(min);
			// 完成后添加一条
			mLlForecast.addView(inflate);
		}
		if (weather.aqi != null) {
			String aqi = weather.aqi.city.aqi;
			String pm25 = weather.aqi.city.pm25;
			mTvAqi.setText(aqi);
			mTvPM25.setText(pm25);
		}
		String comfrotStr = "舒适度: " + weather.suggestion.mComfort.info;
		String carWashStr = "洗车指数: " + weather.suggestion.mCarWash.info;
		String sportStr = "洗车指数: " + weather.suggestion.mSport.info;

		mTvComfort.setText(comfrotStr);
		mTvCarWash.setText(carWashStr);
		mTvSport.setText(sportStr);
		mLlForecast.setVisibility(View.VISIBLE);
	}

	public void requestWeather(final String weather_id) {
		String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weather_id
				+ "&&key=b0f50aa612384ba7bef78e63c560138b";
		HttpUtil.sendOkhttpRequest(weatherUrl, new Callback() {
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				String responseStr = response.body().string();
				final Weather weather = Utility.handleWeatherResponse(responseStr);
				// 主线程更新
				if (weather != null && "ok".equals(weather.status)) {
					SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(ActivityWeather.this).edit();
					edit.putString("weather", responseStr);
					edit.apply();
					// 显示
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							showWeatherInfo(weather);
						}
					});
				}
			}

			@Override
			public void onFailure(Call call, IOException e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(ActivityWeather.this, "获取信息失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
		// 每次请求也要得到新的图片
		loadBingPic();
	}
}
