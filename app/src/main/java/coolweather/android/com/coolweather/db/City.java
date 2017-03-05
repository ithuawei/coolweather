package coolweather.android.com.coolweather.db;

import org.litepal.crud.DataSupport;

public class City extends DataSupport{
    //id是实体类字段
    private int id;
    //code是ID值
    private int cityCode;
    private String cityName;
    //城市所属的省份Id
    private int provinceId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
