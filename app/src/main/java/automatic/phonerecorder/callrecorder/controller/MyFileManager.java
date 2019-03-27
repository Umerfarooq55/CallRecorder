package automatic.phonerecorder.callrecorder.controller;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.PhoneLookup;
import android.text.format.DateFormat;
import android.util.Log;
import automatic.phonerecorder.callrecorder.R;
import automatic.phonerecorder.callrecorder.utils.PreferUtils;
import automatic.phonerecorder.callrecorder.model.CallRecordModel;
import automatic.phonerecorder.callrecorder.model.MyPair;
import automatic.phonerecorder.callrecorder.utils.MyConstants;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MyFileManager implements MyConstants {
    /**
     * returns absolute file directory
     *
     * @return
     * @throws Exception
     */
    public static String getFilename(Context context, String phoneNumber,
                                     int status) throws Exception {
        String filepath = null;
        String myDate = null;
        File file = null;
        if (phoneNumber == null)
            throw new Exception("Phone number can't be empty");
        try {
            filepath = getFilePath();

            file = new File(filepath, PreferUtils.getNameFolderSaveData(context));

            if (!file.exists()) {
                file.mkdirs();
            }

            myDate = (String) DateFormat.format("yyyyMMddkkmmss", new Date());

            // Clean characters in file name
            phoneNumber = phoneNumber.replaceAll("[\\*\\+-]", "");
            if (phoneNumber.length() > 10) {
                phoneNumber.substring(phoneNumber.length() - 10,
                        phoneNumber.length());
            }
        } catch (Exception e) {
            if (DEBUG)
                Log.e(TAG, "Exception " + phoneNumber);
            e.printStackTrace();
        }

        // return ("d" + myDate + "s"
        // + status + "p" + phoneNumber + ".wav");
        return ("d" + myDate + "s" + status + "p" + phoneNumber);
    }

    public static String getFilePath() {
        // TODO: Change to user selected directory
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static void deleteFile(String pathFile) {
        if (pathFile == null)
            return;
        if (DEBUG)
            Log.d(TAG, "FileHelper deleteFile " + pathFile);
        try {
            File file = new File(pathFile);

            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            if (DEBUG)
                Log.e(TAG, "deleteFile Exception");
            e.printStackTrace();
        }
    }

    public static void deleteFile(File file) {
        if (file == null)
            return;
        if (DEBUG)
            Log.d(TAG, "FileHelper deleteFile " + file.getName());
        try {
            if (file.exists())
                file.delete();
        } catch (Exception e) {
            if (DEBUG)
                Log.e(TAG, "deleteFile Exception");
            e.printStackTrace();
        }

    }

    /**
     * get Name was save in Contact when knew phone nume of them.
     *
     * @param number
     * @param context
     * @return: Name
     */
    public static String getContactName(Context context, String number) {
        Log.d("NumberOutgoing",number);
        String name = number;
        if (!name.equals(context.getString(R.string.UnablegetName))) {
            // define the columns I want the query to return
            String[] projection = new String[]{
                    PhoneLookup.DISPLAY_NAME,
                    PhoneLookup._ID};

            // encode the phone number and build the filter URI
            Uri contactUri = Uri.withAppendedPath(
                    PhoneLookup.CONTENT_FILTER_URI,
                    Uri.encode(number));

            // query time
            Cursor cursor = context.getContentResolver().query(contactUri,
                    projection, null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    name = cursor
                            .getString(cursor
                                    .getColumnIndex(PhoneLookup.DISPLAY_NAME));
                } else {
                    // if (DEBUG) Log.i(TAG, "Contact Not Found @ " + number);
                }
                cursor.close();
            }

        }
        return name;
    }

    /**
     * Get Contact Display Name from URI
     *
     * @param context
     * @param contactUri
     * @return
     */
    public static String getContactName(Context context, Uri contactUri) {
        String contactName = null;
        // querying contact data store
        Cursor cursor = context.getContentResolver().query(contactUri, null,
                null, null, null);
        if (cursor.moveToFirst()) {
            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER = An indicator of whether this contact has at
            // least one phone number.
            contactName = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }
        cursor.close();
        return contactName;
    }

    public static String getContactNameFromId(Context context, String contactId) {
        long id = 0;
        try {
            id = Long.parseLong(contactId);
        } catch (NumberFormatException e) {

        }
        // Build the Uri to query to table
        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);

        String contactName = null;
        // querying contact data store
        Cursor cursor = context.getContentResolver().query(uri, null,
                null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                // DISPLAY_NAME = The display name for the contact.
                // HAS_PHONE_NUMBER = An indicator of whether this contact has at
                // least one phone number.
                contactName = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            }
            cursor.close();
        }

        return contactName;
    }

    /**
     * Get Photo of Contact saved from Phone number
     *
     * @param context
     * @param contactId
     * @return : URI contact photo. Return NULL if a contact do not have photo.
     */
    public static Uri getPhotoUri(Context context, String contactId) {
        ContentResolver contentResolver = context.getContentResolver();
        try {
            Cursor cursor = contentResolver
                    .query(ContactsContract.Data.CONTENT_URI,
                            null,
                            ContactsContract.Data.CONTACT_ID
                                    + "="
                                    + contactId
                                    + " AND "
                                    + ContactsContract.Data.MIMETYPE
                                    + "='"
                                    + CommonDataKinds.Photo.CONTENT_ITEM_TYPE
                                    + "'", null, null);

            if (cursor != null) {
                if (!cursor.moveToFirst()) {
                    return null; // no photo
                }
                cursor.close();
            } else {
                return null; // error in cursor process
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Uri person = null;
        try {
            person = ContentUris.withAppendedId(
                    ContactsContract.Contacts.CONTENT_URI,
                    Long.parseLong(contactId));
        } catch (NumberFormatException e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return Uri.withAppendedPath(person,
                ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }

    public static boolean hasContactPhoto(Context context, String number) {
//		String thumbUri = "";
        if (!number.equals(context.getString(R.string.UnablegetName))) {
            String photoId = "";
            String id = "";
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                    Uri.encode(number));
            Cursor cursor = context.getContentResolver().query(uri,
                    new String[]{ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.PHOTO_ID}, null, null, null);
            if (cursor.moveToFirst()) {
                id = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
                photoId = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_ID));
            }
            cursor.close();
            if (!id.equals("") && photoId != null && !photoId.equals(""))
                return true;
                //sms.setContactThumb(ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(id)).toString());
            else
                return false;
        }
        return false;
    }

    public static Bitmap loadContactPhoto(Context context, int id) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(
                ContactsContract.Contacts.CONTENT_URI, id);
        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, uri);
        if (input == null) {
            return null;
        }
        return BitmapFactory.decodeStream(input);

    }

    public static Uri loadUriContactPhoto(Context context, int id) {
        Uri uri = ContentUris.withAppendedId(
                ContactsContract.Contacts.CONTENT_URI, id);
        return uri;
    }


    public static String getContactIdFromPhoneNumber(Context context,
                                                     String phoneNumber) {
        // TODO Auto-generated method stub
        String contactId = "";
        Log.d("DebugPhonenumber",phoneNumber);
        if (!phoneNumber.equals(context.getString(R.string.UnablegetName))) {
            Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
                    Uri.encode(phoneNumber));
            Cursor cFetch = context.getContentResolver().query(uri,
                    new String[]{PhoneLookup.DISPLAY_NAME, PhoneLookup._ID},
                    null, null, null);
             contactId = "";

            if (cFetch != null) {
                if (cFetch.moveToFirst()) {

                    cFetch.moveToFirst();

                    contactId = cFetch
                            .getString(cFetch.getColumnIndex(PhoneLookup._ID));

                }
                cFetch.close();
            }
        }
        return contactId;

    }

    public static String getContactIdFromUri(Context context, Uri contactUri) {
        String id = "-1";
        Cursor cursor = context.getContentResolver().query(contactUri, null, null, null, null);
        if (cursor.moveToFirst()) {
            id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        }
        return id;
    }

    /**
     * Get all phone number of a contact
     *
     * @return
     */
    public static ArrayList<String> getPhoneNumber(Context context, String id) {
        ArrayList<String> phones = new ArrayList<String>();

        Cursor cursor = context.getContentResolver().query(
                CommonDataKinds.Phone.CONTENT_URI, null,
                CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id},
                null);

        while (cursor.moveToNext()) {
            phones.add(cursor.getString(cursor
                    .getColumnIndex(CommonDataKinds.Phone.NUMBER)));
        }

        cursor.close();

        return phones;
    }

    /**
     * Get all record from one folder.
     *
     * @param context
     * @return : list<CallRecordModel>
     */
    public static List<CallRecordModel> getAllRecord(Context context) {
        List<CallRecordModel> listRecord = new ArrayList<CallRecordModel>();
        String filepath = MyFileManager.getFilePath();
        final File file = new File(filepath,
                PreferUtils.getNameFolderSaveData(context));
        if (!file.exists()) {
            file.mkdirs();
        }
        listRecord = listDir2(file, context);

        return listRecord;
    }

    public static List<File> getAllFile(Context context) {
        List<File> listFile = new ArrayList<File>();
        String filepath = MyFileManager.getFilePath();
        final File file = new File(filepath,
                PreferUtils.getNameFolderSaveData(context));
        if (!file.exists()) {
            file.mkdirs();
        }
        listFile = listDir(file, context);
        return listFile;
    }

    private static List<File> listDir(File parentDir, Context context) {
        // TODO Auto-generated method stub
        File[] files = parentDir.listFiles();
        List<File> fileList = new ArrayList<File>();
        for (File file : files) {
            if (file.isDirectory()) {
                fileList.addAll(listDir(file, context));
            } else {
                fileList.add(file);
            }
        }

        return fileList;
    }

    public static File getOldestFile(Context context) {
        // Obtain the array of (file, timestamp) pairs.
        List<File> listFile = getAllFile(context);
        File[] files = listFile.toArray(new File[listFile.size()]);
        MyPair[] pairs = new MyPair[files.length];
        for (int i = 0; i < files.length; i++)
            pairs[i] = new MyPair(files[i]);

        // Sort them by timestamp.
        Arrays.sort(pairs);
        // Take the sorted pairs and extract only the file part, discarding the
        // timestamp.
        for (int i = 0; i < files.length; i++)
            files[i] = pairs[i].f;

        return files[0];
    }

    public static int getTotalFile(Context context) {
        String filepath = MyFileManager.getFilePath();
        final File folder = new File(filepath,
                PreferUtils.getNameFolderSaveData(context));
        // Obtain the array of (file, timestamp) pairs.
        return getFilesCount(folder);
    }

    public static int getFilesCount(File file) {
        File[] files = file.listFiles();
        int count = 0;
        for (File f : files)
            if (f.isDirectory())
                count += getFilesCount(f);
            else
                count++;

        return count;
    }

    public static void deleteOverFileLimitation(Context context, int numberFileDelete) {
        // Obtain the array of (file, timestamp) pairs.
        List<File> listFile = getAllFile(context);
        File[] files = listFile.toArray(new File[listFile.size()]);
        if (files != null) {
            MyPair[] pairs = new MyPair[files.length];
            for (int i = 0; i < files.length; i++)
                pairs[i] = new MyPair(files[i]);

            // Sort them by timestamp.
            Arrays.sort(pairs);
            // Take the sorted pairs and extract only the file part, discarding the
            // timestamp.
            for (int i = 0; i < numberFileDelete; i++) {
                deleteFile(pairs[i].f);
            }
        }

    }

    public static List<CallRecordModel> listDir2(File parentDir, Context context) {
        File[] files = parentDir.listFiles();
        List<CallRecordModel> fileList = new ArrayList<CallRecordModel>();
        for (File file : files) {
            // if (!file.getName().matches(FILE_NAME_PATTERN)) {
            // Log.d(TAG, String.format(
            // "'%s' didn't match the file name pattern",
            // file.getName()));
            // continue;
            // }
            if (file.isDirectory()) {
                fileList.addAll(listDir2(file, context));
            } else {
                CallRecordModel mModel = new CallRecordModel(file.getName());
                String phoneNum = mModel.getCallName().substring(19,
                        mModel.getCallName().length() - 4);
                mModel.setNameFromContact(getContactName(context, phoneNum));
                mModel.setPath(file.getAbsolutePath());
                fileList.add(mModel);
            }

        }

        // Collections.sort(fileList);
        Collections.sort(fileList, Collections.reverseOrder());

        return fileList;
    }

    public static long dirSize(File dir) {
//		long KILOBYTE = 1024;
        if (dir.exists()) {
            long result = 0;
            File[] fileList = dir.listFiles();
            if (fileList == null) return 0;
            for (int i = 0; i < fileList.length; i++) {
                // Recursive call if it's a directory
                if (fileList[i].isDirectory()) {
                    result += dirSize(fileList[i]);
                } else {
                    // Sum the file size in bytes
                    result += fileList[i].length();
                }
            }
//			return (result / (KILOBYTE * KILOBYTE)); // return the file size
            return result;
        }
        return 0;
    }

    /**
     * Return the size of a directory in Byte(B)
     */
    public static long getFolderDataSize(Context context) {
        String filepath = MyFileManager.getFilePath();
        final File file = new File(filepath,
                PreferUtils.getNameFolderSaveData(context));
        return dirSize(file);
    }

    /**
     * Counting number file in one folder
     *
     * @return : int
     */
    public static int getNumberRecord(Context context) {
        final File file = new File(MyFileManager.getFilePath(),
                PreferUtils.getNameFolderSaveData(context));
        return getFile(file.getPath());

    }

    public static int getFile(String dirPath) {
        int count = 0;
        File f = new File(dirPath);
        File[] files = f.listFiles();

        if (files != null)
            for (int i = 0; i < files.length; i++) {
                count++;
                File file = files[i];
                if (file.isDirectory()) {
                    getFile(file.getAbsolutePath());
                }
            }
        return count;
    }

    /**
     * Delete all file in one folder
     *
     * @param context
     */
    public static void deleteAllRecords(Context context) {
        String filepath = getFilePath() + "/"
                + PreferUtils.getNameFolderSaveData(context);
        File file = new File(filepath);

        deleteAllFile(file);
    }

    private static void deleteAllFile(File parentDir) {
        String listOfFileNames[] = parentDir.list();
        if (listOfFileNames == null) {
            return;
        }
        for (int i = 0; i < listOfFileNames.length; i++) {
            File file2 = new File(parentDir.getAbsolutePath() + "/" + listOfFileNames[i]);
            if (file2.isDirectory()) {
                deleteAllFile(file2);
                file2.delete();
            } else {
                file2.delete();
            }
        }
    }

    public static boolean hideMediaFile(Context context, String path) throws IOException {
        boolean isCreateSuccess = false;
        File mymirFolder = new File(path);
        File noMedia = new File(mymirFolder.getParent() + "/.nomedia");
        if (!mymirFolder.exists()) {
            mymirFolder.mkdirs();
            isCreateSuccess = noMedia.createNewFile();
        } else {
            if (!noMedia.exists()) {
                isCreateSuccess = noMedia.createNewFile();
            }
        }
        return isCreateSuccess;
    }

    public static boolean hideRootMediaFolder(Context connext) throws IOException {
        boolean isCreateSuccess = false;
        File mymirFolder = new File(MyFileManager.getFilePath()
                + File.separator + PreferUtils.getNameFolderSaveData(connext));
        File noMedia = new File(mymirFolder.getAbsolutePath() + "/.nomedia");
        if (!mymirFolder.exists()) {
            mymirFolder.mkdirs();
            isCreateSuccess = noMedia.createNewFile();
        } else {
            if (!noMedia.exists()) {
                isCreateSuccess = noMedia.createNewFile();
            }
        }
        return isCreateSuccess;
    }

}
