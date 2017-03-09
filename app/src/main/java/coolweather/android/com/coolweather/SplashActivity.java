package coolweather.android.com.coolweather;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {
    private RelativeLayout mRelativeLayout;
    private boolean        canJump;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mRelativeLayout = (RelativeLayout) findViewById(R.id.rl_ontainer);
        //权限处理
        List<String> permissionList = new ArrayList<>();
        String permissionStr1 = Manifest.permission.READ_PHONE_STATE;
        String permissionStr2 = Manifest.permission.ACCESS_COARSE_LOCATION;
        String permissionStr3 = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int permissionGrant = PackageManager.PERMISSION_GRANTED;
        if (ContextCompat.checkSelfPermission(this, permissionStr1)
                != permissionGrant) {
            permissionList.add(permissionStr1);
        }
        if (ContextCompat.checkSelfPermission(this, permissionStr2)
                != permissionGrant) {
            permissionList.add(permissionStr2);
        }
        if (ContextCompat.checkSelfPermission(this, permissionStr3)
                != permissionGrant) {
            permissionList.add(permissionStr3);
        }
        if (!permissionList.isEmpty()) {
            String[] permmssions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permmssions, 1);
        } else {
            requestAds();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有的权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestAds();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    /**
     * 请求开屏广告
     */
    private void requestAds() {
        String appId = "1106032504";
        String aDsId = "5030527029390216";
        new SplashAD(this, mRelativeLayout, appId, aDsId, new SplashADListener() {
            @Override
            public void onADDismissed() {
                //广告显示完毕,去到主界面
                forward();
            }

            @Override
            public void onNoAD(int i) {
                //广告加载失败,去到主界面
                forward();
            }

            @Override
            public void onADPresent() {
                //广告加载成功
            }

            @Override
            public void onADClicked() {
                //点击了广告
            }

            @Override
            public void onADTick(long l) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //如果点击了广告回来,可以跳
        if (canJump) {
            forward();
        }
        canJump = true;
    }

    @Override
    protected void onPause() {
		super.onPause();
        canJump = false;
    }

    private void forward() {
        if (canJump) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            canJump = true;
        } 
    }
}
