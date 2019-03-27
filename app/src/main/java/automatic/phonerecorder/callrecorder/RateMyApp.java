package automatic.phonerecorder.callrecorder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by retina on 08/02/2017.
 */

public class RateMyApp {
    private static String APP_TITLE;
    private static String APP_PACKAGENAME;

    //private final static int DAYS_UNTIL_PROMPT = 2;
    private final static int LAUNCHES_UNTIL_PROMPT = 14;

    // If true, both the specified number of days and number of launches must occur before
    // the dialog will be presented to the user. Otherwise, it's just one or the other.
    private final static boolean DAYS_AND_LAUNCHES = false;

    private Activity callerActivity;

    private AlertDialog ratemyappDialog;
    public RateMyApp(Activity caller) {
        callerActivity = caller;
        APP_TITLE = getApplicationName();
        APP_PACKAGENAME = getPackageName();
    }

    public void app_launched() {
        SharedPreferences prefs = callerActivity.getSharedPreferences("apprater", Context.MODE_PRIVATE);
        if (prefs.getBoolean("dontshowagain", false))
        {
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter

        long aumento=prefs.getLong("launch_count", 0)+1;
        editor.putLong("launch_count", aumento);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }

        boolean exceedsSpecifiedLaunches = aumento >= LAUNCHES_UNTIL_PROMPT;
        /*boolean exceedsDaysSinceFirstLaunch = System.currentTimeMillis() >= date_firstLaunch +
                (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000);*/

        // Wait until specified number of launches or until specified number of days have passed
        if (exceedsSpecifiedLaunches && !DAYS_AND_LAUNCHES) {
            showRateDialog(editor);
        }
        else if (exceedsSpecifiedLaunches && DAYS_AND_LAUNCHES) {
            showRateDialog(editor);
        }

        editor.apply();
    }

    public void showRateDialog(final SharedPreferences.Editor editor) {
        if(APP_PACKAGENAME.equals("")) return;

        AlertDialog.Builder builder;

        // Inflate the layout
        LayoutInflater inflater = callerActivity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.ratemyapp_dialog, null);

        // Grab elements of the layout
        Button rateButton = (Button) layout.findViewById(R.id.ratemyapp_dialog_accept_button);
        Button laterButton = (Button) layout.findViewById(R.id.ratemyapp_dialog_later_button);
        Button cancelButton = (Button) layout.findViewById(R.id.ratemyapp_dialog_cancel_button);
        TextView titleTextView = (TextView) layout.findViewById(R.id.ratemyapp_dialog_title_textview);
        TextView messageTextView = (TextView) layout.findViewById(R.id.ratemyapp_dialog_info);

        titleTextView.setText(callerActivity.getString(R.string.califica)+" " + APP_TITLE);


        String msg = callerActivity.getString(R.string.si)+ " " + APP_TITLE + "" + callerActivity.getString(R.string.please);

        Spanned message = Html.fromHtml(msg);

        messageTextView.setText(message);

        rateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                try {
                    callerActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PACKAGENAME)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    callerActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + APP_PACKAGENAME)));
                }

                ratemyappDialog.dismiss();
            }
        });

        laterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editor != null) {
                    editor.putLong("launch_count", 0);
                    editor.apply();
                }
                ratemyappDialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.apply();
                }
                ratemyappDialog.dismiss();
            }
        });

        builder = new AlertDialog.Builder(callerActivity);
        builder.setView(layout);

        ratemyappDialog = builder.create();
        ratemyappDialog.show();
    }

    private String getPackageName(){
        String pkgName = "";
        try {
            PackageManager manager = callerActivity.getPackageManager();
            PackageInfo info = manager.getPackageInfo(callerActivity.getPackageName(), 0);
            pkgName = info.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pkgName;
    }

    private String getApplicationName(){
        String appName = "";
        try {
            PackageManager manager = callerActivity.getPackageManager();
            PackageInfo info = manager.getPackageInfo(callerActivity.getPackageName(), 0);
            appName = callerActivity.getResources().getString(info.applicationInfo.labelRes);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return appName;
    }
}
