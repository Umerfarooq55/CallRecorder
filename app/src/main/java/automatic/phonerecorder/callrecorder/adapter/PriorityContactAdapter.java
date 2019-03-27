package automatic.phonerecorder.callrecorder.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import automatic.phonerecorder.callrecorder.R;
import automatic.phonerecorder.callrecorder.controller.MyFileManager;
import automatic.phonerecorder.callrecorder.model.PriorityContactCache;
import automatic.phonerecorder.callrecorder.model.PriorityContactModel;
import automatic.phonerecorder.callrecorder.utils.MyConstants;
import automatic.phonerecorder.callrecorder.utils.Utilities;
import com.loopj.android.image.SmartImageView;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anh Son on 6/30/2016.
 */
public class PriorityContactAdapter extends SelectableAdapter<PriorityContactAdapter.PriorityContactHolder> {
    private Context mcontext;
    private List<PriorityContactModel> mListContacts;
    private ArrayList<PriorityContactCache> mCache;

    private PriorityContactHolder.ClickListener clickListener;

    private DatabaseAdapter mDatabase;

    public PriorityContactAdapter (Context context,List<PriorityContactModel> listContact,
                                   PriorityContactHolder.ClickListener clickListener) {
        super();
        mcontext = context;
        this.clickListener = clickListener;
        mListContacts = listContact;

        int finalSize = mListContacts.size();
        mCache = new ArrayList<>(finalSize);
        for(int i = 0;i< finalSize;i++){
            mCache.add(new PriorityContactCache());
        }
    }
    @Override
    public PriorityContactAdapter.PriorityContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mcontext);
        View itemRecord = inflater.inflate(R.layout.item_priority_contact,parent,false);
        return new PriorityContactHolder(itemRecord,clickListener);
    }

    @Override
    public void onBindViewHolder(PriorityContactAdapter.PriorityContactHolder holder, int position) {
        if(holder!=null){
            PriorityContactCache cache = mCache.get(position);
            holder.position = position;
            PriorityContactModel contact = mListContacts.get(position);
            if(!cache.isCache()){
                //Set cache
                PriorityContactCache contactCache = new PriorityContactCache();
                Utilities.executeAsyncTask(new NameContactTask(mcontext,position,holder,contact.getUriContact(),contactCache));
            } else {

                if(cache.isHasPhoto()){
                    try{
                        holder.avatar.setImageContact(Long.parseLong(cache.getContactId()));
                    }catch (NumberFormatException e){
                    }
                }else {
                     holder.avatar.setImageResource(R.drawable.ic_person_img);
                }
                holder.displayName.setText(cache.getContactName());
                holder.phoneNumber.setText(cache.getPhoneNumber());
            }


        }

    }

    public class NameContactTask extends AsyncTask<Void, Void, String[]> {

        int position;
        Context context;
        Uri contactUri;
        PriorityContactHolder holder;
        PriorityContactCache cache;

        public NameContactTask(Context context,int position,PriorityContactHolder holder,
                               Uri contactUri, PriorityContactCache cache) {
            this.position = position;
            this.holder = holder;
            this.contactUri = contactUri;
            this.context = context;
            this.cache = cache;

        }
        @Override
        protected String[] doInBackground(Void... voids) {
            String[] result = new String[3];
            result[0] = MyFileManager.getContactName(mcontext,contactUri);
            result[1] = Utilities.convertArrayListToString
                    (MyFileManager.getPhoneNumber(mcontext, contactUri.getLastPathSegment()));
            String contactId = MyFileManager.getContactIdFromUri(mcontext,contactUri);
            boolean isHasphoto = MyFileManager.hasContactPhoto(mcontext,result[1]);
            cache.setHasPhoto(isHasphoto);
            result[2] = contactId;
            return result;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            if (holder.position == position) {
                holder.displayName.setText(strings[0]);
                holder.phoneNumber.setText(strings[1]);
                if(cache.isHasPhoto()){
                    try{
                        holder.avatar.setImageContact(Long.parseLong(strings[2]));
                    }catch (NumberFormatException e){
                    }
                }else {
                    holder.avatar.setImageResource(R.drawable.ic_person_img);
                }

                cache.setContactName(strings[0]);
                cache.setPhoneNumber(strings[1]);
                cache.setContactId(strings[2]);
                cache.setCache(true);
                mCache.set(position,cache);
            }


        }
    }
    @Override
    public int getItemCount() {
        return mListContacts.size();
    }

    public PriorityContactModel getPriorityContact (int position){
        return mListContacts.get(position);
    }

    public void addItem(Uri contactUri){
        PriorityContactModel contact = new PriorityContactModel();
        contact.setUriContact(contactUri);
        mListContacts.add(contact);
        mCache.add(new PriorityContactCache());
        notifyItemInserted(mListContacts.size()-1);
        if(MyConstants.DEBUG) Log.d(MyConstants.TAG, "addItem " + contactUri.toString());
    }

    public void removeItem(int position){
        DatabaseAdapter database = DatabaseAdapter.getInstance(mcontext);
        database.deletePriorityContact(mListContacts.get(position));
        mListContacts.remove(position);
        mCache.remove(position);
        notifyItemRemoved(position);
    }
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class PriorityContactHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {
        public int position;
        public SmartImageView avatar;
        public TextView displayName;
        public TextView phoneNumber;

        private ClickListener listener;

        public PriorityContactHolder(View itemView,ClickListener listener) {
            super(itemView);
            this.listener = listener;
            avatar = (SmartImageView) itemView.findViewById(R.id.priority_contact_img_thumbnail);
            displayName = (TextView) itemView.findViewById(R.id.priority_contact_txt_displayname);
            phoneNumber = (TextView) itemView.findViewById(R.id.priority_contact_txt_phonenumber);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemClicked(v,getPosition());
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
            void onItemClicked(View view,int position);
            boolean onItemLongClicked(int position);
        }
    }

    }
