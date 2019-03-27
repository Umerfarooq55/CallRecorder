package automatic.phonerecorder.callrecorder.model;

public class SearchItem {
	private int id;
	private String phoneNumber;
	private String nameContact;
	private long date;
	private int duration;
	private String path;
	private int status;
	private int Sync;
	private String note;
	private int id_original;
	
	public SearchItem() {
		super();
	}

	public SearchItem(int id, String phoneNumber, String nameContact,
			long date, int duration, String path, int status, int sync,
			String note) {
		super();
		this.id = id;
		this.phoneNumber = phoneNumber;
		this.nameContact = nameContact;
		this.date = date;
		this.duration = duration;
		this.path = path;
		this.status = status;
		Sync = sync;
		this.note = note;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId_original() {
		return id_original;
	}

	public void setId_original(int id_original) {
		this.id_original = id_original;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getNameContact() {
		return nameContact;
	}

	public void setNameContact(String nameContact) {
		this.nameContact = nameContact;
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
		return Sync;
	}

	public void setSync(int sync) {
		Sync = sync;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	
}
