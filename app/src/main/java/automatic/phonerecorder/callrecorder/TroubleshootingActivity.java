package automatic.phonerecorder.callrecorder;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.android.gms.ads.AdRequest;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TroubleshootingActivity extends AppCompatActivity  {

    private AdView bannerAdView;
    FrameLayout bannerAdContainer;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("Roboto-Regular.ttf")
                .build()
        );
        setContentView(R.layout.activity_troubleshooting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.nav_troubleshooting));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                CallRecorderApp.isNeedShowPasscode = false;
            }
        });


        Initbannerad();
    }
    @Override
    public void onDestroy() {
        if (bannerAdView != null) {
            bannerAdView.destroy();
            bannerAdView = null;
        }
        super.onDestroy();
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }
    private void Initbannerad()
    {

        if (bannerAdView != null) {
            bannerAdView.destroy();
            bannerAdView = null;
        }
        bannerAdContainer = findViewById(R.id.adView_main);
        boolean isTablet = false;
        bannerAdView = new AdView(TroubleshootingActivity.this, getString(R.string.BPass),
                isTablet ? AdSize.BANNER_HEIGHT_90 : AdSize.BANNER_HEIGHT_50);

        // Reposition the ad and add it to the view hierarchy.
        bannerAdContainer.addView(bannerAdView);

        // Set a listener to get notified on changes or when the user interact with the ad.
//        bannerAdView.setAdListener(TroubleshootingActivity.this);

        // Initiate a request to load an ad.
        bannerAdView.loadAd();
    }
}
