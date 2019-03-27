package automatic.phonerecorder.callrecorder.model;

public class CallRecordModel implements Comparable<CallRecordModel> {
	private String callName;
	private String nameFromContact;
	private String path;

	public CallRecordModel() {
		super();
	}

	public CallRecordModel(String callName) {
		super();
		this.callName = callName;
	}

	public CallRecordModel(String callName, String nameFromContact) {
		super();
		this.callName = callName;
		this.nameFromContact = nameFromContact;
	}

	public CallRecordModel(String callName, String nameFromContact, String path) {
		super();
		this.callName = callName;
		this.nameFromContact = nameFromContact;
		this.path = path;
	}

	public String getCallName() {
		return callName;
	}

	public void setCallName(String callName) {
		this.callName = callName;
	}

	public String getNameFromContact() {
		return nameFromContact;
	}

	public void setNameFromContact(String nameFromContact) {
		this.nameFromContact = nameFromContact;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int compareTo(CallRecordModel another) {
		Long date1 = Long.valueOf(this.getCallName().substring(1, 15));
		Long date2 = Long.valueOf(another.getCallName().substring(1, 15));
		return (date2 > date1 ? -1 : (date2 == date1 ? 0 : 1));
	}

}
