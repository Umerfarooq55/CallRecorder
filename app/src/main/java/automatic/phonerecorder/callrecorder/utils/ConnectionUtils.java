package automatic.phonerecorder.callrecorder.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by Anh Son on 6/10/2016.
 */
public class ConnectionUtils {
    /**
     * Checking for all possible internet providers
     * **/
    @SuppressLint("NewApi")
    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (AndroidUtils.isAtLeastL()) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        }else {
            if (connectivityManager != null) {
                //noinspection deprecation
                @SuppressWarnings("deprecation")
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            Log.d(MyConstants.TAG,
                                    "NETWORKNAME: " + anInfo.getTypeName());
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    /**
     * Check status for Mobile data connect
     *
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
    public static boolean isMobileAvailable(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr != null) {
            NetworkInfo ni = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (ni != null) {
                return ni.isConnected();
            }
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    public static boolean isWifiAvailable(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr != null) {
            NetworkInfo ni = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (ni != null) {
                return ni.isConnected();
            }
        }
        return false;
    }
}
