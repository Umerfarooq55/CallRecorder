package automatic.phonerecorder.callrecorder;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import automatic.phonerecorder.callrecorder.controller.MyFileManager;
import automatic.phonerecorder.callrecorder.utils.MyConstants;
import automatic.phonerecorder.callrecorder.utils.PreferUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Anh Son on 6/27/2016.
 */
public class CallRecorderApp extends Application {
    private Context mContext;
    public static boolean isNeedShowPasscode = true;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();

        File filePreference = new File("/data/data/" + this.getPackageName() +  "/shared_prefs/" + MyConstants.PREFS_NAME);
        if ((filePreference != null && !filePreference.exists())) {
            PreferenceManager.setDefaultValues(this, MyConstants.PREFS_NAME, Context.MODE_PRIVATE, R.xml.settings, false);
        }
        // Check and hide media , dont include into Music app
        boolean firstTime = PreferUtils.getbooleanPreferences(mContext, MyConstants.KEY_IS_THE_FIRST_NO_MEDIA);
        if(firstTime){
            PreferUtils.savebooleanPreferences(mContext, MyConstants.KEY_IS_THE_FIRST_NO_MEDIA, false);
            try {
                MyFileManager.hideRootMediaFolder(mContext);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                if(MyConstants.DEBUG) Log.e(MyConstants.TAG, "MainActivity: Cannot create .nomedia file");
            }
        }
    }
}
