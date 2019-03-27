package automatic.phonerecorder.callrecorder.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import automatic.phonerecorder.callrecorder.adapter.DatabaseAdapter;
import automatic.phonerecorder.callrecorder.controller.MyFileManager;
import automatic.phonerecorder.callrecorder.model.PriorityContactModel;
import automatic.phonerecorder.callrecorder.model.RecordModel;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Anh Son on 6/10/2016.
 */
public class Utilities {
    /**
     * Converts dps to pixels.
     *
     * @param dp
     *            Value in dp.
     * @param res
     *            Resources reference to get the screen density.
     * @return Value in pixels.
     */
    public static int dpToPixels(int dp, Resources res) {
        return (int) (res.getDisplayMetrics().density * dp + 0.5f);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    /**
     * Check one phone number saved on Contact or not .
     *
     * @param context
     * @param phoneNumber
     * @return
     */
    public static boolean isSaveContact(Context context, String phoneNumber) {
        // / number is the phone number
        Uri lookupUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        String[] mPhoneNumberProjection = { PhoneLookup._ID,
                PhoneLookup.NUMBER, PhoneLookup.DISPLAY_NAME };
        Cursor cur = context.getContentResolver().query(lookupUri,
                mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return false;
    }

    /**
     * Check one phone number save on Priority Contact or not.
     *
     * @param context
     * @param phoneNumber
     * @return
     */
    public static boolean isPriorityContact(Context context, String phoneNumber) {
        DatabaseAdapter database = new DatabaseAdapter(context);
        ArrayList<PriorityContactModel> listcontacts = database
                .getAllPriorityContact();
        for (PriorityContactModel contact : listcontacts) {
            ArrayList<String> phoneNumberPriority = MyFileManager
                    .getPhoneNumber(context, contact.getUriContact()
                            .getLastPathSegment());
            for (String phone : phoneNumberPriority) {
                if (phoneNumber != null
                        && PhoneNumberUtils
                        .compare(context, phoneNumber, phone)) {
                    return true;
                }
            }

        }
        return false;
    }

    /**
     * Get audio source from setting
     *
     * @return
     */
    public static int getAudioSource(Context context) {
        int fromSeting = 0;
        try {
            fromSeting = Integer.parseInt(PreferUtils.getStringPreferences(context,
                    MyConstants.KEY_AUDIO_SOURCE));
        } catch (NumberFormatException e) {
            // TODO: handle exception
            if (MyConstants.DEBUG)
                Log.e(MyConstants.TAG,
                        "getAudioSource : NumberFormatException :fromSeting = "
                                + fromSeting);
        }
        switch (fromSeting) {

            case MyConstants.VOICE_CALL:
                return MediaRecorder.AudioSource.VOICE_CALL;
            case MyConstants.MIC:
                return MediaRecorder.AudioSource.MIC;
            case MyConstants.VOICE_COMMUNICATION:
                return MediaRecorder.AudioSource.VOICE_COMMUNICATION;
            case MyConstants.CAMCORDER:
                return MediaRecorder.AudioSource.CAMCORDER;
            case MyConstants.VOICE_UPLINK:
                return MediaRecorder.AudioSource.VOICE_UPLINK;
            case MyConstants.VOICE_DOWNLINK:
                return MediaRecorder.AudioSource.VOICE_DOWNLINK;
            default:
                return MediaRecorder.AudioSource.VOICE_CALL;
        }
    }
    public static int getDurationAudioFile(Context context, String pathAudioFile) {
        MediaPlayer mp = MediaPlayer.create(context, Uri.parse(pathAudioFile));
        int duration = -1;
        if (mp != null) {
            duration = mp.getDuration();
            mp.release();
        }

        return duration;
    }

    /**
     * Get duration of one AudioFile.
     *
     * @param duration
     * @param isNumformat
     * @return : String
     */
    public static String formatDurationAudioFile(int duration,
                                                 boolean isNumformat) {
		/* convert millis to appropriate time */
        if (!isNumformat)
            return String.format(Locale.US,
                    "%d min,%d sec",
                    TimeUnit.MILLISECONDS.toMinutes(duration),
                    TimeUnit.MILLISECONDS.toSeconds(duration)
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                            .toMinutes(duration)));
        else
            return String.format(Locale.US,
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(duration),
                    TimeUnit.MILLISECONDS.toSeconds(duration)
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                            .toMinutes(duration)));
    }

    public static void deleteOldestRecord(Context context) {
        DatabaseAdapter database = new DatabaseAdapter(context);
        RecordModel record = database
                .getOldestRecord(DatabaseAdapter.TABLE_INBOX);
        database.deleteRecord(record, DatabaseAdapter.TABLE_INBOX);
        database.deleteSearchItemIndex(record);
        MyFileManager.deleteFile(record.getPath());
    }

    @SafeVarargs
    public static <T> void executeAsyncTask(AsyncTask<T, ?, ?> task, T... params) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            task.execute(params);
        }
    }
    private static Toast mToast = null;

    /** Show Toast */
    public static void showToast(Context context, String message) {
        if (mToast == null) {
            mToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        } else {
            mToast.setText(message);
        }
        mToast.show();
    }
    /**
     * Hide or show soft keyboard
     *
     * @param context
     * @param view
     * @param isShow
     */
    public static void showOrHideKeyboard(final Context context,
                                          final View view, boolean isShow) {
        if (isShow) {
            if (view!=null) {
                view.requestFocus();
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager keyboard = (InputMethodManager) context
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        keyboard.showSoftInput(view, 0);
                    }
                }, 100);
            }
        } else {
            if(view!=null) {
                view.requestFocus();
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager keyboard = (InputMethodManager) context
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        keyboard.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }, 100);
            }
        }
    }
    /**
     * Convert bytes unit to KB or MB or GB...
     *
     * @param bytes
     * @param si
     * @return : String date
     */
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit)
            return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1)
                + (si ? "" : "i");
        return String.format(Locale.getDefault(),"%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
    /**
     * Get version of application from Manifest file
     *
     * @param context
     * @return
     */
    public static String getVersion(Context context) {
        String versionName = "1.0";
        try {
            versionName = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
        }
        return versionName;
    }

    public static int calculatePercent (double value, double total){
        return (int)((value * 100.0f) / total);
    }

    /**
     * Delete all record from INBOX table and SEARCH table
     *
     * @param context
     */
    public static void clearData(Context context) {
        DatabaseAdapter database = DatabaseAdapter.getInstance(context);
        ArrayList<RecordModel> allRecord = database
                .getListRecord(DatabaseAdapter.TABLE_INBOX);
        for (RecordModel record : allRecord) {
            MyFileManager.deleteFile(record.getPath());
        }
        database.deleteAllRecord(DatabaseAdapter.TABLE_INBOX);
        database.deleteAllRecord(DatabaseAdapter.TABLE_SEARCH_INDEX);
    }

    public static void deleteOlderRecord(Context context, int numberRecord) {
        DatabaseAdapter database = new DatabaseAdapter(context);
        ArrayList<RecordModel> listRecordNeedDelete = database.getOlderRecord(
                DatabaseAdapter.TABLE_INBOX, numberRecord);
        for (RecordModel record : listRecordNeedDelete) {
            database.deleteRecord(record, DatabaseAdapter.TABLE_INBOX);
            database.deleteSearchItemIndex(record);
            MyFileManager.deleteFile(record.getPath());
        }
    }

    /**
     * Convert ArrayList to String as Example: <br>
     * List<String> ids = new ArrayList<String>();<br>
     * ids.add("1");<br>
     * ids.add("2");<br>
     * ids.add("3");<br>
     * ids.add("4");<br>
     * Now i want an output from this list as 1,2,3,4 without explicitly
     * iterating over it.<br>
     *
     * @param data
     * @return
     */
    public static String convertArrayListToString(ArrayList<String> data) {
        return TextUtils.join(",", data);
    }
    /**
     * generate a MD5 Hash
     *
     * @param text
     * @return MD5 string
     */
    public static final String md5Digest(final String text) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(text.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            int messageDigestLenght = messageDigest.length;
            for (int i = 0; i < messageDigestLenght; i++) {
                String hashedData = Integer
                        .toHexString(0xFF & messageDigest[i]);
                while (hashedData.length() < 2)
                    hashedData = "0" + hashedData;
                hexString.append(hashedData);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return ""; // if text is null then return nothing
    }
    /**
     * Compare 2 edittext
     *
     * @param edt1
     * @param edt2
     * @return
     */
    public static boolean ConpareEdit(EditText edt1, EditText edt2) {
        if (edt1.getText().toString().trim()
                .matches(edt2.getText().toString().trim())) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * Check one Edittext have any text or not ?
     *
     * @param edt
     * @return
     */
    public static boolean isEditTextEmpty(EditText edt) {
        if (edt == null)
            return true;
        String pass = edt.getText().toString().trim();
        if (pass.matches(""))
            return true;
        else
            return false;
    }
}
