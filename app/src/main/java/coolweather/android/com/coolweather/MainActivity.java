package coolweather.android.com.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        //进来级默认去上次的县份
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		String weatherStr = sp.getString("weather", null);
		if (weatherStr != null) {
			Intent intent = new Intent(this, ActivityWeather.class);
			startActivity(intent);
			finish();
		}

	}

}
