package automatic.phonerecorder.callrecorder.model;

/**
 * Created by Anh Son on 6/13/2016.
 */
public class PriorityContactCache {
    private String contactId;
    private boolean isHasPhoto;
    private String contactName;
    private String phoneNumber;
    private boolean isCache;

    public PriorityContactCache() {
    }

    public PriorityContactCache(String contactId, boolean isHasPhoto, String contactName, boolean isCache) {
        this.contactId = contactId;
        this.isHasPhoto = isHasPhoto;
        this.contactName = contactName;
        this.isCache = isCache;
    }

    public boolean isHasPhoto() {
        return isHasPhoto;
    }

    public void setHasPhoto(boolean hasPhoto) {
        isHasPhoto = hasPhoto;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }


    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public boolean isCache() {
        return isCache;
    }

    public void setCache(boolean cache) {
        isCache = cache;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
