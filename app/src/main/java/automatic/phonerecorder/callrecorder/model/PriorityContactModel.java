package automatic.phonerecorder.callrecorder.model;

import android.net.Uri;

public class PriorityContactModel {
	private int id;
	private Uri UriContact;
	public boolean flagFooter = false;
	
	public PriorityContactModel() {
		super();
	}

	public PriorityContactModel(int id, Uri uriContact) {
		super();
		this.id = id;
		UriContact = uriContact;
	}

	public PriorityContactModel(boolean flagFooter) {
		super();
		this.flagFooter = flagFooter;
	}

	public long getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Uri getUriContact() {
		return UriContact;
	}

	public void setUriContact(Uri uriContact) {
		UriContact = uriContact;
	}

	public boolean isFlagFooter() {
		return flagFooter;
	}

	public void setFlagFooter(boolean flagFooter) {
		this.flagFooter = flagFooter;
	}
	
	
	
	
	

}
