package coolweather.android.com.coolweather.db;

import org.litepal.crud.DataSupport;

public class County extends DataSupport{
    //实体字段id
    private int id;
    //天气id
    private String weatherId;
    private String countyName;
    //所属城市的ID
    private int cityId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
