package automatic.phonerecorder.callrecorder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.CreateFileActivityOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.ads.AdView;



import automatic.phonerecorder.callrecorder.controller.UploadFile;
import automatic.phonerecorder.callrecorder.utils.ConnectionUtils;
import automatic.phonerecorder.callrecorder.utils.GDPR;
import automatic.phonerecorder.callrecorder.utils.MyConstants;
import automatic.phonerecorder.callrecorder.utils.PreferUtils;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static automatic.phonerecorder.callrecorder.MainActivity.counter;
import static automatic.phonerecorder.callrecorder.MainActivity.frequence;

/**
 * Created by Anh Son on 6/27/2016.
 */
public class CloudActivity extends PreferenceActivity
        implements MyConstants,View.OnClickListener,Preference.OnPreferenceClickListener {
    private Context mContext;

    public static DropboxAPI<AndroidAuthSession> mDropboxApi;
    private Button mSyncAll;
    private Preference mDrive, mDropbox;
    InterstitialAd mInterstitialAd;
    private AdView adView;

    private static final String TAG = "drive-quickstart";
    private static final int REQUEST_CODE_SIGN_IN = 0;
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 1;
    private static final int REQUEST_CODE_CREATOR = 2;

    private GoogleSignInClient mGoogleSignInClient;
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;
    private Bitmap mBitmapToSave;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
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
        bar.setTitle(getString(R.string.nav_cloud));
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                CallRecorderApp.isNeedShowPasscode = false;
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        mContext = this;
        MobileAds.initialize(this,
                getResources().getString(R.string.app_id));
        InitInterstitial();
        getPreferenceManager()
                .setSharedPreferencesName(MyConstants.PREFS_NAME);
        addPreferencesFromResource(R.xml.cloud_acount_prefs);
        getListView().setDivider(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.footer_cloud_account);

        mSyncAll = (Button)findViewById(R.id.footer_cloud_account_btn_sync_all);
        mSyncAll.setOnClickListener(this);
        mDrive = findPreference(KEY_DRIVE_ACCOUNT);
        mDropbox = findPreference(KEY_DROPBOX_ACCOUNT);
        mDrive.setOnPreferenceClickListener(this);
        mDropbox.setOnPreferenceClickListener(this);

        getInstanceDropbox(mContext);

        //For Hide Drive account cloud , will update in next version
        if(!IS_ENABLE_GOOGLE_DRIVE){
            PreferenceCategory mCategory = (PreferenceCategory) findPreference("preference_category_account_cloud");
            Preference mGoogleDivePref = findPreference("key_drive_account");
            mCategory.removePreference(mGoogleDivePref);
        }
        Log.d(TAG,"isDropboxLinked = "+isDropboxLinked());
        setLoggedIn(false, false);
        Initbannerad();
    }

    @Override
    protected void onResume() {
        super.onResume();

//        setLoggedIn(PreferUtils.getbooleanPreferences(mContext, IS_DROPBOX_LINKED),false);
        AndroidAuthSession session = mDropboxApi.getSession();
        // The next part must be inserted in the onResume() method of the
        // activity from which session.startAuthentication() was called, so
        // that Dropbox authentication completes properly.
        if (session.authenticationSuccessful()) {
            try {
                // Store it locally in our app for later use
                storeAuth(session);
                setLoggedIn(true,false);// disable Drive google
                if(!PreferUtils.getbooleanPreferences(mContext, KEY_IS_THE_FIRST_SYNC_AFTER_AUTHENTICATION)){
                    if(ConnectionUtils.isWifiAvailable(mContext)){
                        new UploadFile(mContext, mDropboxApi).execute(MODE_UPLOAD_FOLDER);
                    }else if (ConnectionUtils.isMobileAvailable(mContext)) {
                        createWaringDialog(getString(R.string.warning_dialog_using_mobile_data_title),
                                getString(R.string.warning_dialog_using_mobile_data_des), true);
                    }

                    PreferUtils.savebooleanPreferences(mContext, KEY_IS_THE_FIRST_SYNC_AFTER_AUTHENTICATION, true);
                }
            } catch (IllegalStateException e) {
                Toast.makeText(mContext, "Couldn't authenticate with Dropbox",
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error authenticating" + e.getLocalizedMessage());
            }
        }
    }

    public static DropboxAPI<AndroidAuthSession> getInstanceDropbox(
            Context context) {
        AndroidAuthSession session = buildSession(context);
        mDropboxApi = new DropboxAPI<>(session);
        return mDropboxApi;
    }

    /**
     * Check this app linked to Dropbox or not
     *
     * @return
     */
    public static boolean isDropboxLinked() {
        return (mDropboxApi != null && (mDropboxApi.getSession().isLinked()));
    }

    public static AndroidAuthSession buildSession(Context context) {
        AppKeyPair appKeyPair = new AppKeyPair(DROPBOX_APP_KEY,
                DROPBOX_APP_SECRET);

        AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
        loadAuth(context, session);
        return session;
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a
     * local store, rather than storing user name & password, and
     * re-authenticating each time (which is not to be done, ever).
     */
    public static void loadAuth(Context context, AndroidAuthSession session) {
        SharedPreferences prefs = context.getSharedPreferences(
                ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key == null || secret == null || key.length() == 0
                || secret.length() == 0)
            return;

        if (key.equals("oauth2:")) {
            // If the key is set to "oauth2:", then we can assume the token is
            // for OAuth 2.
            session.setOAuth2AccessToken(secret);
        }
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a
     * local store, rather than storing user name & password, and
     * re-authenticating each time (which is not to be done, ever).
     */
    private void storeAuth(AndroidAuthSession session) {
        // Store the OAuth 2 access token, if there is one.
        String oauth2AccessToken = session.getOAuth2AccessToken();
        if (oauth2AccessToken != null) {
            SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME,
                    0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, "oauth2:");
            edit.putString(ACCESS_SECRET_NAME, oauth2AccessToken);
            edit.commit();
            return;
        }
    }

    private void logOut() {
        // Remove credentials from the session
        mDropboxApi.getSession().unlink();
        // Clear our stored keys
        clearKeys();
        // Change UI state to display logged out version
        setLoggedIn(false, false);
        PreferUtils.savebooleanPreferences(mContext, KEY_IS_THE_FIRST_SYNC_AFTER_AUTHENTICATION, false);
        PreferUtils.savebooleanPreferences(mContext, IS_DROPBOX_LINKED, false);
    }
    /**
     * Enable view when login dropbox or google drive
     *
     * @param userDropboxLoggedIn
     * @param userGoogleLoggedIn
     */
    private void setLoggedIn(boolean userDropboxLoggedIn, boolean userGoogleLoggedIn) {
        if (DEBUG)
            Log.i(TAG, "Dropbox login is " + userDropboxLoggedIn);
        if(userDropboxLoggedIn)
            PreferUtils.savebooleanPreferences(mContext, IS_DROPBOX_LINKED, true);
        if (userDropboxLoggedIn || userGoogleLoggedIn) {
            findPreference("cloud_sync_wifi_only")
                    .setEnabled(true);
            findPreference("cloud_sync_automatic")
                    .setEnabled(true);
            mSyncAll.setEnabled(true);
        } else {
            findPreference("cloud_sync_wifi_only")
                    .setEnabled(false);
            findPreference("cloud_sync_automatic")
                    .setEnabled(false);
            mSyncAll.setEnabled(false);
        }

        if(IS_ENABLE_GOOGLE_DRIVE){
            //for 2 account : Dropbox and Drive .
            if (userDropboxLoggedIn) {
                findPreference("key_drive_account").setIcon(R.drawable.ic_drive_unlink);
                findPreference("key_dropbox_account").setIcon(R.drawable.ic_dropbox);
            } else if (userGoogleLoggedIn) {
                findPreference("key_drive_account").setIcon(R.drawable.ic_drive);
                findPreference("key_dropbox_account").setIcon(R.drawable.ic_dropbox_unlink);
            } else {
                findPreference("key_drive_account").setIcon(R.drawable.ic_drive_unlink);
                findPreference("key_dropbox_account").setIcon(R.drawable.ic_dropbox_unlink);
            }
        }else {
            // one account : Dropbox
            if (userDropboxLoggedIn) {
                findPreference("key_dropbox_account").setIcon(R.drawable.ic_dropbox);
            }else {
                findPreference("key_dropbox_account").setIcon(R.drawable.ic_dropbox_unlink);
            }
        }

    }

    private void clearKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.clear();
        edit.apply();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.footer_cloud_account_btn_sync_all:
                // Toast.makeText(mContext, "Sync All", Toast.LENGTH_LONG).show();
                if(ConnectionUtils.isWifiAvailable(mContext)){
                    new UploadFile(mContext, mDropboxApi).execute(MODE_UPLOAD_FOLDER);
                }else if (ConnectionUtils.isMobileAvailable(mContext)) {
                    // warning -> OK
                    createWaringDialog(getString(R.string.warning_dialog_using_mobile_data_title),
                            getString(R.string.warning_dialog_using_mobile_data_des), true);
                }else {
                    // warning -> NO
                    createWaringDialog("", getString(R.string.no_internet), false);
                }

                // new UploadFile(mContext, mDropboxApi).execute(MODE_UPLOAD_FOLDER);

                break;

            default:
                break;
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (key == null)
            return false;
        if(!ConnectionUtils.isConnectingToInternet(mContext)){
            createWaringDialog("", getString(R.string.no_internet), false);
            return false;
        }
        if (key.equals(KEY_DRIVE_ACCOUNT)) {
            Toast.makeText(mContext, "Login", Toast.LENGTH_SHORT).show();
            signIn();
        } else if (key.equals(KEY_DROPBOX_ACCOUNT)) {
            if (isDropboxLinked()) {
                if (DEBUG) Log.i(TAG, "isDropboxLinked " + isDropboxLinked());
                logOut();
            } else {
                if (DEBUG) Log.i(TAG, "isDropboxLinked " + isDropboxLinked());
                mDropboxApi.getSession().startOAuth2Authentication(mContext);

            }

        }
        return false;
    }

    private void createWaringDialog(String title,String message,boolean isOnline){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        if(isOnline) {
            builder.setTitle(title);
            builder.setMessage(message);
            // Add the buttons
            builder.setPositiveButton(R.string.string_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    new UploadFile(mContext, mDropboxApi).execute(MODE_UPLOAD_FOLDER);
                }
            });
            builder.setNegativeButton(R.string.string_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                    dialog.dismiss();
                }
            });
        }else {
            builder.setMessage(message);
            builder.setPositiveButton(R.string.string_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    dialog.dismiss();
                }
            });
        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }

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
    private void Initbannerad()
    {
        adView = findViewById(R.id.adView_main);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
    /** Start sign in activity. */
    private void signIn() {
        Log.i(TAG, "Start sign in");
        mGoogleSignInClient = buildGoogleSignInClient();
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    /** Build a Google SignIn client. */
    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .build();
        return GoogleSignIn.getClient(this, signInOptions);
    }

    /** Create a new file and save it to Drive. */
    private void saveFileToDrive() {
        // Start by creating a new contents, and setting a callback.
        Log.i(TAG, "Creating new contents.");
        final Bitmap image = mBitmapToSave;

        mDriveResourceClient
                .createContents()
                .continueWithTask(
                        new Continuation<DriveContents, Task<Void>>() {
                            @Override
                            public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
                                return createFileIntentSender(task.getResult(), image);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Failed to create new contents.", e);
                            }
                        });
    }

    /**
     * Creates an {@link IntentSender} to start a dialog activity with configured {@link
     * CreateFileActivityOptions} for user to create a new photo in Drive.
     */
    private Task<Void> createFileIntentSender(DriveContents driveContents, Bitmap image) {
        Log.i(TAG, "New contents created.");
        // Get an output stream for the contents.
        OutputStream outputStream = driveContents.getOutputStream();
        // Write the bitmap data from it.
        ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, bitmapStream);
        try {
            outputStream.write(bitmapStream.toByteArray());
        } catch (IOException e) {
            Log.w(TAG, "Unable to write file contents.", e);
        }

        // Create the initial metadata - MIME type and title.
        // Note that the user will be able to change the title later.
        MetadataChangeSet metadataChangeSet =
                new MetadataChangeSet.Builder()
                        .setMimeType("image/jpeg")
                        .setTitle("Android Photo.png")
                        .build();
        // Set up options to configure and display the create file activity.
        CreateFileActivityOptions createFileActivityOptions =
                new CreateFileActivityOptions.Builder()
                        .setInitialMetadata(metadataChangeSet)
                        .setInitialDriveContents(driveContents)
                        .build();

        return mDriveClient
                .newCreateFileActivityIntentSender(createFileActivityOptions)
                .continueWith(
                        new Continuation<IntentSender, Void>() {
                            @Override
                            public Void then(@NonNull Task<IntentSender> task) throws Exception {
                                startIntentSenderForResult(task.getResult(), REQUEST_CODE_CREATOR, null, 0, 0, 0);
                                return null;
                            }
                        });
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                Log.i(TAG, "Sign in request code");
                // Called after user is signed in.
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "Signed in successfully.");
                    // Use the last signed in account here since it already have a Drive scope.
                    mDriveClient = Drive.getDriveClient(this, GoogleSignIn.getLastSignedInAccount(this));
                    // Build a drive resource client.
                    mDriveResourceClient =
                            Drive.getDriveResourceClient(this, GoogleSignIn.getLastSignedInAccount(this));
                    // Start camera.
                    startActivityForResult(
                            new Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CODE_CAPTURE_IMAGE);
                }
                break;
            case REQUEST_CODE_CAPTURE_IMAGE:
                Log.i(TAG, "capture image request code");
                // Called after a photo has been taken.
                if (resultCode == Activity.RESULT_OK) {
                    Log.i(TAG, "Image captured successfully.");
                    // Store the image data as a bitmap for writing later.
                    mBitmapToSave = (Bitmap) data.getExtras().get("data");
                    saveFileToDrive();
                }
                break;
            case REQUEST_CODE_CREATOR:
                Log.i(TAG, "creator request code");
                // Called after a file is saved to Drive.
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "Image successfully saved.");
                    mBitmapToSave = null;
                    // Just start the camera again for another photo.
                    startActivityForResult(
                            new Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CODE_CAPTURE_IMAGE);
                }
                break;
        }
    }
}
