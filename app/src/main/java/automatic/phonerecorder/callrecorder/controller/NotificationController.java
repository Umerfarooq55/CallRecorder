package automatic.phonerecorder.callrecorder.controller;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import automatic.phonerecorder.callrecorder.MainActivity;

public class NotificationController {
    private Context mContext;
    private int NOTIFICATION_ID = 1987;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private NotificationCompat.Builder mNotification;
    private NotificationManager mNotificationManager;
    private PendingIntent mContentIntent;
    
    public NotificationController(Context context)
    {
        mContext = context;
    }
 

    /**
     * Show notification 
     * @param icon : Ex: R.drawable.ic_recording
     * @param contentText : Initial text that appears in the status bar
     * @param contentTitle : create the content which is shown in the notification pulldown
     * @param : contentText : Text of the notification in the pull down
     */
	public void createNotification(int notificationId,int icon,String contentText,String contentTitle) {
    	//get the notification manager
        Log.d("Oreo","41");

    }
	public NotificationCompat.Builder createNotificationForeground(int notificationId,int icon,String contentText,String contentTitle ){
		mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        long when = System.currentTimeMillis();
        mNotification = new NotificationCompat.Builder(mContext);
        mNotification.setContentTitle(contentTitle);
        mNotification.setContentText(contentText);
        mNotification.setSmallIcon(icon);
        mNotification.setWhen(when);
        //you have to set a PendingIntent on a notification to tell the system what you want it to do when the notification is selected
        //I don't want to use this here so I'm just creating a blank one
        Intent notificationIntent = new Intent();

        mContentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);
        mNotification.setContentIntent(mContentIntent);
        return mNotification;
	}
	public void createNoRecordWarningNotification(int icon ,String contentText,String contentTitle){
		//get the notification manager
        Log.d("Oreo","40");
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        long when = System.currentTimeMillis();
        mNotification = new NotificationCompat.Builder(mContext);
        mNotification.setContentTitle(contentTitle);
        mNotification.setContentText(contentText);
        mNotification.setSmallIcon(icon);
        mNotification.setWhen(when);
        //you have to set a PendingIntent on a notification to tell the system what you want it to do when the notification is selected
        //I don't want to use this here so I'm just creating a blank one
        Intent notificationIntent = new Intent(mContext, MainActivity.class);
        notificationIntent.putExtra("warning_no_support_key", true);
        mContentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);
        mNotification.setContentIntent(mContentIntent);
        //add the additional content and intent to the notification
        //mNotification.setLatestEventInfo(mContext, contentTitle, contentText, mContentIntent);

        //make this notification appear in the 'Ongoing events' section
//        mNotification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_AUTO_CANCEL;
        mNotification.setAutoCancel(false);
        //show the notification
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
//            notificationChannel.enableLights(true);
//            notificationChannel.setLightColor(Color.RED);
//            notificationChannel.enableVibration(true);
//            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//            assert mNotificationManager != null;
//            mNotification.setChannelId(NOTIFICATION_CHANNEL_ID);
//            mNotificationManager.createNotificationChannel(notificationChannel);
        }else{
            mNotificationManager.notify(NOTIFICATION_ID, mNotification.build());
        }

	}
 
    /**
     * called when the background task is complete, this removes the notification from the status bar.
     * We could also use this to add a new �task complete� notification
     */
    public void completed(int notificationId)    {
        Log.d("Oreo","20");
        //remove the notification from the status bar
    	NotificationManager notificationManager =
    			(NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    	notificationManager.cancel(notificationId);
    }
}
