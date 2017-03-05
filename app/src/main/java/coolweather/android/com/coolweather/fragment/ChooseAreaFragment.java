package coolweather.android.com.coolweather.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import coolweather.android.com.coolweather.ActivityWeather;
import coolweather.android.com.coolweather.R;
import coolweather.android.com.coolweather.db.City;
import coolweather.android.com.coolweather.db.County;
import coolweather.android.com.coolweather.db.Province;
import coolweather.android.com.coolweather.util.HttpUtil;
import coolweather.android.com.coolweather.util.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	private ProgressDialog mProgressDialog;
	private TextView mTvTitle;
	private Button mBtnBack;
	private ListView mLv;
	private ArrayAdapter<String> mAdapter;
	private List<String> mDataList = new ArrayList<>();
	/**
	 * 省列表、市列表、县列表
	 */
	private List<Province> mProvinceList;
	private List<City> mCityList;
	private List<County> mCountyList;

	/**
	 * 选中的省份、市区、县
	 */
	private Province mSelectProvince;
	private City mSelectCity;
	private County mSelectCounty;

	/**
	 * 当前选中级别,首次是省级
	 */
	private int mCurrentLevel;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View inflate = inflater.inflate(R.layout.choose_area, container, false);
		mTvTitle = (TextView) inflate.findViewById(R.id.tv_title);
		mBtnBack = (Button) inflate.findViewById(R.id.bt_back);
		mLv = (ListView) inflate.findViewById(R.id.lv_data);

		mAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, mDataList);
		mLv.setAdapter(mAdapter);
		return inflate;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// 进来先显示省份
		queryProvinces();
		// 点击条目,统一处理
		mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (mCurrentLevel == LEVEL_PROVINCE) {
					mSelectProvince = mProvinceList.get(position);
					// 显示省份,点击后就查询和显示城市
					queryAndDisplayCities();
				} else if (mCurrentLevel == LEVEL_CITY) {
					mSelectCity = mCityList.get(position);
					// 点击城市去查询县份
					queryAndDisplayCounties();
				} else if (mCurrentLevel == LEVEL_COUNTY) {
					String weatherId = mCountyList.get(position).getWeatherId();
					Intent intent = new Intent(getActivity(), ActivityWeather.class);
					intent.putExtra("weather_id", weatherId);
					startActivity(intent);
                    getActivity().finish();
                }
			}
		});
		// 返回按钮的处理:(无按钮)省<-市级<-县级
		mBtnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mCurrentLevel == LEVEL_COUNTY) {
					queryAndDisplayCities();
				} else if (mCurrentLevel == LEVEL_CITY) {
					queryProvinces();
				}
			}
		});
	}

	/**
	 * 查询省份级,先从本地数据库查询,本地没有再去服务器查询.
	 */
	private void queryProvinces() {
		mTvTitle.setText("中国");
		mBtnBack.setVisibility(View.GONE);
		// 直接获取列表
		mProvinceList = DataSupport.findAll(Province.class);
		if (mProvinceList.size() > 0) {
			// 清空适配器的数据,再赋值新的,然后再刷新数据
			mDataList.clear();
			for (Province p : mProvinceList) {
				mDataList.add(p.getProvinceName());
			}
			mAdapter.notifyDataSetChanged();
			mLv.setSelection(0);
			mCurrentLevel = LEVEL_PROVINCE;
			// 服务器,获取省份数据
		} else {
			String adressUrl = "http://guolin.tech/api/china";
			// 传入什么等级就查询什么等级
			queryFromServer(adressUrl, LEVEL_PROVINCE);
		}
	}

	private void queryAndDisplayCities() {
		mTvTitle.setText(mSelectProvince.getProvinceName());
		mBtnBack.setVisibility(View.VISIBLE);
		// 本地查询城市数据,没有则到服务器获取
		// 按条件查找,指定的城市id
		mCityList = DataSupport.where("provinceId = ?", String.valueOf(mSelectProvince.getId())).find(City.class);
		if (mCityList.size() > 0) {
			mDataList.clear();
			for (City c : mCityList) {
				mDataList.add(c.getCityName());
			}
			mAdapter.notifyDataSetChanged();
			mLv.setSelection(0);
			mCurrentLevel = LEVEL_CITY;
		} else {
			// 网络查询城市数据
			String addressUrl = "http://guolin.tech/api/china/" + mSelectProvince.getProvinceCode();
			queryFromServer(addressUrl, LEVEL_CITY);
		}
	}

	private void queryAndDisplayCounties() {
		mTvTitle.setText(mSelectCity.getCityName());
		mBtnBack.setVisibility(View.VISIBLE);

		mCountyList = DataSupport.where("cityId = ?", String.valueOf(mSelectCity.getId())).find(County.class);
		if (mCountyList.size() > 0) {
			mDataList.clear();
			for (County c : mCountyList) {
				mDataList.add(c.getCountyName());
			}
			mAdapter.notifyDataSetChanged();
			mLv.setSelection(0);
			mCurrentLevel = LEVEL_COUNTY;
		} else {
			String addressUrl = "http://guolin.tech/api/china/" + mSelectProvince.getProvinceCode() + "/"
					+ mSelectCity.getCityCode();
			queryFromServer(addressUrl, LEVEL_COUNTY);
		}
	}

	/**
	 * 根据传入类型不同查询不同的信息
	 *
	 * @param adressUrl
	 * @param level
	 */
	private void queryFromServer(String adressUrl, final int level) {
		// 先显示个对话框
		showProgressDialog();
		// 请求数据
		HttpUtil.sendOkhttpRequest(adressUrl, new Callback() {
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				boolean result = false;
				if (level == LEVEL_PROVINCE) {
					result = Utility.handleProvinceResponse(response.body().string());
				} else if (level == LEVEL_CITY) {
					result = Utility.handleCityResponse(response.body().string(), mSelectProvince.getId());
				} else if (level == LEVEL_COUNTY) {
					result = Utility.handleCountyResponse(response.body().string(), mSelectCity.getId());
				}

				if (result) {
					// 上面通过okhttp是子线程中处理,下面要ui线程刷新
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// 关闭对话框
							closeProgressDialog();
							// 因为已经存入数据库了,所以再显示出来即可
							if (level == LEVEL_PROVINCE) {
								queryProvinces();
							} else if (level == LEVEL_CITY) {
								queryAndDisplayCities();
							} else if (level == LEVEL_COUNTY) {
								queryAndDisplayCounties();
							}
						}
					});
				}
			}

			@Override
			public void onFailure(Call call, IOException e) {
				closeProgressDialog();
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	private void showProgressDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(getActivity());
			mProgressDialog.setTitle("正在加载中...");
			mProgressDialog.setCancelable(false);
		}
		mProgressDialog.show();
	}

	private void closeProgressDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
	}
}
