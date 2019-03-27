package automatic.phonerecorder.callrecorder.model;

/**
 * Created by Anh Son on 6/13/2016.
 */
public class ContactCache {
    private String contactId;
    private boolean isHasPhoto;
    private String contactName;
    private boolean isCache;

    public ContactCache() {
    }

    public ContactCache(String contactId, boolean isHasPhoto, String contactName, boolean isCache) {
        this.contactId = contactId;
        this.isHasPhoto = isHasPhoto;
        this.contactName = contactName;
        this.isCache = isCache;
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

    public boolean isHasPhoto() {
        return isHasPhoto;
    }

    public void setHasPhoto(boolean hasPhoto) {
        isHasPhoto = hasPhoto;
    }
}
