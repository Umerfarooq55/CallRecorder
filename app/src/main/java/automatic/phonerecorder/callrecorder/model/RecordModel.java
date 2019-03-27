package automatic.phonerecorder.callrecorder.model;

import android.os.Parcel;
import android.os.Parcelable;

public class RecordModel implements Parcelable,Cloneable{
	
	private int id;
	private String phoneNumber;
	private long date;
	private int duration;
	private String path;
	private int status;
	private int sync;
	private String note;
	private boolean isSection;
	
	public RecordModel() {
		super();
	}

	public RecordModel(int id, String phoneNumber, long date, int duration,
			String path, int status, int sync, String note) {
		super();
		this.id = id;
		this.phoneNumber = phoneNumber;
		this.date = date;
		this.duration = duration;
		this.path = path;
		this.status = status;
		this.sync = sync;
		this.note = note;
	}
	public RecordModel ( RecordModel record ,int newId){
		super();
		this.id = newId;
		this.phoneNumber = record.getPhoneNumber();
		this.date = record.getDate();
		this.duration = record.getDuration();
		this.path = record.getPath();
		this.status = record.getStatus();
		this.sync = record.getSync();
		this.note = record.getNote();
	}
	
	public RecordModel(SearchItem searchItem){
		super();
		this.id = searchItem.getId();
		this.phoneNumber = searchItem.getPhoneNumber();
		this.date = searchItem.getDate();
		this.duration = searchItem.getDuration();
		this.path = searchItem.getPath();
		this.status = searchItem.getStatus();
		sync = searchItem.getSync();
		this.note = searchItem.getNote();
	}
	
	
	public RecordModel(String phoneNumber, long date, int duration,
			String path, int status, int sync, String note) {
		super();
		this.phoneNumber = phoneNumber;
		this.date = date;
		this.duration = duration;
		this.path = path;
		this.status = status;
		this.sync = sync;
		this.note = note;
	}
	
	

	public RecordModel(String phoneNumber, long date, int duration,
			String path, int status, int sync) {
		super();
		this.phoneNumber = phoneNumber;
		this.date = date;
		this.duration = duration;
		this.path = path;
		this.status = status;
		this.sync = sync;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getSync() {
		return sync;
	}

	public void setSync(int sync) {
		this.sync = sync;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public boolean isSection() {
		return isSection;
	}

	public void setSection(boolean section) {
		isSection = section;
	}

	private RecordModel (Parcel in){
		id = in.readInt();
		phoneNumber = in.readString();
		date = in.readLong();
		duration = in.readInt();
		path = in.readString();
		status = in.readInt();
		sync = in.readInt();
		note = in.readString();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel out, int flags) {
		// TODO Auto-generated method stub
		out.writeInt(id);
		out.writeString(phoneNumber);
		out.writeLong(date);
		out.writeInt(duration);
		out.writeString(path);
		out.writeInt(status);
		out.writeInt(sync);
		out.writeString(note);
	}
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public static final Creator<RecordModel> CREATOR = new Creator<RecordModel>() {

		@Override
		public RecordModel createFromParcel(Parcel in) {
			// TODO Auto-generated method stub
			return new RecordModel(in);
		}

		@Override
		public RecordModel[] newArray(int size) {
			// TODO Auto-generated method stub
			return new RecordModel[size];
		}
		 
	 };
	
	
	

}
