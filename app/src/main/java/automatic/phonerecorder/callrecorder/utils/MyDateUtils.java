package automatic.phonerecorder.callrecorder.utils;

import android.annotation.SuppressLint;
import android.content.Context;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import automatic.phonerecorder.callrecorder.R;

public class MyDateUtils {

	/**
	 *  Format time as 24h or 12h example : 13:01 or 1:01 PM 
	 * @param context
	 * @param milliseconds
	 * @return : String time
	 */
	public static String getTimeFromMilliseconds (Context context,long milliseconds){
		String TIME_FORMAT_12H = "hh:mm aa";
		String TIME_FORMAT_24H = "HH:mm";
		
		String time = null;
		if(PreferUtils.getbooleanPreferences(context, MyConstants.KEY_USE_24_HOUR_FORMAT)){
			SimpleDateFormat TimeFormat24H = new SimpleDateFormat(TIME_FORMAT_24H,Locale.getDefault());
			Calendar cl1 = Calendar.getInstance();
			cl1.setTimeInMillis(milliseconds);  //here your time in miliseconds
//			time = ""+cl1.get(Calendar.HOUR_OF_DAY) + ":" + cl.get(Calendar.MINUTE);
			time = TimeFormat24H.format(cl1.getTime());
		}else{
			SimpleDateFormat TimeFormat12H = new SimpleDateFormat(TIME_FORMAT_12H,Locale.getDefault());
			Calendar cl2 = Calendar.getInstance();
			cl2.setTimeInMillis(milliseconds);  //here your time in miliseconds
			time = TimeFormat12H.format(cl2.getTime());
		}
		return time;
	}
	/**
	 * Format date as form example : Thusday 12/25/2015
	 * @param milliseconds
	 * @return
	 */
	public static String formatDateFromMilliseconds(long milliseconds){
		Date date = new Date(); 
	    date.setTime(milliseconds);
	    String formattedDate=new SimpleDateFormat("EE MM/dd/yyyy",Locale.getDefault()).format(date);
	    return formattedDate;
	}
	/**
	 * Format millisecond to human can read easilier.
	 * @param milliseconds
	 * @return
	 */
	@SuppressLint("StringFormatInvalid")
	public static String formatDuration(Context context, long milliseconds){
		String message = "";
	    if(milliseconds >= 1000){
	    	int seconds = (int) (milliseconds / 1000) % 60;
	        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
	        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
	        if(hours != 0){
	        	message = String.format(Locale.getDefault(), "%d "+context.getString(R.string.hours), hours);
	        }else if (hours == 0 && minutes != 0){
	        	message = String.format(Locale.getDefault(), "%d "+context.getString(R.string.minutes), minutes);
	        } else if(hours == 0 && minutes == 0 & seconds != 0){
	        	message = String.format(Locale.getDefault(), "%d "+context.getString(R.string.seconds), seconds);
	        }else {
	        	message = "1 "+context.getString(R.string.seconds);
			}
	    }else {
	    	message = "1 "+context.getString(R.string.seconds);
		}
	    
		return message;
	}
	public static String getHumanTimeFormatFromMilliseconds(String millisecondS){
	    String message = "";
	    long milliseconds = Long.valueOf(millisecondS);
	    if (milliseconds >= 1000){
	        int seconds = (int) (milliseconds / 1000) % 60;
	        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
	        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
	        int days = (int) (milliseconds / (1000 * 60 * 60 * 24));
	        if((days == 0) && (hours != 0)){
	            message = String.format("%d hours %d minutes %d seconds ago", hours, minutes, seconds);
	        }else if((hours == 0) && (minutes != 0)){
	            message = String.format("%d minutes %d seconds ago", minutes, seconds);
	        }else if((days == 0) && (hours == 0) && (minutes == 0)){
	            message = String.format("%d seconds ago", seconds);
	        }else{
	            message = String.format("%d days %d hours %d minutes %d seconds ago", days, hours, minutes, seconds);
	        }
	    } else{
	        message = "Less than a second ago.";
	    }
	    return message;
	}
}
