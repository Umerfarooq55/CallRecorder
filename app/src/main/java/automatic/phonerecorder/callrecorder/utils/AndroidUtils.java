/*
 * Copyright 2015, Randy Saborio & Tinbytes, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package automatic.phonerecorder.callrecorder.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;

public final class AndroidUtils {
  private static boolean sIsAtLeastM;
  private static boolean sIsAtLeastL;

  static {
    final int v = getApiVersion();
    sIsAtLeastL = v >= 21;//android.os.Build.VERSION_CODES.LOLLIPOP;
    sIsAtLeastM = v >= 23;//android.os.Build.VERSION_CODES.M
  }
  /**
   * @return The Android API version of the OS that we're currently running on.
   */
  public static int getApiVersion() {
    return android.os.Build.VERSION.SDK_INT;
  }

  /**
   * @return True if the version of Android that we're running on is at least M
   *  (API level 23).
   */
  public static boolean isAtLeastM() {
    return sIsAtLeastM;
  }
  /**
   * @return True if the version of Android that we're running on is at least L
   *  (API level 21).
   */
  public static boolean isAtLeastL() {
    return sIsAtLeastL;
  }

  public static String[] sRequiredPermissions = new String[] {
          // Required to record audio
          Manifest.permission.RECORD_AUDIO,
//         Manifest.permission.PROCESS_OUTGOING_CALLS,
          // Required for knowing the phone number, number of SIMs, etc.
          Manifest.permission.READ_PHONE_STATE,
          // This is not strictly required, but simplifies the contact picker scenarios
          Manifest.permission.READ_CONTACTS,
          // require for save record file to storage
          Manifest.permission.WRITE_EXTERNAL_STORAGE,
  };

  /**
   * Check if the app has the specified permission. If it does not, the app needs to use
   * {@link android.app.Activity #requestPermission}. Note that if it
   * returns true, it cannot return false in the same process as the OS kills the process when
   * any permission is revoked.
   * @param permission A permission from {@link android.Manifest.permission}
   */
  @TargetApi(23)
  public static boolean hasPermission(Context context, final String permission) {
    if (isAtLeastM()) {
      int permissionCheck = context.checkSelfPermission(permission);
      return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }else {
      return true;
    }
  }
  /** Does the app have all the specified permissions */
  public static boolean hasPermissions(Context context,final String[] permissions) {
    for (final String permission : permissions) {
      if (!hasPermission(context,permission)) {
        return false;
      }
    }
    return true;
  }

  /** Does the app have the minimum set of permissions required to operate. */
  public static boolean hasRequiredPermissions(Context context) {
    return hasPermissions(context,sRequiredPermissions);
  }

}
