package automatic.phonerecorder.callrecorder.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;

import automatic.phonerecorder.callrecorder.R;
import automatic.phonerecorder.callrecorder.controller.MyFileManager;
import automatic.phonerecorder.callrecorder.model.ContactCache;
import automatic.phonerecorder.callrecorder.model.RecordModel;
import automatic.phonerecorder.callrecorder.utils.MyConstants;
import automatic.phonerecorder.callrecorder.utils.MyDateUtils;
import automatic.phonerecorder.callrecorder.utils.Utilities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;


/**
 * Created by Anh Son on 6/11/2016.
 */
public class InboxAdapter extends SelectableAdapter<InboxAdapter.ViewHolder> {
    public static final String TAG = "InboxAdapter";
    public static final int TYPE_ITEM = 0;
    public static final int TYPE_HEADER_GROUP = 1;

    private Context mcontext;
    private List<RecordModel> mListRecord;
    private ArrayList<ContactCache> mCache;

    private SparseBooleanArray selectedItems;
    private ViewHolder.ClickListener clickListener;

    private DatabaseAdapter mDatabase;
    /**
     * 0: InboxAdapter , 1: SavedAdapter
     */
    private int mTypeAdapter = 0;

    // Pass in the contact array into the constructor
    public InboxAdapter(Context context,List<RecordModel> records,
                        ViewHolder.ClickListener clickListener) {
        super();
        mcontext = context;
        mListRecord = makeDataHeader(records);
        selectedItems = new SparseBooleanArray();
        this.clickListener = clickListener;
        mDatabase = DatabaseAdapter.getInstance(context);
    }

    public void setmTypeAdapter(int mTypeAdapter) {
        this.mTypeAdapter = mTypeAdapter;
    }

    public int getmTypeAdapter() {
        return mTypeAdapter;
    }

    public ArrayList<RecordModel> makeDataHeader(List<RecordModel> data) {
        int size = data.size();
        ArrayList<RecordModel> mListWithHeader = new ArrayList<RecordModel>();
        if (data.size() == 0)
            return mListWithHeader;
        String thisdate = getDateFromMilliseconds(data.get(0).getDate());
//        mSectionHeader.add(0);
        try {
            RecordModel firstsection  = (RecordModel) data.get(0).clone();
            firstsection.setSection(true);
            mListWithHeader.add(firstsection);
        } catch (CloneNotSupportedException e) {
           // e.printStackTrace();
        }

        mListWithHeader.add(data.get(0));
        for (int i = 1; i < size; i++) {
            String nextdate = getDateFromMilliseconds(data.get(i).getDate());
            if (nextdate.matches(thisdate)) {
                mListWithHeader.add(data.get(i));
            } else {
                try {
                    RecordModel section = (RecordModel) data.get(i).clone();
                    section.setSection(true);
                    mListWithHeader.add(section);
                }catch (CloneNotSupportedException en) {
                    //en.printStackTrace();
                }
                mListWithHeader.add(data.get(i));
//                mSectionHeader.add(mSectionHeader.size() + i);
                thisdate = nextdate;
            }
        }
        int finalSize = mListWithHeader.size();
        mCache = new ArrayList<>(finalSize);
        for(int i = 0;i< finalSize;i++){
            mCache.add(new ContactCache());
        }
        if(MyConstants.DEBUG) Log.d(TAG,"makeDataHeader");
        return mListWithHeader;
    }

    private String getDateFromMilliseconds(long milliseconds) {
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(milliseconds); // here your time in miliseconds
        String date = "" + cl.get(Calendar.YEAR) + cl.get(Calendar.MONTH)
                + cl.get(Calendar.DAY_OF_MONTH);
        return date;
    }

/*    public int getPosition(int position) {
        return position - mSectionHeader.headSet(position).size();
    }*/
    @Override
    public int getItemViewType(int position) {
        final RecordModel item = mListRecord.get(position);
        return item.isSection() ? TYPE_HEADER_GROUP : TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mListRecord.size();
    }


    @Override
    public InboxAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mcontext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mcontext);
        ViewHolder viewHolder = null;
        if(MyConstants.DEBUG) Log.d(TAG,"onCreateViewHolder");
        switch (viewType){
            case TYPE_ITEM:
                View itemRecord = inflater.inflate(R.layout.item_call_record,parent,false);
                viewHolder = new ViewHolder(mcontext,itemRecord,TYPE_ITEM,clickListener);
                if(MyConstants.DEBUG) Log.d(TAG,"onCreateViewHolder:inflate:TYPE_ITEM");
                break;
            case TYPE_HEADER_GROUP:
                View headerRecord = inflater.inflate(R.layout.header_group_listview,parent,false);
                viewHolder = new ViewHolder(mcontext,headerRecord,TYPE_HEADER_GROUP,clickListener);
                if(MyConstants.DEBUG) Log.d(TAG,"onCreateViewHolder:inflate:TYPE_HEADER_GROUP");
                break;
        }
        // Return a new holder instance
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(InboxAdapter.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        int statusCall = 0;
        RecordModel record = mListRecord.get(position);
        holder.position = position;
        ContactCache cache = mCache.get(position);

        if(MyConstants.DEBUG) Log.d(TAG,"onBindViewHolder");
        switch (type){
            case TYPE_HEADER_GROUP:
                holder.time.setText(MyDateUtils.formatDateFromMilliseconds(record.getDate()));
                if(MyConstants.DEBUG) Log.d(TAG,"onBindViewHolder:TYPE_HEADER_GROUP");
                break;
            case TYPE_ITEM:
                holder.time.setText(MyDateUtils.getTimeFromMilliseconds(mcontext, record.getDate()));
                holder.duration.setText(MyDateUtils.formatDuration(mcontext,record.getDuration()));
                String note = record.getNote();
                if(null!=note && !"".equals(note.trim())){
                    holder.note.setText(record.getNote());
                    holder.fullnote.setVisibility(View.VISIBLE);
                }else {
                    holder.note.setText("");
                    holder.fullnote.setVisibility(View.GONE);
                }
                statusCall = record.getStatus();
                if(statusCall == MyConstants.INCOMING_CALL_STARTED){
                    holder.status.setImageResource(R.drawable.ic_incoming);
                }else if (statusCall == MyConstants.OUTGOING_CALL_STARTED) {
                    holder.status.setImageResource(R.drawable.ic_outgoing);
                }
                String myPhone = record.getPhoneNumber();
                if(MyConstants.DEBUG) Log.d(TAG,"onBindViewHolder:TYPE_ITEM");
                if(!cache.isCache()){
                    //Set cache
                    ContactCache contactCache = new ContactCache();
                    try {
                        Utilities.executeAsyncTask(new NameContactTask(mcontext, position, holder, myPhone,contactCache));
                    } catch (RejectedExecutionException e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                    if(MyConstants.DEBUG) Log.d(TAG,"onBindViewHolder:TYPE_ITEM:set Cached");
                } else {
                    if(cache.isHasPhoto()){
                        try{
                            if(MyConstants.DEBUG) Log.d(TAG,"photoContactUri != null 206");
                            holder.avatar.setImageContact(Long.parseLong(cache.getContactId()));
                        }catch (NumberFormatException e){

                        }
                    }else {
                        holder.avatar.setImageResource(R.drawable.ic_person_img);
                        if(MyConstants.DEBUG) Log.d(TAG,"photoContactUri == null 213");
                    }
                    if(cache.getContactName().equals(mcontext.getString(R.string.UnablegetName))){
                        Log.d("SetSpan","Done");
                        Spannable wordtoSpan = new SpannableString(cache.getContactName());
                        wordtoSpan.setSpan(new ForegroundColorSpan(Color.GREEN), 9, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        holder.nameContact.setText(wordtoSpan);
                    }else{
                        holder.nameContact.setText(cache.getContactName());
                    }
//                    holder.nameContact.setText(cache.getContactName());
                    if(MyConstants.DEBUG) Log.d(TAG,"onBindViewHolder:TYPE_ITEM:get Cached");
                }
                // Highlight the item if it's selected
                holder.selectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
                break;
        }



    }

    public class NameContactTask extends AsyncTask<Void, Void, String[]> {
        private int mPosition;
        private ViewHolder mHolder;
        private String mPhoneNumber;
        private Context context;
        private ContactCache cache;

        public NameContactTask(Context context, int position, ViewHolder holder, String phoneNumber, ContactCache contact) {
            mPosition = position;
            mHolder = holder;
            this.mPhoneNumber = phoneNumber;
            this.context = context;
            this.cache = contact;
        }
        @Override
        protected String[]  doInBackground(Void... params) {
            // TODO Auto-generated method stub
            String[] result = new String[2];
            String nameContact = MyFileManager.getContactName(context,mPhoneNumber);
            String contactId = MyFileManager.getContactIdFromPhoneNumber(context, mPhoneNumber);
            boolean isHasphoto = MyFileManager.hasContactPhoto(mcontext,mPhoneNumber);
            cache.setHasPhoto(isHasphoto);
            result[0] = nameContact;
            result[1]= contactId;
            return result;
        }
        @Override
        protected void onPostExecute(final String [] result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (mHolder.position == mPosition) {
                if (result != null) {
                    if(cache.isHasPhoto()){
                        try{
                            mHolder.avatar.setImageContact(Long.parseLong(result[1]));
                        }catch (NumberFormatException e){
                        }
                    }else {
                        mHolder.avatar.setImageResource(R.drawable.ic_person_img);
                    }
                    Log.d("SetSpan",result[0]);
                    if(result[0].equals(mcontext.getString(R.string.UnablegetName))){
                        Log.d("SetSpan","Done");
                        mHolder.nameContact.setClickable(true);
                        Spannable wordtoSpan = new SpannableString(result[0]);
                        wordtoSpan.setSpan(new ForegroundColorSpan(Color.GREEN), 10, 19, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mHolder.nameContact.setText(wordtoSpan);
                    }else{
                        mHolder.nameContact.setClickable(false);
                        mHolder.nameContact.setText(result[0]);
                    }
                    mHolder.nameContact.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(result[0].equals(mcontext.getString(R.string.UnablegetName))) {
                                showDialogInformLimitInbox();
                            }
                        }
                    });
                    // set cache
                    cache.setContactId(result[1]);
                    cache.setContactName(result[0]);
                    cache.setCache(true);
                    mCache.set(mPosition,cache);
                }
            }
        }
    }
    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        }
        else {
            selectedItems.put(pos, true);
        }
        if(MyConstants.DEBUG) Log.d(TAG,"selected = "+pos);
        notifyItemChanged(pos);
    }

    /**
     * Check group record as date is empty or not
     * @param position
     * @return : position of header
     */
    private int checkEmtryGroup(int position){
        int result = -1;
        int size = mListRecord.size();
        Log.d(TAG, "checkEmtryGroup ,mListRecord.size()= "+size);
        if(position >= size ) return -1;
        String thisdate = getDateFromMilliseconds(mListRecord.get(position).getDate());
        if(position == (size - 1)){
            if(mListRecord.get(position - 1).isSection()){
                result = position - 1;
            }
            return result;
        } else if(position == 1){
            String nextdate = getDateFromMilliseconds(mListRecord.get(position+1).getDate());
            if(!thisdate.matches(nextdate)){
                result = 0;
            }
            return result;
        } else {
            String nextdate = getDateFromMilliseconds(mListRecord.get(position+1).getDate());
            if(thisdate.matches(nextdate)){
                return -1;
            } else if(!mListRecord.get(position - 1).isSection()) {
                return -1;
            } else {
                return (position - 1);
            }
        }
    }
    public RecordModel getRecorderModel (int position){
        return mListRecord.get(position);
    }

    /**
     * Removes the item that currently is at the passed in position from the
     * underlying data set.
     *
     * @param position The index of the item to remove.
     */
    public void removeItem(int position, boolean isNeedDeleteFile) {
        RecordModel record = mListRecord.get(position);
        if(getmTypeAdapter() == MyConstants.TYPE_ADAPTER_INBOX_FRAGMENT){
            mDatabase.deleteRecord(record,DatabaseAdapter.TABLE_INBOX);
            if(isNeedDeleteFile) MyFileManager.deleteFile(record.getPath());
        } else {
            mDatabase.deleteRecord(record,DatabaseAdapter.TABLE_SAVE_RECORD);
            if(isNeedDeleteFile) MyFileManager.deleteFile(record.getPath());
        }
        int positionNeedRemove = checkEmtryGroup(position);
        mListRecord.remove(position);
        notifyItemRemoved(position);
        mCache.remove(position);

        if(positionNeedRemove != -1){
            mListRecord.remove(positionNeedRemove);
            notifyItemRemoved(positionNeedRemove);
        }
    }

    /**
     * move one record from inbox to saved
     * @param position
     */
    public void removeItemNoNeedDeleteDatabase(int position){
        if(getmTypeAdapter() == MyConstants.TYPE_ADAPTER_INBOX_FRAGMENT){

            int positionNeedRemove = checkEmtryGroup(position);
            mListRecord.remove(position);
            notifyItemRemoved(position);
            mCache.remove(position);
            if(positionNeedRemove != -1){
                mListRecord.remove(positionNeedRemove);
                notifyItemRemoved(positionNeedRemove);
            }
        }

    }

    public void notifyUpdateNote(int pos,String updateNote){
        mListRecord.get(pos).setNote(updateNote);
        notifyItemChanged(pos);
    }
    private void removeRange(int positionStart, int itemCount, boolean isNeedDeleteFile) {
        for (int i = 0; i < itemCount; ++i) {
            //Delete in database
            RecordModel record = mListRecord.get(positionStart);
            if(getmTypeAdapter() == MyConstants.TYPE_ADAPTER_INBOX_FRAGMENT){
                mDatabase.deleteRecord(record,DatabaseAdapter.TABLE_INBOX);
                if(isNeedDeleteFile) MyFileManager.deleteFile(record.getPath());
            } else {
                mDatabase.deleteRecord(record,DatabaseAdapter.TABLE_SAVE_RECORD);
                if(isNeedDeleteFile) MyFileManager.deleteFile(record.getPath());
            }
            mListRecord.remove(positionStart);
            mCache.remove(positionStart);

        }
        notifyItemRangeRemoved(positionStart, itemCount);

    }
    public void removeItems(List<Integer> positions, boolean isNeedDeleteFile) {
        // Reverse-sort the list
        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs - lhs;
            }
        });

        // Split the list in ranges
        while (!positions.isEmpty()) {
            if (positions.size() == 1) {
                removeItem(positions.get(0),isNeedDeleteFile);
                positions.remove(0);
            } else {
                int count = 1;
                while (positions.size() > count && positions.get(count).equals(positions.get(count - 1) - 1)) {
                    ++count;
                }

                if (count == 1) {
                    removeItem(positions.get(0),isNeedDeleteFile);
                } else {
                    removeRange(positions.get(count - 1), count,isNeedDeleteFile);
                }

                for (int i = 0; i < count; ++i) {
                    positions.remove(0);
                }
            }
        }
    }


    public void clearSelections() {
        List<Integer> selection = getSelectedItems();
        selectedItems.clear();
        for(Integer i : selection){
            notifyItemChanged(i);
        }
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {

        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public int position;
        public SmartImageView avatar;
        public ImageView status;
        public TextView nameContact;
        public TextView time;
        public TextView note;
        public TextView duration;
        public ImageView fullnote;
        public Context context;

        public View selectedOverlay;

        private ClickListener listener;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview

        public ViewHolder(Context context,View itemView, int rowType,ClickListener listener) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            this.context = context;
//            itemView.setSelected(true);
            this.listener = listener;
            switch (rowType){
                case TYPE_ITEM:
                    avatar = (SmartImageView) itemView.findViewById(R.id.item_call_record_img_avatar);
                    status = (ImageView) itemView.findViewById(R.id.item_call_record_img_status);
                    nameContact = (TextView) itemView.findViewById(R.id.item_call_record_txt_namecontact);
                    time = (TextView) itemView.findViewById(R.id.item_call_record_txt_time);
                    note = (TextView) itemView.findViewById(R.id.item_call_record_txt_note);
                    duration = (TextView) itemView.findViewById(R.id.item_call_record_txt_duration);
                    fullnote = (ImageView) itemView.findViewById(R.id.item_call_record_imb_full_note);
                    selectedOverlay = itemView.findViewById(R.id.selected_overlay);
                    itemView.setOnClickListener(this);
                    itemView.setOnLongClickListener(this);
                    break;
                case TYPE_HEADER_GROUP:
                    time = (TextView) itemView.findViewById(R.id.textSeparator);
                    break;

            }
        }
        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemClicked(getPosition());
            }

        }

        @Override
        public boolean onLongClick(View v) {
            if (listener != null) {
                return listener.onItemLongClicked(getPosition());
            }
            return false;
        }
        public interface ClickListener {
            void onItemClicked(int position);
            boolean onItemLongClicked(int position);
        }

    }
    private void showDialogInformLimitInbox(){
        View checkBoxView = View.inflate(mcontext, R.layout.google_policy, null);
         TextView link = checkBoxView.findViewById(R.id.link);

         link.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 String url = "https://support.google.com/googleplay/android-developer/answer/9047303";
                 Intent i = new Intent(Intent.ACTION_VIEW);
                 i.setData(Uri.parse(url));
                 mcontext.startActivity(i);
             }
         });
//
//        Due to crash in Nauget
//        TextView message = (TextView) checkBoxView.findViewById(R.id.content);
//       message.setMovementMethod(new ScrollingMovementMethod());
        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
        builder.setTitle("Use of Call Log permission groups");
        builder.setView(checkBoxView)
                .setPositiveButton(R.string.string_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                    }
                }).show();
    }
}
