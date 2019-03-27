package automatic.phonerecorder.callrecorder;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import automatic.phonerecorder.callrecorder.adapter.DatabaseAdapter;
import automatic.phonerecorder.callrecorder.adapter.InboxAdapter;
import automatic.phonerecorder.callrecorder.model.RecordModel;
import automatic.phonerecorder.callrecorder.utils.MyConstants;
import automatic.phonerecorder.callrecorder.utils.PreferUtils;
import automatic.phonerecorder.callrecorder.utils.Utilities;

import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Anh Son on 6/10/2016.
 */
public class ContactHistoryActivity extends AppCompatActivity implements InboxAdapter.ViewHolder.ClickListener {
    private Context mContext;

    private DatabaseAdapter mDatabase;
    private ArrayList<RecordModel> mListRecord;
    private InboxAdapter mHistoryAdapter;
    private RecyclerView mrContactHistoryRv;
    private TextView mEmptyHistory;
    private String mPhoneNumber;

    //passcode screen
    private View mPasscodeScreen;
    private PasscodeScreen mPasscode;
    // For admob
    private AdView adView;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        setContentView(R.layout.activity_history_contact);
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.contact_history_title));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                CallRecorderApp.isNeedShowPasscode = false;
            }
        });

        Bundle data = getIntent().getExtras();
        if(data != null ){
            mPhoneNumber = data.getString("phonenumber_key");
        }
        mDatabase = DatabaseAdapter.getInstance(mContext);
        initUI();
        initPasscodeUI();
        //initAdsAdmob();

    }
    /*private void initAdsAdmob(){
        // Look up the AdView as a resource and load a request.
        adView = (AdView) findViewById(R.id.adView_main);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(MyConstants.TEST_DEVICE_ID)
                .build();
        adView.loadAd(adRequest);
    }*/
    /*private void resumeAds(){
        adView.resume();
    }
    private void pauseAds(){
        adView.pause();
    }*/


    @Override
    protected void onResume() {
        super.onResume();
        if(CallRecorderApp.isNeedShowPasscode && PreferUtils.getbooleanPreferences(this, MyConstants.KEY_PRIVATE_MODE)
                && !PreferUtils.getbooleanPreferences(mContext, MyConstants.KEY_IS_LOGINED)){
            mPasscode.showPasscode();
        }
        //resumeAds();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //pauseAds();
    }

    @Override
    protected void onStop() {
        super.onStop();
        CallRecorderApp.isNeedShowPasscode = true;
        PreferUtils.savebooleanPreferences(mContext,MyConstants.KEY_IS_LOGINED, false);
    }


    private void initUI(){
        mEmptyHistory = (TextView) findViewById(R.id.contact_history_txt_emtry_content);
        mrContactHistoryRv = (RecyclerView) findViewById(R.id.history_contact_rv);
        mrContactHistoryRv.setLayoutManager(new LinearLayoutManager(this));
        mrContactHistoryRv.setHasFixedSize(true);
        Utilities.executeAsyncTask(new LoadListContactHistory(),mPhoneNumber);
    }

    @Override
    public void onItemClicked(int position) {

    }

    @Override
    public boolean onItemLongClicked(int position) {
        return false;
    }

    public class LoadListContactHistory extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... phonenumber) {
            // TODO Auto-generated method stub
/*            if(FROM_RECORD_TYPE == MyConstants.FROM_RECORDING || FROM_RECORD_TYPE == MyConstants.FROM_SEARCH){
                mListRecord = mDatabase.getListRecordbyPhoneNumber(
                        DatabaseAdapter.TABLE_INBOX, phonenumber[0]);
                if(MyConstants.DEBUG) Log.d(MyConstants.TAG, "mListRecord : "+mListRecord.size()
                        + " phonenumber :"+phonenumber[0]);
            }else if (FROM_RECORD_TYPE == MyConstants.FROM_FAVORITE) {
                mListRecord = mDatabase.getListRecordbyPhoneNumber(
                        DatabaseAdapter.TABLE_SAVE_RECORD, phonenumber[0]);
                if(MyConstants.DEBUG) Log.d(MyConstants.TAG, "mListRecord : "+mListRecord.size()
                        + " phonenumber :"+phonenumber[0]);
            }*/
            mListRecord = mDatabase.getListRecordbyPhoneNumber(
                    DatabaseAdapter.TABLE_INBOX, phonenumber[0]);
            mListRecord.addAll(mDatabase.getListRecordbyPhoneNumber(
                    DatabaseAdapter.TABLE_SAVE_RECORD, phonenumber[0]));
            if(!mListRecord.isEmpty()){
                mHistoryAdapter = new InboxAdapter (mContext,mListRecord,ContactHistoryActivity.this);
                mHistoryAdapter.setmTypeAdapter(MyConstants.TYPE_ADAPTER_INBOX_FRAGMENT);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            setProgressBarIndeterminateVisibility(false);
            if (mListRecord != null && mListRecord.size() < 1 && mEmptyHistory != null) {
                mEmptyHistory.setVisibility(View.VISIBLE);
                mrContactHistoryRv.setVisibility(View.GONE);
            } else if (mrContactHistoryRv != null && mEmptyHistory != null) {
                mEmptyHistory.setVisibility(View.GONE);
                mrContactHistoryRv.setVisibility(View.VISIBLE);
                mrContactHistoryRv.setAdapter(mHistoryAdapter);
            }

        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            setProgressBarIndeterminateVisibility(true);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CallRecorderApp.isNeedShowPasscode = false;
    }
    private void initPasscodeUI(){
        mPasscodeScreen = findViewById(R.id.contact_history_passcode);
        Button number_0 = (Button) findViewById(R.id.numpad_0);
        Button number_1 = (Button) findViewById(R.id.numpad_1);
        Button number_2 = (Button) findViewById(R.id.numpad_2);
        Button number_3 = (Button) findViewById(R.id.numpad_3);
        Button number_4 = (Button) findViewById(R.id.numpad_4);
        Button number_5 = (Button) findViewById(R.id.numpad_5);
        Button number_6 = (Button) findViewById(R.id.numpad_6);
        Button number_7 = (Button) findViewById(R.id.numpad_7);
        Button number_8 = (Button) findViewById(R.id.numpad_8);
        Button number_9 = (Button) findViewById(R.id.numpad_9);
        ImageButton number_erase = (ImageButton) findViewById(R.id.button_erase);

        EditText pinfield_1 = (EditText) findViewById(R.id.pin_field_1);
        EditText pinfield_2 = (EditText) findViewById(R.id.pin_field_2);
        EditText pinfield_3 = (EditText) findViewById(R.id.pin_field_3);
        EditText pinfield_4 = (EditText) findViewById(R.id.pin_field_4);
        TextView resetPassword = (TextView) findViewById(R.id.passcode_txt_reset_password);
        mPasscode = new PasscodeScreen();
        mPasscode.initLayout(this,mPasscodeScreen,number_0, number_1, number_2, number_3,
                number_4, number_5, number_6, number_7, number_8, number_9,
                number_erase,pinfield_1,pinfield_2,pinfield_3,pinfield_4,resetPassword);
    }
}
