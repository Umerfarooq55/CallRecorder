package automatic.phonerecorder.callrecorder.utils;

import com.dropbox.client2.session.Session;

/**
 * Created by Anh Son on 6/8/2016.
 */
public interface MyConstants {
    // Common debug
    boolean DEBUG                          = true;
    boolean isAds                          = false;
    String TEST_DEVICE_ID                  = "AB4B5A5E730E688BCC7C0E8D551611F3";
    String TAG                             = "S_CallRecorder";
    int TRUE                               = 1;
    int FALSE                              = 0;

    // Key setting for Preferences
    String PREFS_NAME                      = "Setting_prefs";
    String SERVICE_ENABLED                 = "ServiceEnabled";
    String KEY_ENABLE_NOTIFICATION         = "is_enable_notication";
    String KEY_NOTIFICATION_ALWAYS_ASK     = "notification_always_ask";
    String KEY_MODE_RECORDER               = "mode_recorder";
    String KEY_PRIORITY_CONTACT            = "priority_contacts_manage";
    String KEY_INBOX_SIZE                  = "inbox_size";
    String KEY_SHAKE_CANCEL_RECORD         = "is_shake_cancel_record";
    String KEY_PRIVATE_MODE                = "is_enable_private_mode";
    String KEY_USE_24_HOUR_FORMAT          = "is_use_24_hour_format";
    String KEY_BEEP_SOUND                  = "is_beep_sound_when_record";
    String KEY_ACTION_WHEN_NOTE            = "action_when_note";
    // Audio source
    String KEY_AUDIO_SOURCE                = "key_audio_source";
    int MIC                                = 6000;
    int VOICE_CALL                         = 6001;
    int VOICE_COMMUNICATION                = 6002;
    int CAMCORDER                          = 6003;
    int VOICE_RECOGNITION                  = 6004;
    int VOICE_UPLINK                       = 6005;
    int VOICE_DOWNLINK                     = 6006;
    int MODE_RECORDER_RECORD_ALL           = 100;
    int MODE_RECORDER_CONTACTS_ONLY        = 101;
    int MODE_RECORDER_PRIORITY_CONTACTS    = 102;
    int MODE_RECORDER_UNKNOW_NUMBER        = 103;

    int MAXIMUM_INBOX_SIZE_10              = 10;
    int MAXIMUM_INBOX_SIZE_50              = 50;
    int MAXIMUM_INBOX_SIZE_100             = 100;
    int MAXIMUM_INBOX_SIZE_500             = 500;
    int MAXIMUM_INBOX_SIZE_UNLIMITED       = 1000;

    String KEY_FILE_TYPE_OUTPUT            = "file_type_output";
    int FILE_TYPE_3GP                      = 1000;
    int FILE_TYPE_WAV                      = 1001;
    int FILE_TYPE_MP4                      = 1002;
    int FILE_TYPE_AMR                      = 1003;

    int AUTOMATIC_SAVE_RECORDING           = 700;
    int ASK_WHAT_TO_DO                     = 701;
    int DONT_SAVE                          = 702;

    // For utils upgrade
    String KEY_IS_THE_FIRST_OPEN_APP       = "firstTime_app_upgrade";
    String KEY_IS_THE_FIRST_NO_MEDIA       = "fistTime_hide_media";
    String KEY_OLDVERSION_DATABSE          = "key_old_version_database";

    // For engine recorder
    String COMMAND_TYPE                    = "commandType";
    String PHONE_NUMBER                    = "phoneNumber";
    String SLIENT_MODE                     = "silentMode";
    int INCOMING_CALL_STARTED              = 51;
    int OUTGOING_CALL_STARTED              = 52;
    int INCOMING_CALL_ENDED                = 53;
    int OUTGOING_CALL_ENDED                = 54;
    int MISSED_CALL                        = 55;

    // For Storage manager
    String KEY_NAME_FOLDER_SAVE_DATA       = "key_name_folder_save_data";
    String FILE_DIRECTORY                  = ".luminousrec";

    // For Dialog rate
    public static final String KEY_EXCEPTION                = "key_exception";


    //broadcast key
    String ACTION_BROADCAST_INBOX_INTENT_UPDATE_LIST_RECORD = "com.automaticcallrecorder.callrecorder314.inbox.updatelist";
    String ACTION_BROADCAST_INBOX_INTENT_UPDATE_NOTE = "com.automaticcallrecorder.callrecorder314.player.inbox.updatenote";
    String ACTION_BROADCAST_SAVED_INTENT_UPDATE_LIST_RECORD = "com.automaticcallrecorder.callrecorder314.saved.updatelist";
    String ACTION_BROADCAST_SAVED_INTENT_UPDATE_NOTE = "com.automaticcallrecorder.callrecorder314.player.saved.updatenote";
    String ACTION_BROADCAST_INTENT_DELETE_ALL = "com.automaticcallrecorder.callrecorder314.storage.deleteall";

    int TYPE_ADAPTER_INBOX_FRAGMENT = 0;
    int TYPE_ADAPTER_SAVED_FRAGMENT = 1;

    //Player
    int FROM_RECORDING                     = 10000;
    int FROM_FAVORITE                      = 10001;
    int FROM_SEARCH                        = 10002;
    int MAIN_ACTIVITY                      = 5000;
    int PRIORITY_CONTACT_ACTIVITY          = 5001;
    int FROM_SETTING                       = 5003;
    int PLAYER_ACTIVITY                    = 5004;
    String KEY_SEND_RECORD_TO_PLAYER       = "key_send_record_to_player";
    String KEY_ACTIVITY                    = "activity";
    String KEY_RECORD_TYPE_PLAY            = "key_record_type_play";
    int UPDATE_NOTE_BROADCAST = 300;
    int UPDATE_SAVED_BROADCAST = 301;

    String KEY_DONT_SHOW_AGAIN          = "key_dont_show_again";

    //For helpActivity
    String KEY_ABOUT_APP_VERSION           = "key_about_app_version";
    //String KEY_ABOUT_APP_LICENSE           = "key_about_app_license";
    String KEY_ABOUT_APP_GET_ALL_APP       = "key_about_app_get_all_app";
    String KEY_ABOUT_APP_FEEDBACK          = "key_about_app_feedback";
    //String KEY_ABOUT_APP_ON_GOOGLEPLUS     = "key_about_app_smobile_on_googleplus";
    String KEY_ABOUT_APP_PRIVACY           = "key_about_app_privacy";
    //String KEY_ABOUT_APP_TRANSLATION       = "key_about_app_translation";

    //Cloud
    String IS_DROPBOX_LINKED               = "is_dropbox_linked";
    String MODE_UPLOAD_FOLDER              = "9000";
    String MODE_UPLOAD_ONE_FILE            = "9001";
    String ACCOUNT_PREFS_NAME = "acount_prefs";
    boolean IS_ENABLE_GOOGLE_DRIVE         = true;
    String KEY_DRIVE_ACCOUNT               = "key_drive_account";
    String KEY_DROPBOX_ACCOUNT             = "key_dropbox_account";
    String KEY_AUTOMATIC_SYNC              = "cloud_sync_automatic";
    String KEY_CLOUD_SYNC_WIFI_ONLY        = "cloud_sync_wifi_only";
    String KEY_IS_THE_FIRST_SYNC_AFTER_AUTHENTICATION = "key_is_the_first_sync_after_authentication";
    String ACCESS_KEY_NAME                 = "access_key";
    String ACCESS_SECRET_NAME              = "access_secret";
    Session.AccessType ACCESS_TYPE         = Session.AccessType.DROPBOX;
    String DROPBOX_APP_KEY                 = "g6ay8ycpy7u91ar";
    String DROPBOX_APP_SECRET              = "i3xecn99hgsobwp";

    // For private mode
    public static final String KEY_PASSWORD_FOR_PRIVATE_MODE   = "password";
    public static final String KEY_IS_LOGINED                  = "is_logined";
}
