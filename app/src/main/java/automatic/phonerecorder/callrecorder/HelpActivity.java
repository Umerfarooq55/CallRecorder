package automatic.phonerecorder.callrecorder;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import automatic.phonerecorder.callrecorder.utils.GDPR;
import automatic.phonerecorder.callrecorder.utils.MyConstants;
import automatic.phonerecorder.callrecorder.utils.Utilities;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static automatic.phonerecorder.callrecorder.MainActivity.counter;
import static automatic.phonerecorder.callrecorder.MainActivity.frequence;

/**
 * Created by Anh Son on 6/27/2016.
 */
public class HelpActivity extends PreferenceActivity implements MyConstants, Preference.OnPreferenceClickListener {
    private Context mContext;

    private Preference mAppVersion;
    private Preference mLicense;
    private Preference mPrivacy;
    private Preference mGetAllApp;
    private Preference mFeedback;
    /*private Preference mSmobileOnWeb;
    private Preference mTranslation;*/
    InterstitialAd mInterstitialAd;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar_layout, root, false);
        root.addView(bar, 0); // insert at top
        bar.setTitle(getString(R.string.nav_about));
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                CallRecorderApp.isNeedShowPasscode = false;
            }
        });
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        mContext = this;
        addPreferencesFromResource(R.xml.help);
        getListView().setDivider(new ColorDrawable(Color.TRANSPARENT));
        initUI();
        MobileAds.initialize(this,
                getResources().getString(R.string.app_id));
        InitInterstitial();

    }

    private void initUI() {
        mAppVersion = findPreference(KEY_ABOUT_APP_VERSION);
        //mLicense = findPreference(KEY_ABOUT_APP_LICENSE);
        mPrivacy = findPreference(KEY_ABOUT_APP_PRIVACY);
        mGetAllApp = findPreference(KEY_ABOUT_APP_GET_ALL_APP);
        mFeedback = findPreference(KEY_ABOUT_APP_FEEDBACK);
        //mSmobileOnWeb = findPreference(KEY_ABOUT_APP_ON_GOOGLEPLUS);
        //mTranslation = findPreference(KEY_ABOUT_APP_TRANSLATION);

        mAppVersion.setSummary(Utilities.getVersion(mContext));
        mAppVersion.setOnPreferenceClickListener(this);
        //mLicense.setOnPreferenceClickListener(this);
        mPrivacy.setOnPreferenceClickListener(this);
        mGetAllApp.setOnPreferenceClickListener(this);
        mFeedback.setOnPreferenceClickListener(this);
        //mSmobileOnWeb.setOnPreferenceClickListener(this);
        //mTranslation.setOnPreferenceClickListener(this);

    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        /*if (KEY_ABOUT_APP_LICENSE.equals(key)) {
            createLicenseDialogConfirm();
            return true;
        }*/
        if (KEY_ABOUT_APP_GET_ALL_APP.equals(key)) {
//
//                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id="+ getResources().getString(R.string.dev_id))));
            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
            return true;
        }

        if (KEY_ABOUT_APP_PRIVACY.equals(key)) {
            try {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                        .parse(getResources().getString(R.string.privacy_policy_url))));

            } catch (android.content.ActivityNotFoundException anf) {
                startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(getResources().getString(R.string.privacy_policy_url))));
            }
            return true;
        }

        if (KEY_ABOUT_APP_FEEDBACK.equals(key)) {
            Intent feedbackIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "umerfarooqsaeed7@gmail.com", null));
            feedbackIntent.putExtra(Intent.EXTRA_SUBJECT,
                    getString(R.string.app_name) + " ("
                            + Utilities.getVersion(mContext) + "|"
                            + getDeviceName() + "): "
                            + getString(R.string.about_app_feedback_title));
            try {
                mContext.startActivity(Intent.createChooser(feedbackIntent, getString(R.string.feedback) + "..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Utilities.showToast(mContext, getString(R.string.app_feedback_exception_no_app_handle));
            }

            return true;
        }
        /*if (KEY_ABOUT_APP_ON_GOOGLEPLUS.equals(key)) {
            String url = "https://plus.google.com/u/0/105893029919348166328";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            return true;
        }*/
        /*if (KEY_ABOUT_APP_TRANSLATION.equals(key)) {
            Intent translateIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "nkadvertisingmedia@gmail.com", null));
            translateIntent.putExtra(Intent.EXTRA_SUBJECT,
                    getString(R.string.app_name) + " ("
                            + Utilities.getVersion(mContext) + "|"
                            + getDeviceName() + "): "
                            + getString(R.string.about_app_translation_title));
            translateIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.about_app_translation_body));
            try {
                mContext.startActivity(Intent.createChooser(translateIntent, getString(R.string.about_app_translation_title) + "..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Utilities.showToast(mContext, getString(R.string.app_feedback_exception_no_app_handle));
            }

            return true;
        }*/
        return false;
    }

    /*private void createLicenseDialogConfirm() {
        AlertDialog.Builder licenseDialog = new AlertDialog.Builder(mContext);
        licenseDialog.setTitle(getString(R.string.about_app_license_title));

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_license, null);
        licenseDialog.setView(dialogView);

        licenseDialog.setPositiveButton(getString(R.string.string_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = licenseDialog.create();
        alertDialog.show();
    }*/

    @Override
    public void onBackPressed() {
        showInterstitialAd();
        super.onBackPressed();
        CallRecorderApp.isNeedShowPasscode = false;
    }

    private void InitInterstitial() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.Interstitial));
        requestNewInterstitial();

        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                super.onAdClosed();
            }
        });


    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, GDPR.getBundleAd(this)).build();
        mInterstitialAd.loadAd(adRequest);
    }

    private void showInterstitialAd() {
        if (counter==frequence) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
            counter =1;
        }
        else {
            counter++;
        }
    }
}

