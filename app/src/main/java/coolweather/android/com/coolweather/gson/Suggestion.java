package coolweather.android.com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * suggestion: {
 air: {
 brf: "中",
 txt: "气象条件对空气污染物稀释、扩散和清除无明显影响，易感人群应适当减少室外活动时间。"
 },
 comf: {
 brf: "较舒适",
 txt: "白天天气晴好，同时较大的空气湿度，会使您在午后略感闷热，但早晚仍很凉爽、舒适。"
 },
 cw: {
 brf: "不宜",
 txt: "不宜洗车，未来24小时内有雨，如果在此期间洗车，雨水和路上的泥水可能会再次弄脏您的爱车。"
 },
 drsg: {
 brf: "舒适",
 txt: "建议着长袖T恤、衬衫加单裤等服装。年老体弱者宜着针织长袖衬衫、马甲和长裤。"
 },
 flu: {
 brf: "少发",
 txt: "各项气象条件适宜，无明显降温过程，发生感冒机率较低。"
 },
 sport: {
 brf: "较适宜",
 txt: "天气较好，户外运动请注意防晒。推荐您进行室内运动。"
 },
 trav: {
 brf: "适宜",
 txt: "天气较好，但丝毫不会影响您出行的心情。温度适宜又有微风相伴，适宜旅游。"
 },
 uv: {
 brf: "弱",
 txt: "紫外线强度较弱，建议出门前涂擦SPF在12-15之间、PA+的防晒护肤品。"
 }
 }
 */
public class Suggestion {
    @SerializedName("comf")
    public Comfort mComfort;
    @SerializedName("cw")
    public CarWash mCarWash;
    @SerializedName("sport")
    public Sport mSport;

    public class Comfort {
        @SerializedName("txt")
        public String info;
    }
    public class CarWash {
        @SerializedName("txt")
        public String info;
    }
    public class Sport {
        @SerializedName("txt")
        public String info;
    }
}
