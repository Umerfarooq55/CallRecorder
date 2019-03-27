package automatic.phonerecorder.callrecorder.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import automatic.phonerecorder.callrecorder.utils.MyConstants;
import automatic.phonerecorder.callrecorder.utils.PreferUtils;

import java.util.Date;

public class VoicePhoneReceiver extends VoicePhoneListener implements MyConstants {

	@Override
	protected void onIncomingCallStarted(Context ctx, String number, Date start) {
		if(DEBUG) Log.i(TAG, "onIncomingCallStarted : "+ ", Number :" + number);
		if(PreferUtils.getbooleanPreferences(ctx, SERVICE_ENABLED))
			startRecorderService(ctx, INCOMING_CALL_STARTED, number);
	}

	@Override
	protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
		if(DEBUG) Log.i(TAG, "onOutgoingCallStarted : "+ ", Number :" + number);
		if(DEBUG) Log.i(TAG, "SERVICE_ENABLED : "+ PreferUtils.getbooleanPreferences(ctx, SERVICE_ENABLED));
		if(PreferUtils.getbooleanPreferences(ctx, SERVICE_ENABLED))
			startRecorderService(ctx, OUTGOING_CALL_STARTED, number);
	}

	@Override
	protected void onIncomingCallEnded(Context ctx, String number, Date start,
			Date end) {
		if(DEBUG) Log.i(TAG, "onIncomingCallEnded : "+ ", Number :" + number);
		startRecorderService(ctx, INCOMING_CALL_ENDED, number);
	}

	@Override
	protected void onOutgoingCallEnded(Context ctx, String number, Date start,
			Date end) {
		if(DEBUG) Log.i(TAG, "onOutgoingCallEnded : "+ ", Number :" + number);

		startRecorderService(ctx, OUTGOING_CALL_ENDED, number);

	}

	@Override
	protected void onMissedCall(Context ctx, String number, Date start) {
		if(DEBUG) Log.i(TAG, "onMissedCall : "+ ", Number :" + number);
		if(PreferUtils.getbooleanPreferences(ctx, SERVICE_ENABLED))
		startRecorderService(ctx, MISSED_CALL, number);
	}
	

	/**
	 * Start voice recorder service
	 * 
	 * @param context
	 * @param stateCall
	 * @param phoneNumber
	 */
	private void startRecorderService(Context context, int stateCall,
			String phoneNumber) {
		Intent myIntent = new Intent(context, VoiceRecoderService.class);
		myIntent.putExtra(COMMAND_TYPE, stateCall);
		myIntent.putExtra(PHONE_NUMBER, phoneNumber);
		//context.startService(myIntent);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			context.startForegroundService(myIntent);
//		context.startService(myIntent);
		} else {
			context.startService(myIntent);
		}
	}
}
