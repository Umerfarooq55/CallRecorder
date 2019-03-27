package automatic.phonerecorder.callrecorder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import automatic.phonerecorder.callrecorder.R;
import automatic.phonerecorder.callrecorder.model.SearchItem;
import automatic.phonerecorder.callrecorder.utils.MyConstants;


import java.util.ArrayList;
import java.util.Calendar;

public class SearchAdapter extends ArrayAdapter<SearchItem> {
	private Context mcontext;
	private ArrayList<SearchItem> items;
    private ArrayList<SearchItem> itemsAll;
	private ArrayList<SearchItem> suggestions;
	private int viewResourceId;
	
	@SuppressWarnings("unchecked")
	public SearchAdapter(Context context, int viewResourceId,ArrayList<SearchItem> mListRecord) {
		super(context,viewResourceId,mListRecord);
		this.mcontext = context;
		this.items = mListRecord;
		this.itemsAll = (ArrayList<SearchItem>) items.clone();
		this.suggestions = new ArrayList<SearchItem>();
		this.viewResourceId = viewResourceId;
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ItemSearchHolder itemHolder;
		SearchItem record = items.get(position);
		if(convertView == null){
			LayoutInflater vi = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(viewResourceId, null);
			itemHolder = new ItemSearchHolder();
			itemHolder.nameContact = (TextView) convertView.findViewById(R.id.item_search_txt_namecontact);
			itemHolder.phonenumber = (TextView) convertView.findViewById(R.id.item_search_txt_phonenumber);
			itemHolder.time = (TextView) convertView.findViewById(R.id.item_search_record_txt_time);
			itemHolder.status = (ImageView) convertView.findViewById(R.id.item_search_record_img_status);
			itemHolder.note = (TextView) convertView.findViewById(R.id.item_search_txt_note);
			
			convertView.setTag(itemHolder);
			
		}else {
			itemHolder = (ItemSearchHolder) convertView.getTag();
		}
		
		itemHolder.position = position;
		String myPhone = record.getPhoneNumber();
		itemHolder.phonenumber.setText(myPhone);
		itemHolder.nameContact.setText(record.getNameContact());
		itemHolder.time.setText(getTimeFromMilliseconds(record.getDate()));
		int statusCall = record.getStatus();
		String note = record.getNote();
		if(statusCall == MyConstants.INCOMING_CALL_STARTED){
			itemHolder.status.setImageResource(R.drawable.ic_incoming);
		}else if (statusCall == MyConstants.OUTGOING_CALL_STARTED) {
			itemHolder.status.setImageResource(R.drawable.ic_outgoing);
		}
		if(note != null && !note.isEmpty()){
			itemHolder.note.setText(note);
		}else {
			itemHolder.note.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	static class ItemSearchHolder {
		int position;
		TextView nameContact;
		TextView phonenumber;
		TextView time;
		TextView note;
		ImageView status;
	}
	@Override
    public Filter getFilter() {
        return nameFilter;
    }
	
	Filter nameFilter = new Filter() {
		@Override
        public String convertResultToString(Object resultValue) {
            String str = ((SearchItem)(resultValue)).getPhoneNumber();
            return str;
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(constraint != null) {
            	suggestions.clear();
            	for (SearchItem record : itemsAll) {
                    if(record.getPhoneNumber().toLowerCase().startsWith(constraint.toString().toLowerCase())
                    		|| record.getNameContact().toLowerCase().startsWith(constraint.toString().toLowerCase())
                    		||(record.getNote() != null && record.getNote().toLowerCase().startsWith(constraint.toString().toLowerCase()))){
                        suggestions.add(record);
                    }
                }
            	FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
            	
                return new FilterResults();
            }
        }
        @SuppressWarnings("unchecked")
		@Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
        	clear();
            if(results != null && results.count > 0) {
            	 // we have filtered results
                addAll((ArrayList<SearchItem>) results.values);
                
            }else {
            	 // no filter, add entire original list back in
                addAll(itemsAll);
				
			}
            notifyDataSetInvalidated();
        }
	};
	

	private String getTimeFromMilliseconds (long milliseconds){
		Calendar cl = Calendar.getInstance();
		cl.setTimeInMillis(milliseconds);  //here your time in miliseconds
		String time = ""+cl.get(Calendar.HOUR_OF_DAY) + ":" + cl.get(Calendar.MINUTE);
		return time;
	}
}
