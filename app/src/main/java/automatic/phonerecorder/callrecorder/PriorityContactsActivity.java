package automatic.phonerecorder.callrecorder;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;


import automatic.phonerecorder.callrecorder.adapter.DatabaseAdapter;
import automatic.phonerecorder.callrecorder.model.PriorityContactModel;
import automatic.phonerecorder.callrecorder.utils.Utilities;

import automatic.phonerecorder.callrecorder.adapter.PriorityContactAdapter;
import automatic.phonerecorder.callrecorder.utils.MyConstants;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import java.util.ArrayList;

import static android.widget.AdapterView.*;

/**
 * Created by Anh Son on 6/10/2016.
 */
public class PriorityContactsActivity extends AppCompatActivity
        implements PriorityContactAdapter.PriorityContactHolder.ClickListener,
        OnItemClickListener {
    private Context mContext;

    private RecyclerView mrContactHistoryRv;
    private PriorityContactAdapter mPriorityContactAdapter;
    private ArrayList<PriorityContactModel> mListContacts ;
    private DatabaseAdapter mDatabase;

    private final int CONTACT_PICKER_RESULT = 43;
    private boolean isPickContactResult;

    private final int MENU_DELETE= 0;
    private final int MENU_VIEW = 1;

    private int mPosition = -1;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_priority_contact);
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.priority_contact_title));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mDatabase = DatabaseAdapter.getInstance(mContext);

        mrContactHistoryRv = (RecyclerView) findViewById(R.id.priority_contact_rv);
        mrContactHistoryRv.setLayoutManager(new LinearLayoutManager(this));
        mrContactHistoryRv.setHasFixedSize(true);
        Utilities.executeAsyncTask(new LoadListPriorityContact());
        this.registerForContextMenu(mrContactHistoryRv);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_priority_contact, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
                isPickContactResult = true;
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onItemClicked(View v, int position) {
        mPosition = position;
        v.showContextMenu();
    }

    @Override
    public boolean onItemLongClicked(int position) {
        mPosition = position;
        return false;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CONTACT_PICKER_RESULT:
                    // handle contact results
                    Uri contactURI = data.getData();
                    long index = mDatabase.addPriorityContact(mContext, contactURI);
                    Log.d(MyConstants.TAG, "CONTACT_PICKER_RESULT, index= "+index);
                    if(index > 0) mPriorityContactAdapter.addItem(contactURI);

                    break;
            }

        } else {
            // gracefully handle failure
            if(MyConstants.DEBUG) Log.w(MyConstants.TAG, "PriorityContactsActivity Warning: activity result not ok");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        view.showContextMenu();
    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        // TODO Auto-generated method stub
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(getString(R.string.header_context_menu));
        String[] menuItems = getResources().getStringArray(R.array.context_menu_item_priority_contact);
        for (int i = 0; i<menuItems.length; i++) {
            menu.add(Menu.NONE, i, i, menuItems[i]);
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        int menuItemIndex = item.getItemId();
        PriorityContactModel contact = mListContacts.get(mPosition);
        switch (menuItemIndex) {
            case MENU_DELETE:
                // Remove selected items following the ids
                mPriorityContactAdapter.removeItem(mPosition);
                break;
            case MENU_VIEW:
                Intent contactIntent = new Intent(Intent.ACTION_VIEW);
                contactIntent.setData(contact.getUriContact());
                startActivity(contactIntent);
                isPickContactResult = true;
                break;
        }
        return super.onContextItemSelected(item);
    }

    public class LoadListPriorityContact extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            // TODO Auto-generated method stub
            mListContacts = mDatabase.getAllPriorityContact();
            mPriorityContactAdapter = new PriorityContactAdapter(mContext, mListContacts,PriorityContactsActivity.this);
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            setProgressBarIndeterminateVisibility(false);
            mrContactHistoryRv.setAdapter(mPriorityContactAdapter);
        }
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            setProgressBarIndeterminateVisibility(true);
        }

    }
}
