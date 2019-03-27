package automatic.phonerecorder.callrecorder.utils;

import android.content.Context;
import android.content.SharedPreferences;

import automatic.phonerecorder.callrecorder.controller.MyFileManager;

import java.io.File;
import java.util.Date;

/**
 * Created by Anh Son on 6/8/2016.
 */
public class PreferUtils implements MyConstants{

    /**
     * Save value to preferences on module setting.
     *
     * @param value
     */
    public static boolean savebooleanPreferences(Context context, String key,
                                                 boolean value) {
        SharedPreferences data = context.getSharedPreferences(
                PREFS_NAME, 0);
        SharedPreferences.Editor editor = data.edit();
        editor.putBoolean(key, value);
        boolean result = editor.commit();
        return result;
    }

    public static boolean getbooleanPreferences(Context context, String key) {
        SharedPreferences data = context.getSharedPreferences(
                PREFS_NAME, 0);
        if (key == null) {
            return false;
        }
        if (key.equals(SERVICE_ENABLED)
                || key.equals(KEY_ENABLE_NOTIFICATION)
                || key.equals(KEY_IS_THE_FIRST_NO_MEDIA)
                || key.equals(KEY_IS_THE_FIRST_OPEN_APP)) {
            return data.getBoolean(key, true);
        }
        return data.getBoolean(key, false);
    }
    public static boolean saveStringPreferences(Context context, String key,
                                                String value) {
        SharedPreferences data = context.getSharedPreferences(
                PREFS_NAME, 0);
        SharedPreferences.Editor editor = data.edit();
        editor.putString(key, value);
        boolean result = editor.commit();
        return result;
    }

    public static String getStringPreferences(Context context, String key) {
        SharedPreferences data = context.getSharedPreferences(
                PREFS_NAME, 0);
        // For default value
        if (key.equals(KEY_MODE_RECORDER)) {
            return data.getString(key, ""+ MODE_RECORDER_RECORD_ALL);
        } else if (key.equals(KEY_INBOX_SIZE)) {
            return data.getString(key, "" + MAXIMUM_INBOX_SIZE_100);
        } else if (key.equals(KEY_AUDIO_SOURCE)) {
            return data.getString(key, "" + VOICE_CALL);
        } else if (key.equals(KEY_ACTION_WHEN_NOTE)){
            return data.getString(key, "" + ASK_WHAT_TO_DO);
        }
        return data.getString(key, "1000");
    }

    public static boolean saveIntPreferences(Context context, String key,
                                             int value) {
        SharedPreferences data = context.getSharedPreferences(
                PREFS_NAME, 0);
        SharedPreferences.Editor editor = data.edit();
        editor.putInt(key, value);
        boolean result = editor.commit();
        return result;
    }

    public static int getIntPreferences(Context context, String key) {
        SharedPreferences data = context.getSharedPreferences(
                PREFS_NAME, 0);
        return data.getInt(key, 0);
    }

    /**
     * get Name fodler from pref , if have not yet set , app will get default
     * value
     *
     * @param context
     * @return
     */
    public static String getNameFolderSaveData(Context context) {
        SharedPreferences data = context.getSharedPreferences(
                PREFS_NAME, 0);
        return data.getString(KEY_NAME_FOLDER_SAVE_DATA,
                FILE_DIRECTORY);
    }

    /**
     * get path fodler from preferences , if have not yet set , app will get
     * default value
     *
     * @param context
     * @return
     */
    public static String getPathFolderSaveData(Context context) {
        String namefolder = getNameFolderSaveData(context);
        String currentDate = (String) android.text.format.DateFormat.format(
                "yyyy-MM-dd", new Date());
        String filepath = MyFileManager.getFilePath() + File.separator
                + namefolder + File.separator + currentDate;
        File file = new File(filepath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return filepath;
    }
}
