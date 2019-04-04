package automatic.phonerecorder.callrecorder;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import automatic.phonerecorder.callrecorder.adapter.DatabaseAdapter;
import automatic.phonerecorder.callrecorder.utils.AndroidUtils;
import automatic.phonerecorder.callrecorder.utils.GDPR;
import automatic.phonerecorder.callrecorder.utils.PreferUtils;
import automatic.phonerecorder.callrecorder.utils.Utilities;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.CacheFlag;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;

import java.util.EnumSet;

import automatic.phonerecorder.callrecorder.utils.MyConstants;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static automatic.phonerecorder.callrecorder.MainActivity.counter;
import static automatic.phonerecorder.callrecorder.MainActivity.frequence;

/**
 * Created by Anh Son on 6/10/2016.
 */
public class SettingActivity extends PreferenceActivity implements MyConstants,Preference.OnPreferenceChangeListener, InterstitialAdListener {
    private Context mContext;
    private CheckBoxPreference mNotiticationAlwaysAsk;
    private CheckBoxPreference mNotificationMode;
    private ListPreference mModeRecorderOption;
    private ListPreference mInboxSize;
    private Preference mPriorityContactsManageOption;
    private CheckBoxPreference mPrivateMode;

    private int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE= 5469;

    private final int DIALOG_SETUP_PASSWORD_PRIVATEMODE = 200;
    private final int DIALOG_WARNING_DELETEFILE_INBOXLIMIT = 201;
    private final int DIALOG_WARNING_DISABLE_NOTIFICATION = 202;

    private CharSequence mCurrentValueInboxSize;
    private CharSequence mNewValueInboxSize;
    private int mToltalFile = 0;
    private DatabaseAdapter mDatabase;
    private InterstitialAd interstitialAd;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar_layout, root, false);
        root.addView(bar, 0); // insert at top
        bar.setTitle(getString(R.string.action_settings));
        bar.setTitleTextColor(getResources().getColor(R.color.white));
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                CallRecorderApp.isNeedShowPasscode = false;
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
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
        mContext = this;
        getPreferenceManager()
                .setSharedPreferencesName(PREFS_NAME);
        addPreferencesFromResource(R.xml.settings);
        getListView().setDivider(new ColorDrawable(Color.TRANSPARENT));
        mDatabase = new DatabaseAdapter(mContext);
//        MobileAds.initialize(this,
//                getResources().getString(R.string.app_id));
        InitInterstitial();
        if(AndroidUtils.isAtLeastM()){
            mNotiticationAlwaysAsk = (CheckBoxPreference) findPreference(KEY_NOTIFICATION_ALWAYS_ASK);
            // If this permission hasn't granted yet, disable this option
            if(!Settings.canDrawOverlays(this)){
                PreferUtils.savebooleanPreferences(this,
                        KEY_NOTIFICATION_ALWAYS_ASK, false);
                mNotiticationAlwaysAsk.setChecked(false);
            }
            mNotiticationAlwaysAsk.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @TargetApi(23) @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // TODO Auto-generated method stub
                    if (!PreferUtils.getbooleanPreferences(mContext,
                            KEY_NOTIFICATION_ALWAYS_ASK) ) {
                        if(!Settings.canDrawOverlays(mContext)){
                            String message = getString(R.string.dialog_request_overlay_permision_message) + "<b> Overlay </b>Permission";
                            showMessageOKCancel(message,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                                    Uri.parse("package:" + mContext.getPackageName()));
                                            startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
                                        }
                                    });
                        } else {
                            PreferUtils.savebooleanPreferences(mContext,
                                    KEY_NOTIFICATION_ALWAYS_ASK, true);
                            mNotiticationAlwaysAsk.setChecked(true);
                        }

                    } else {
                        PreferUtils.savebooleanPreferences(mContext,
                                KEY_NOTIFICATION_ALWAYS_ASK, false);
                        mNotiticationAlwaysAsk.setChecked(false);
                    }
                    return false;
                }
            });
        }
        mNotificationMode = (CheckBoxPreference) findPreference(KEY_ENABLE_NOTIFICATION);
        mNotificationMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // TODO Auto-generated method stub
                if(PreferUtils.getbooleanPreferences(mContext, KEY_ENABLE_NOTIFICATION)){
                    createDialogConfirm(DIALOG_WARNING_DISABLE_NOTIFICATION);
                }else {
                    PreferUtils.savebooleanPreferences(mContext, KEY_ENABLE_NOTIFICATION, true);
                    mNotificationMode.setChecked(true);
                }
                return false;
            }
        });
        mPrivateMode = (CheckBoxPreference) findPreference(KEY_PRIVATE_MODE);
        mPrivateMode
                .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                    @Override
                    public boolean onPreferenceChange(Preference preference,
                                                      Object newValue) {
                        // TODO Auto-generated method stub
                        if (!PreferUtils.getbooleanPreferences(mContext,
                                KEY_PRIVATE_MODE)) {
                            createDialogConfirm(DIALOG_SETUP_PASSWORD_PRIVATEMODE);
                        } else {
                            PreferUtils.savebooleanPreferences(mContext,
                                    KEY_PRIVATE_MODE, false);
                            mPrivateMode.setChecked(false);
                        }
                        return false;
                    }
                });

        mPriorityContactsManageOption = findPreference(KEY_PRIORITY_CONTACT);
        mModeRecorderOption = (ListPreference) findPreference(KEY_MODE_RECORDER);
        mInboxSize = (ListPreference) findPreference(KEY_INBOX_SIZE);
        mModeRecorderOption.setOnPreferenceChangeListener(this);
        mInboxSize.setOnPreferenceChangeListener(this);

        setValueForSettingScreen();


    }
    @TargetApi(23) @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (AndroidUtils.isAtLeastM() && requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(mContext)) {
                // You have permission
                PreferUtils.savebooleanPreferences(mContext,
                        KEY_NOTIFICATION_ALWAYS_ASK, true);
                mNotiticationAlwaysAsk.setChecked(true);
            }else {
                PreferUtils.savebooleanPreferences(mContext,
                        KEY_NOTIFICATION_ALWAYS_ASK, false);
                mNotiticationAlwaysAsk.setChecked(false);
            }
        }
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(mContext)
                .setMessage(Html.fromHtml(message))
                .setPositiveButton(getString(R.string.string_ok), okListener)
                .setNegativeButton(getString(R.string.string_cancel), null)
                .create()
                .show();
    }
    private void createDialogConfirm(int id) {
        switch (id) {
            case DIALOG_WARNING_DISABLE_NOTIFICATION:
                AlertDialog.Builder disableNotification = new AlertDialog.Builder(mContext);
                disableNotification.setTitle(getString(R.string.warning_title));
                disableNotification.setMessage(getString(R.string.dialog_warning_disable_notification_message));
                disableNotification.setPositiveButton(R.string.string_ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        PreferUtils.savebooleanPreferences(mContext, KEY_ENABLE_NOTIFICATION, false);
                        mNotificationMode.setChecked(false);
                    }
                });
                disableNotification.setNegativeButton(R.string.string_cancel,  new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });
                AlertDialog disableDg = disableNotification.create();
                disableDg.show();
                break;
            case DIALOG_WARNING_DELETEFILE_INBOXLIMIT:
                AlertDialog.Builder warningDialog = new AlertDialog.Builder(
                        mContext);
                warningDialog
                        .setTitle(getString(R.string.dialog_warning_over_inbox_limit_title));
                warningDialog
                        .setMessage(getString(R.string.dialog_warning_over_inbox_limit_message));
                warningDialog.setCancelable(false);
                warningDialog.setPositiveButton(getString(R.string.string_ok),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                // TODO Auto-generated method stub
                                try {
                                    new DeleteOverFileAsyncTask().execute();
                                } catch (NumberFormatException e) {
                                    // TODO: handle exception
                                    if (DEBUG)
                                        Log.i(TAG,
                                                "SettingFragment NumberFormatException");
                                }
                            }
                        });
                warningDialog.setNegativeButton(getString(R.string.string_cancel),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                dialog.dismiss();
                                PreferUtils.saveStringPreferences(mContext,
                                        KEY_INBOX_SIZE,
                                        mCurrentValueInboxSize.toString());
                                mInboxSize.setValue(mCurrentValueInboxSize
                                        .toString());
                            }
                        });
                AlertDialog dialog = warningDialog.create();
                dialog.show();
                break;
            case DIALOG_SETUP_PASSWORD_PRIVATEMODE:
                final Dialog mConfirmDialog = new Dialog(mContext);
                mConfirmDialog.setTitle(getString(R.string.dialog_setup_password_txt_title));
                mConfirmDialog.setCancelable(false);

                // LayoutInflater inflater = getActivity().getLayoutInflater();
                // View dialoglayout =
                // inflater.inflate(R.layout.dialog_setup_password,
                // null);
                mConfirmDialog.setContentView(R.layout.dialog_setup_password);

                final EditText password = (EditText) mConfirmDialog
                        .findViewById(R.id.dialog_setup_confirm_edt_password);
                final EditText passwordConfirm = (EditText) mConfirmDialog
                        .findViewById(R.id.dialog_setup_confirm_edt_confirmpassword);
                // final TextView message = (TextView)
                // mConfirmDialog.findViewById(R.id.dialog_setup_confirm_txt_waring);
                final TextSwitcher message = (TextSwitcher) mConfirmDialog
                        .findViewById(R.id.dialog_setup_confirm_txt_waring);
                message.setInAnimation(mContext, R.anim.shake_animation);
                message.setInAnimation(mContext, R.anim.shake_animation);
                message.setFactory(new ViewSwitcher.ViewFactory() {
                    @Override
                    public View makeView() {
                        TextView textView = new TextView(mContext);
                        textView.setTextColor(Color.parseColor("#FF7F27"));
                        textView.setGravity(Gravity.CENTER_HORIZONTAL);
                        textView.setTypeface(Typeface.DEFAULT_BOLD);
                        return textView;
                    }
                });
                Button OK = (Button) mConfirmDialog
                        .findViewById(R.id.dialog_setup_confirm_btn_ok);
                Button Cancel = (Button) mConfirmDialog
                        .findViewById(R.id.dialog_setup_confirm_btn_cancel);

                Utilities.showOrHideKeyboard(mContext, password, true);
                OK.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (Utilities.isEditTextEmpty(password)) {
                            message.setVisibility(View.VISIBLE);
                            message.setText(getString(R.string.password_is_empty));
                        } else if (!Utilities.ConpareEdit(password, passwordConfirm)) {
                            message.setVisibility(View.VISIBLE);
                            message.setText(getString(R.string.wrong_match_password));
                        } else if(password.length()<4){
                            message.setVisibility(View.VISIBLE);
                            message.setText(getString(R.string.wrong_lenght_password));
                        }else {
                            mConfirmDialog.dismiss();
                            PreferUtils.savebooleanPreferences(mContext,
                                    KEY_PRIVATE_MODE, true);
                            mPrivateMode.setChecked(true);
                            PreferUtils.savebooleanPreferences(mContext,
                                    KEY_IS_LOGINED, true);
                            PreferUtils.saveStringPreferences(
                                    mContext,
                                    KEY_PASSWORD_FOR_PRIVATE_MODE,
                                    Utilities.md5Digest(password.getText().toString()
                                            .trim()));
                        }
                    }
                });
                Cancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        mConfirmDialog.dismiss();
                        PreferUtils.savebooleanPreferences(mContext, KEY_PRIVATE_MODE,
                                false);
                        mPrivateMode.setChecked(false);
                    }
                });

                mConfirmDialog.show();
                break;
            default:
                break;
        }
    }

    private void setValueForSettingScreen() {
        // TODO Auto-generated method stub
        try {
            int modeRecorder = Integer.parseInt(""
                    + mModeRecorderOption.getValue());
            if (modeRecorder != MODE_RECORDER_PRIORITY_CONTACTS)
                mPriorityContactsManageOption.setEnabled(false);
            else
                mPriorityContactsManageOption.setEnabled(true);
            if (DEBUG)
                Log.i(TAG, "value : " + modeRecorder);
        } catch (NumberFormatException e) {
            // TODO: handle exception
            if (DEBUG)
                Log.e(TAG,
                        "SettingFragment:setValueForSettingScreen - NumberFormatException");
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mModeRecorderOption) {
            // TODO Auto-generated method stub
            final String val = newValue.toString();
            int index = mModeRecorderOption.findIndexOfValue(val);
            if (index == 2) {
                mPriorityContactsManageOption.setEnabled(true);
            } else {
                mPriorityContactsManageOption.setEnabled(false);
            }
            return true;
        }else if (preference == mInboxSize) {
            ListPreference listPreference = (ListPreference) preference;
            mCurrentValueInboxSize = listPreference.getValue();
            mNewValueInboxSize = newValue.toString();
            mToltalFile = mDatabase.getRecordCount(DatabaseAdapter.TABLE_INBOX);
            try {
                if (Integer.parseInt(mNewValueInboxSize.toString()) != MAXIMUM_INBOX_SIZE_UNLIMITED
                        && mToltalFile > Integer.parseInt(mNewValueInboxSize
                        .toString())) {
                    createDialogConfirm(DIALOG_WARNING_DELETEFILE_INBOXLIMIT);
                }
            } catch (NumberFormatException e) {
                // TODO: handle exception
                if (DEBUG)
                    Log.i(TAG, "onPreferenceChange : NumberFormatException");
            }

            return true;
        }
        return false;
    }

    @Override
    public void onInterstitialDisplayed(Ad ad) {

    }

    @Override
    public void onInterstitialDismissed(Ad ad) {
        InitInterstitial();
    }

    @Override
    public void onError(Ad ad, AdError adError) {

    }

    @Override
    public void onAdLoaded(Ad ad) {

    }

    @Override
    public void onAdClicked(Ad ad) {

    }

    @Override
    public void onLoggingImpression(Ad ad) {

    }

    private class DeleteOverFileAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            int numberRecordNeedDelete = mToltalFile
                    - Integer.parseInt(mNewValueInboxSize.toString());
            Utilities.deleteOlderRecord(mContext, numberRecordNeedDelete);

            return null;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            setProgressBarIndeterminateVisibility(true);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            setProgressBarIndeterminateVisibility(false);
            Intent intent = new Intent();
            intent.setAction(MyConstants.ACTION_BROADCAST_INTENT_DELETE_ALL);
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            sendBroadcast(intent);
        }
    }

    @Override
    public void onBackPressed() {
        showInterstitialAd();
        super.onBackPressed();
        CallRecorderApp.isNeedShowPasscode = false;
    }

    private void InitInterstitial() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
            interstitialAd = null;
        }


        // Create the interstitial unit with a placement ID (generate your own on the Facebook app settings).
        // Use different ID for each ad placement in your app.
        interstitialAd = new InterstitialAd(
                SettingActivity.this,
                getString(R.string.ipass));

        // Set a listener to get notified on changes or when the user interact with the ad.
        interstitialAd.setAdListener(SettingActivity.this);

        // Load a new interstitial.
        interstitialAd.loadAd(EnumSet.of(CacheFlag.VIDEO));

    }

    private void showInterstitialAd() {
        if (counter==frequence) {
            if (interstitialAd == null || !interstitialAd.isAdLoaded()) {
                // Ad not ready to show.
                InitInterstitial();
            } else {
                // Ad was loaded, show it!
                interstitialAd.show();

            }
            counter =1;
        }
        else {
            counter++;
        }
    }
}
