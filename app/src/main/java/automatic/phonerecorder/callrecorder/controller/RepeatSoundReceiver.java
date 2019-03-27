package automatic.phonerecorder.callrecorder.controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;

/**
 * When the alarm fires, this WakefulBroadcastReceiver receives the broadcast Intent 
 * and then starts the IntentService {@code SampleSchedulingService} to do some work.
 */
public class RepeatSoundReceiver extends WakefulBroadcastReceiver {
	public final String TAG = "RepeatSoundReceiver";
	private final int REQUEST_CODE_BEEP_SOUND = 1523;
	private final int TIME_REPEATE_BEEP_SOUND = 30;
  
    @Override
    public void onReceive(Context context, Intent intent) {
        // BEGIN_INCLUDE(alarm_onreceive)
    	Log.d(TAG, "onReceive");
    	 final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_VOICE_CALL, 100);
         tg.startTone(ToneGenerator.TONE_PROP_BEEP2);
        // END_INCLUDE(alarm_onreceive)
    }

    // BEGIN_INCLUDE(set_alarm)

    /**
     * Set alarm for repeat sound each 30 seconds.
     * @param context
     */
    public void setAlarmBeepSound(Context context) {
    	AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, RepeatSoundReceiver.class);
		PendingIntent alarmIntent = PendingIntent.getBroadcast(context,
				REQUEST_CODE_BEEP_SOUND, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();

        alarmMgr.setRepeating(AlarmManager.RTC,
                calendar.getTimeInMillis(), TIME_REPEATE_BEEP_SOUND * 1000, alarmIntent);
    }
    // END_INCLUDE(set_alarm)

    /**
     * Cancels the alarm beep sound
     * @param context
     */
    // BEGIN_INCLUDE(cancel_alarm)
    public void cancelAlarmBeepSound(Context context) {
        // If the alarm has been set, cancel it.
    	AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, RepeatSoundReceiver.class);
		PendingIntent alarmIntent = PendingIntent.getBroadcast(context,
				REQUEST_CODE_BEEP_SOUND, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
        
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
            alarmIntent.cancel();
        }
       
    }
}
