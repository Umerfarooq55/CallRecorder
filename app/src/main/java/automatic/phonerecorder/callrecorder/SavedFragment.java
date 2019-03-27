package automatic.phonerecorder.callrecorder;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import automatic.phonerecorder.callrecorder.adapter.DatabaseAdapter;
import automatic.phonerecorder.callrecorder.adapter.InboxAdapter;
import automatic.phonerecorder.callrecorder.controller.UploadFile;
import automatic.phonerecorder.callrecorder.model.RecordModel;
import automatic.phonerecorder.callrecorder.utils.MyConstants;
import automatic.phonerecorder.callrecorder.utils.PreferUtils;
import automatic.phonerecorder.callrecorder.utils.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anh Son on 6/8/2016.
 */
public class SavedFragment extends Fragment implements InboxAdapter.ViewHolder.ClickListener {

    public static final String TAG = "InboxAdapter";

    private Context mContext;
    private Activity mActivity;

    private DatabaseAdapter mDatabase;
    private ArrayList<RecordModel> mListSavedRecord;
    private InboxAdapter mSavedAdapter;
    private RecyclerView mrListRecordRv;
    private TextView mNoContentLayout;

    private ActionMode mActionMode;
    private ActionModeCallback mActionModeCallback = new ActionModeCallback();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this.getActivity();
        mActivity = this.getActivity();
        mDatabase = DatabaseAdapter.getInstance(mContext);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.saved_layout, container, false);
        mNoContentLayout = (TextView)v.findViewById(R.id.fragment_saved_txt_emtry_content);
        // RecyclerView with sample data
        mrListRecordRv = (RecyclerView) v.findViewById(R.id.fragment_saved_rv);
        mrListRecordRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mrListRecordRv.setHasFixedSize(true);
        Utilities.executeAsyncTask(new LoadListSavedRecord());

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyConstants.ACTION_BROADCAST_SAVED_INTENT_UPDATE_LIST_RECORD);
        intentFilter.addAction(MyConstants.ACTION_BROADCAST_SAVED_INTENT_UPDATE_NOTE);
        mContext.registerReceiver(mMessageUpdateReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }

    @Override
    public void onPause() {
        mContext.unregisterReceiver(mMessageUpdateReceiver);
        super.onPause();
    }

    @Override
    public void onItemClicked(int position) {
        if (mActionMode != null) {
            myToggleSelection(position);
        } else {
            playRecord(position,mSavedAdapter.getRecorderModel(position));
        }

    }
    /**
     * Start player
     * @param record
     */
    private void playRecord(int position,RecordModel record){
        File file = new File(record.getPath());
        if(!file.exists()){
            Utilities.showToast(mContext, getString(R.string.file_is_not_exist));
        }else {
            Intent playIntent = new Intent(mContext, PlayerActivity.class);
            playIntent.putExtra(MyConstants.KEY_SEND_RECORD_TO_PLAYER,record);
            playIntent.putExtra(MyConstants.KEY_ACTIVITY, MyConstants.MAIN_ACTIVITY);
            playIntent.putExtra(MyConstants.KEY_RECORD_TYPE_PLAY, MyConstants.FROM_FAVORITE);
            playIntent.putExtra("play_position",position);
            startActivity(playIntent);
            CallRecorderApp.isNeedShowPasscode = false;
        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        if (mActionMode == null) {
            mActionMode = ((MainActivity)getActivity()).startSupportActionMode(mActionModeCallback);
        }

        myToggleSelection(position);
        return  true;
    }

    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.menu_multichoice_saved_list, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                // This is to highlight the status bar and distinguish it from the action bar,
                // as the action bar while in the action mode is colored app_green_dark
                mActivity.getWindow().setStatusBarColor(getResources().getColor(R.color.toolbarBackground));
            }
            return false;
        }


        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            List<Integer> selectedItemPositions = mSavedAdapter.getSelectedItems();
            switch (menuItem.getItemId()) {
                case R.id.menu_delete:
                    for(int i = selectedItemPositions.size() - 1; i >= 0; i--){
                        mDatabase.deleteSearchItemIndex(mSavedAdapter.getRecorderModel(selectedItemPositions.get(i)));
                    }
                    mSavedAdapter.removeItems(selectedItemPositions,true);
                    actionMode.finish();
                    return true;
                case R.id.menu_upload:
                    if (PreferUtils.getbooleanPreferences(mContext, MyConstants.IS_DROPBOX_LINKED)) {
                        for(int i = selectedItemPositions.size() - 1; i >= 0; i--){
                            new UploadFile(mContext, CloudActivity.getInstanceDropbox(mContext))
                                    .execute(MyConstants.MODE_UPLOAD_ONE_FILE, "", mSavedAdapter.getRecorderModel(selectedItemPositions.get(i)).getPath());
                        }
                    }else {
                        Utilities.showToast(mContext,getString(R.string.unlink_dropbox));
                    }
                    actionMode.finish();
                    return true;
                case R.id.menu_share:
                    ArrayList<Uri> recordUris = new ArrayList<>();
                    for(int i = selectedItemPositions.size() - 1; i >= 0; i--){
                        Uri uri = Uri.fromFile(new File(mSavedAdapter.getRecorderModel(selectedItemPositions.get(i)).getPath()));
                        recordUris.add(uri);
                    }
                    Intent share = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    share.setType("audio/*");
                    share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, recordUris);
                    startActivity(Intent.createChooser(share,getString(R.string.share_this_record_dialog_title)));
                    actionMode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mSavedAdapter.clearSelections();
            mActionMode = null;
        }
    }


    public class LoadListSavedRecord extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... mode) {
            // TODO Auto-generated method stub
            //For test performance
            mListSavedRecord = mDatabase.getListRecord(DatabaseAdapter.TABLE_SAVE_RECORD);

            if(!mListSavedRecord.isEmpty()){
                mSavedAdapter = new InboxAdapter(mContext,mListSavedRecord,SavedFragment.this);
                mSavedAdapter.setmTypeAdapter(MyConstants.TYPE_ADAPTER_SAVED_FRAGMENT);
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if(mActivity !=null ) mActivity.setProgressBarIndeterminateVisibility(false);
            if(mListSavedRecord != null && mListSavedRecord.isEmpty() && mNoContentLayout != null){
                mNoContentLayout.setVisibility(View.VISIBLE);
                mNoContentLayout.setText(R.string.empty_histoty_page);
                mrListRecordRv.setVisibility(View.INVISIBLE);
            }else if(mNoContentLayout != null){
                mNoContentLayout.setVisibility(View.INVISIBLE);
                mrListRecordRv.setVisibility(View.VISIBLE);
                mrListRecordRv.setAdapter(mSavedAdapter);
                mrListRecordRv.setLayoutManager(new LinearLayoutManager(getActivity()));
            }

        }
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            if(mActivity !=null ) mActivity.setProgressBarIndeterminateVisibility(true);
        }

    }

    private void myToggleSelection(int idx) {
        mSavedAdapter.toggleSelection(idx);
        int selectedCount = mSavedAdapter.getSelectedItemCount();
        if(selectedCount <= 0){
            mActionMode.finish();
        } else {
            String title = selectedCount + " " + getString(R.string.selected_count);
            mActionMode.setTitle(title);
        }

    }

    private BroadcastReceiver mMessageUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if(MyConstants.DEBUG) Log.i(MyConstants.TAG , "mMessageUpdateReceiver : Action : " +action);
            if(action ==null){
                return;
            }
            int positionNeedUpdate = intent.getIntExtra("play_position",-1);
            if(action.equals(MyConstants.ACTION_BROADCAST_SAVED_INTENT_UPDATE_NOTE)){
                String updateNote = intent.getStringExtra("player_update_note");
                mSavedAdapter.notifyUpdateNote(positionNeedUpdate,updateNote);

            } else if(action.equals(MyConstants.ACTION_BROADCAST_SAVED_INTENT_UPDATE_LIST_RECORD)){
                Utilities.executeAsyncTask(new LoadListSavedRecord());
            }
        }

    };

}