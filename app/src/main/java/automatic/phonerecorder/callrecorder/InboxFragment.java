package automatic.phonerecorder.callrecorder;

/**
 * Created by Anh Son on 6/8/2016.
 */

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import automatic.phonerecorder.callrecorder.utils.AndroidUtils;
import automatic.phonerecorder.callrecorder.utils.MyConstants;
import automatic.phonerecorder.callrecorder.utils.PreferUtils;
import automatic.phonerecorder.callrecorder.utils.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hp1 on 21-01-2015.
 */
public class InboxFragment extends Fragment implements InboxAdapter.ViewHolder.ClickListener, MyConstants {
    private Context mContext;
    private Activity mActivity;

    private DatabaseAdapter mDatabase;
    private ArrayList<RecordModel> mListRecord;
    private InboxAdapter mInboxAdapter;
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.inbox_layout, container, false);
        mNoContentLayout = (TextView)v.findViewById(R.id.fragment_inbox_txt_emtry_content);
        // RecyclerView with sample data
        mrListRecordRv = (RecyclerView) v.findViewById(R.id.fragment_inbox_rv);
        mrListRecordRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mrListRecordRv.setHasFixedSize(true);
        if(AndroidUtils.isAtLeastM() && !AndroidUtils.hasRequiredPermissions(mContext)){
            addRuntimePermission();
        }else {
            Log.d("CallLog","isshowing");
            Utilities.executeAsyncTask(new LoadListRecord());
        }


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_BROADCAST_INBOX_INTENT_UPDATE_NOTE);
        intentFilter.addAction(ACTION_BROADCAST_INBOX_INTENT_UPDATE_LIST_RECORD);
        intentFilter.addAction(ACTION_BROADCAST_INTENT_DELETE_ALL);
        mContext.registerReceiver(mMessageUpdateReceiver, intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        mContext.unregisterReceiver(mMessageUpdateReceiver);
        super.onPause();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    private void myToggleSelection(int idx) {
        mInboxAdapter.toggleSelection(idx);
        int selectedCount = mInboxAdapter.getSelectedItemCount();
        if(selectedCount <= 0){
            mActionMode.finish();
        } else {
            String title = selectedCount + " " + getString(R.string.selected_count);
            mActionMode.setTitle(title);
        }

    }

    @Override
    public void onItemClicked(int position) {
        if (mActionMode != null) {
            myToggleSelection(position);
        } else {
            playRecord(position,mInboxAdapter.getRecorderModel(position));
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
            playIntent.putExtra(KEY_SEND_RECORD_TO_PLAYER,record);
            playIntent.putExtra(KEY_ACTIVITY, MAIN_ACTIVITY);
            playIntent.putExtra(KEY_RECORD_TYPE_PLAY, FROM_RECORDING);
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
            inflater.inflate(R.menu.menu_multichoice_inbox_list, menu);
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
            List<Integer> selectedItemPositions = mInboxAdapter.getSelectedItems();
            switch (menuItem.getItemId()) {
                case R.id.menu_delete:
                    for(int i = selectedItemPositions.size() - 1; i >= 0; i--){
                        mDatabase.deleteSearchItemIndex(mInboxAdapter.getRecorderModel(selectedItemPositions.get(i)));
                    }
                    mInboxAdapter.removeItems(selectedItemPositions,true);
                    actionMode.finish();
                    return true;

                case R.id.menu_save:
                    for(int i = selectedItemPositions.size() - 1; i >= 0; i--){
                        RecordModel record = mInboxAdapter.getRecorderModel(selectedItemPositions.get(i));
                        long index = mDatabase.addRecord(record,
                                DatabaseAdapter.TABLE_SAVE_RECORD);
                        // Need update lai ID cua record trong table search da luu trong save folder.
                        mDatabase.deleteSearchItemIndex(record);
                        mDatabase.addSearchIndex(mContext,new RecordModel(record,(int)index));
                    }
                    mInboxAdapter.removeItems(selectedItemPositions,false);
                    actionMode.finish();
                    Intent intent = new Intent();
                    intent.setAction(MyConstants.ACTION_BROADCAST_SAVED_INTENT_UPDATE_LIST_RECORD);
                    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    mContext.sendBroadcast(intent);
                    return true;
                case R.id.menu_upload:
                    if (PreferUtils.getbooleanPreferences(mContext, IS_DROPBOX_LINKED)) {
                        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                            new UploadFile(mContext, CloudActivity.getInstanceDropbox(mContext))
                                    .execute(MODE_UPLOAD_ONE_FILE, "",
                                            mInboxAdapter.getRecorderModel(selectedItemPositions.get(i)).getPath());
                        }
                    } else {
                        Utilities.showToast(mContext,getString(R.string.unlink_dropbox));
                    }

                    actionMode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mInboxAdapter.clearSelections();
            mActionMode = null;
        }
    }
    public class LoadListRecord extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... mode) {
            // TODO Auto-generated method stub
            //For test performance
            mListRecord = mDatabase.getListRecord(DatabaseAdapter.TABLE_INBOX);

            if(!mListRecord.isEmpty()){
                mInboxAdapter = new InboxAdapter (mContext,mListRecord,InboxFragment.this);
                mInboxAdapter.setmTypeAdapter(TYPE_ADAPTER_INBOX_FRAGMENT);
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if(mActivity !=null ) mActivity.setProgressBarIndeterminateVisibility(false);
            if(mListRecord!= null && mListRecord.isEmpty() && mNoContentLayout != null){
                mNoContentLayout.setVisibility(View.VISIBLE);
                mNoContentLayout.setText(R.string.empty_histoty_page);
                mrListRecordRv.setVisibility(View.INVISIBLE);
            }else if(mNoContentLayout != null){
                mNoContentLayout.setVisibility(View.INVISIBLE);
                mrListRecordRv.setVisibility(View.VISIBLE);
                mrListRecordRv.setAdapter(mInboxAdapter);
                mrListRecordRv.setLayoutManager(new LinearLayoutManager(mContext));
            }

        }
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            if(mActivity !=null ) mActivity.setProgressBarIndeterminateVisibility(true);
        }

    }

    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 102;
    private void addRuntimePermission(){
        List<String> permissionsNeeded = new ArrayList<>();
        final List<String> permissionsList = new ArrayList<>();
        if(AndroidUtils.isAtLeastM()){
            if (!addPermission(permissionsList, Manifest.permission.RECORD_AUDIO))
                permissionsNeeded.add(mContext.getString(R.string.permission_record_audio));
            if (!addPermission(permissionsList, Manifest.permission.READ_CONTACTS))
                permissionsNeeded.add(mContext.getString(R.string.permission_read_contacts));
//            if (!addPermission(permissionsList, Manifest.permission.PROCESS_OUTGOING_CALLS))
//                permissionsNeeded.add(mContext.getString(R.string.permission_detect_new_call));
            if (!addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE))
                permissionsNeeded.add(mContext.getString(R.string.permission_read_call_status));
            if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                permissionsNeeded.add(mContext.getString(R.string.permission_save_record_file));

            if (permissionsList.size() > 0) {
                if (permissionsNeeded.size() > 0) {
                    // Need Rationale
                    String message = mContext.getString(R.string.dialog_request_overlay_permision_message) +" "+ permissionsNeeded.get(0);
                    for (int i = 1; i < permissionsNeeded.size(); i++)
                        message = message + ", " + permissionsNeeded.get(i);
                    showMessageOKCancel(message,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS); // @TargetApi(23)
                                }
                            }, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    mNoContentLayout.setText(R.string.no_permission);
                                }
                            });
                    return;
                }
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS); // @TargetApi(23)
                return;
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                // Initial
                perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
//                perms.put(Manifest.permission.PROCESS_OUTGOING_CALLS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check result and update
                if (AndroidUtils.hasRequiredPermissions(mContext)) {
                    Utilities.executeAsyncTask(new LoadListRecord());
                } else {
                    mNoContentLayout.setText(R.string.no_permission);
                }

            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);//@TargetApi(23)
        }
    }
    @TargetApi(23)
    public boolean addPermission(List<String> permissionsList, String permission) {
        if (mContext.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // @TargetApi(23)
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener,
                                     DialogInterface.OnClickListener cancelListner) {
        new AlertDialog.Builder(mContext)
                .setMessage(message)
                .setPositiveButton(getString(R.string.string_ok), okListener)
                .setNegativeButton(getString(R.string.string_cancel), cancelListner)
                .create()
                .show();
    }

    private BroadcastReceiver mMessageUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if(DEBUG) Log.i(TAG , "MainActivity,mMessageUpdateReceiver : Action : " +action);
            if(action ==null){
                return;
            }
            int positionNeedUpdate = intent.getIntExtra("play_position",-1);
            if(action.equals(ACTION_BROADCAST_INBOX_INTENT_UPDATE_NOTE)){
                String updateNote = intent.getStringExtra("player_update_note");
                mInboxAdapter.notifyUpdateNote(positionNeedUpdate,updateNote);

            } else if(action.equals(ACTION_BROADCAST_INBOX_INTENT_UPDATE_LIST_RECORD)){
                if(DEBUG) Log.d(TAG,"Moved record to Saved folder , position = "+positionNeedUpdate);
                mInboxAdapter.removeItemNoNeedDeleteDatabase(positionNeedUpdate);

            } else if(action.equals(ACTION_BROADCAST_INTENT_DELETE_ALL)){
                Utilities.executeAsyncTask(new LoadListRecord());
            }
        }

    };


}