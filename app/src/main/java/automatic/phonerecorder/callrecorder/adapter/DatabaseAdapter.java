package automatic.phonerecorder.callrecorder.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import automatic.phonerecorder.callrecorder.controller.MyFileManager;
import automatic.phonerecorder.callrecorder.model.PriorityContactModel;
import automatic.phonerecorder.callrecorder.model.RecordModel;
import automatic.phonerecorder.callrecorder.model.SearchItem;
import automatic.phonerecorder.callrecorder.utils.MyConstants;
import automatic.phonerecorder.callrecorder.utils.PreferUtils;

import java.util.ArrayList;

public class DatabaseAdapter extends SQLiteOpenHelper {

	// Constants for Database

	private final static String DATABASE_NAME = "smobile_voicerecorder.db";
	private final static int DATABASE_VERSION = 4;

	private final static String TABLE_PRIORITY_CONTACTS = "priority_contacts";
	private final static String COLUMN_ID = "id";
	private final static String COLUMN_CONTACT_URI = "contact_uri";

	public final static String TABLE_INBOX = "inbox";
	public final static String TABLE_SAVE_RECORD = "save_record";
	public final static String TABLE_SEARCH_INDEX = "search_index";
	private final static String COLUMN_PHONENUMBER = "phonenumber";
	private final static String COLUMN_DATE = "date";
	private final static String COLUMN_PATH = "path";
	private final static String COLUMN_DURATION = "duration";
	private final static String COLUMN_STATUS = "status";
	private final static String COLUMN_SYNC = "sync";
	private final static String COLUMN_NOTE = "note";
	private final static String COLUMN_NAME_CONTACT = "name_contact";
	
	//database version 4 : Add new column ID original in search table
	private final static String COLUMN_ID_ORIGINAL = "id_original";

	// End Constants
	// Database creation sql statement
	private static final String CREATE_PRIORITY_CONTACTS_TABLE = "create table "
			+ TABLE_PRIORITY_CONTACTS
			+ "("
			+ COLUMN_ID
			+ " integer primary key autoincrement, "
			+ COLUMN_CONTACT_URI
			+ " text not null,"
			+ "UNIQUE("
			+ COLUMN_CONTACT_URI
			+ ") ON CONFLICT IGNORE);";

	private static final String CREATE_INBOX_TABLE = "create table "
			+ TABLE_INBOX + "(" 
			+ COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_PHONENUMBER + " text not null," 
			+ COLUMN_DATE + " integer," 
			+ COLUMN_PATH + " text not null," 
			+ COLUMN_DURATION + " integer," 
			+ COLUMN_STATUS + " integer," 
			+ COLUMN_SYNC + " integer," 
			+ COLUMN_NOTE + " text);";

	private static final String CREATE_SAVE_RECORD_TABLE = "create table "
			+ TABLE_SAVE_RECORD + "(" 
			+ COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_PHONENUMBER + " text not null," 
			+ COLUMN_DATE + " integer," 
			+ COLUMN_PATH + " text not null," 
			+ COLUMN_DURATION + " integer," 
			+ COLUMN_STATUS + " integer," 
			+ COLUMN_SYNC + " integer," 
			+ COLUMN_NOTE + " text);";
	private static final String CREATE_TABLE_SEARCH_INDEX = "create table "
			+ TABLE_SEARCH_INDEX + "(" 
			+ COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_PHONENUMBER + " text not null," 
			+ COLUMN_NAME_CONTACT + " text," 
			+ COLUMN_DATE + " integer," 
			+ COLUMN_PATH + " text not null," 
			+ COLUMN_DURATION + " integer," 
			+ COLUMN_STATUS + " integer," 
			+ COLUMN_SYNC + " integer," 
			+ COLUMN_ID_ORIGINAL + " integer," 
			+ COLUMN_NOTE + " text);";

	private static final String ADD_NEW_COLUMN_TABLE_SEARCH_INDEX = "ALTER TABLE " 
			+ TABLE_SEARCH_INDEX 
			+ " ADD COLUMN " 
			+ COLUMN_ID_ORIGINAL +" integer";
	
	
	
	private Context mContext;
	private static DatabaseAdapter instance;
	
	public DatabaseAdapter(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	public static synchronized DatabaseAdapter getInstance(Context context)
    {
        if (instance == null)
            instance = new DatabaseAdapter(context);

        return instance;
    }
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		// TODO Auto-generated method stub
		database.execSQL(CREATE_PRIORITY_CONTACTS_TABLE);
		database.execSQL(CREATE_INBOX_TABLE);
		database.execSQL(CREATE_SAVE_RECORD_TABLE);
		database.execSQL(CREATE_TABLE_SEARCH_INDEX);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		PreferUtils.saveIntPreferences(mContext, MyConstants.KEY_OLDVERSION_DATABSE, oldVersion);
		switch(oldVersion) {
		/** Add new table (Search index table ) for search function*/
		case 2:
			 db.execSQL(CREATE_TABLE_SEARCH_INDEX);
		case 3:
			 db.execSQL(ADD_NEW_COLUMN_TABLE_SEARCH_INDEX);
		}
	}
	
	public int getDatabaseVersion(){
		return DATABASE_VERSION;
	}

	public long addPriorityContact(Context context, Uri uriContact) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_CONTACT_URI, uriContact.toString());
		long index = db.insert(TABLE_PRIORITY_CONTACTS, null, values);
//		db.close();
		return index;
	}
	


	public ArrayList<PriorityContactModel> getAllPriorityContact() {
		ArrayList<PriorityContactModel> contacts = new ArrayList<PriorityContactModel>();
		SQLiteDatabase db = this.getReadableDatabase();
		String selectquery = "SELECT *FROM " + TABLE_PRIORITY_CONTACTS;
		Cursor cursor = db.rawQuery(selectquery, null);
		if (cursor.moveToFirst()) {
			do {
				PriorityContactModel contact = new PriorityContactModel();
				contact.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
				contact.setUriContact(Uri.parse(cursor.getString(cursor
						.getColumnIndex(COLUMN_CONTACT_URI))));
				contacts.add(contact);

			} while (cursor.moveToNext());
		}
		cursor.close();
//		db.close(); Fix issue : IllegalStateException: Cannot perform this operation because the connection pool has been closed.
		return contacts; 

	}

	public int deletePriorityContact(PriorityContactModel contact) {
		SQLiteDatabase db = this.getWritableDatabase();
		int rowIndex = db.delete(TABLE_PRIORITY_CONTACTS, COLUMN_ID + " =?",
				new String[] { String.valueOf(contact.getId()) });
//		db.close();
		return rowIndex;
	}

	public long addRecord(RecordModel record, String table) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_PHONENUMBER, record.getPhoneNumber());
		values.put(COLUMN_DATE, record.getDate());
		values.put(COLUMN_PATH, record.getPath());
		values.put(COLUMN_DURATION, record.getDuration());
		values.put(COLUMN_STATUS, record.getStatus());
		values.put(COLUMN_SYNC, record.getSync());
		values.put(COLUMN_NOTE, record.getNote());
		long index = db.insert(table, null, values);
//		db.close();
		return index;
	}
	public long addSearchIndex (Context context, RecordModel record){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_PHONENUMBER, record.getPhoneNumber());
		values.put(COLUMN_DATE, record.getDate());
		values.put(COLUMN_PATH, record.getPath());
		values.put(COLUMN_DURATION, record.getDuration());
		values.put(COLUMN_STATUS, record.getStatus());
		values.put(COLUMN_SYNC, record.getSync());
		values.put(COLUMN_NOTE, record.getNote());
		values.put(COLUMN_NAME_CONTACT, MyFileManager.getContactName(context, record.getPhoneNumber()));
		values.put(COLUMN_ID_ORIGINAL, record.getId());
		long index = db.insert(TABLE_SEARCH_INDEX, null, values);
//		db.close();
		return index;
	}

	public long updateNote(String table, int id, String note) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_NOTE, note);
		long index = db.update(table, values, "id="+id, null);
//		db.close();
		return index;
	}
	public long updateSearchNote(int id, String note) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_NOTE, note);
		long index = db.update(TABLE_SEARCH_INDEX, values,COLUMN_ID_ORIGINAL+"="+id, null);
//		db.close();
		return index;
	}
	public long updateIdOriginal(int  id, long newid){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_ID_ORIGINAL,newid);
		long index = db.update(TABLE_SEARCH_INDEX, cv, COLUMN_ID_ORIGINAL+"="+id,null);
		return index;
	}
	/**
	 *  int Sync value : 1-> true , 0-> false
	 * @param table
	 * @param id
	 * @param isSync
	 * @return
	 */
	public long updateSync(String table,int id, int isSync){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_STATUS, isSync);
		long index = db.update(table, values, "id="+id, null);
//		db.close();
		return index;
	}

	public int deleteRecord(RecordModel record, String table) {
		SQLiteDatabase db = this.getWritableDatabase();
		int rowIndex = db.delete(table, COLUMN_ID + " =?",
				new String[] { String.valueOf(record.getId()) });
//		db.close();
		return rowIndex;
	}
	
	public int deleteSearchItemIndex(RecordModel record){
		SQLiteDatabase db = this.getWritableDatabase();
		int rowIndex = db.delete(TABLE_SEARCH_INDEX, COLUMN_ID_ORIGINAL + " =?",
				new String[] { String.valueOf(record.getId()) });
//		db.close();
		return rowIndex;
	}
	
	public void deleteAllRecord(String table){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(table, null, null);
//		db.close();
	}
	
	public RecordModel getOldestRecord(String table){
		SQLiteDatabase db = this.getWritableDatabase();
		RecordModel record = new RecordModel();
		String selectquery = "SELECT *FROM " + table + 
				" WHERE " + COLUMN_DATE + " IN (SELECT " + COLUMN_DATE + 
				" FROM " + table + " ORDER BY " + COLUMN_DATE + " ASC LIMIT " + 1 + ")";
		Cursor cursor = db.rawQuery(selectquery, null);
		if (cursor.moveToFirst()) {
			do{
				record.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
				record.setPhoneNumber(cursor.getString(cursor.getColumnIndex(COLUMN_PHONENUMBER)));
				record.setDate(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE)));
				record.setPath(cursor.getString(cursor.getColumnIndex(COLUMN_PATH)));
				record.setDuration(cursor.getInt(cursor.getColumnIndex(COLUMN_DURATION)));
				record.setStatus(cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS)));
				record.setSync(cursor.getInt(cursor.getColumnIndex(COLUMN_SYNC)));
				record.setNote(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE)));
			}while (cursor.moveToNext());
		}
		cursor.close();
		return record;
	}
	
	public ArrayList<RecordModel> getOlderRecord(String table,int numberRecord){
		ArrayList<RecordModel> list = new ArrayList<RecordModel>();
		SQLiteDatabase db = this.getWritableDatabase();
//		final SQLiteStatement stmt = db.compileStatement("SELECT MIN(date) FROM "+table);
//		long rowID = stmt.simpleQueryForLong();
		String selectquery = "SELECT *FROM " + table + 
				" WHERE " + COLUMN_DATE + " IN (SELECT " + COLUMN_DATE + 
				" FROM " + table + " ORDER BY " + COLUMN_DATE + " ASC LIMIT " +numberRecord + ")";
//		db.execSQL(selectquery);
		Cursor cursor = db.rawQuery(selectquery, null);
		if (cursor.moveToFirst()) {
			do{
				RecordModel record = new RecordModel();
				record.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
				record.setPhoneNumber(cursor.getString(cursor.getColumnIndex(COLUMN_PHONENUMBER)));
				record.setDate(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE)));
				record.setPath(cursor.getString(cursor.getColumnIndex(COLUMN_PATH)));
				record.setDuration(cursor.getInt(cursor.getColumnIndex(COLUMN_DURATION)));
				record.setStatus(cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS)));
				record.setSync(cursor.getInt(cursor.getColumnIndex(COLUMN_SYNC)));
				record.setNote(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE)));
				list.add(record);
			}while (cursor.moveToNext());
		}
		cursor.close();
		return list;
	}

	public ArrayList<RecordModel> getListRecord(String table) {
		ArrayList<RecordModel> list = new ArrayList<RecordModel>();
		SQLiteDatabase db = this.getReadableDatabase();
		String selectquery = "SELECT *FROM " + table + " ORDER BY " + COLUMN_DATE + " DESC";
		Cursor cursor = db.rawQuery(selectquery, null);
		if (cursor.moveToFirst()) {
			do {
				RecordModel record = new RecordModel();
				record.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
				record.setPhoneNumber(cursor.getString(cursor.getColumnIndex(COLUMN_PHONENUMBER)));
				record.setDate(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE)));
				record.setPath(cursor.getString(cursor.getColumnIndex(COLUMN_PATH)));
				record.setDuration(cursor.getInt(cursor.getColumnIndex(COLUMN_DURATION)));
				record.setStatus(cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS)));
				record.setSync(cursor.getInt(cursor.getColumnIndex(COLUMN_SYNC)));
				record.setNote(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE)));
				list.add(record);
			} while (cursor.moveToNext());
		}

		cursor.close();
//		db.close();

		return list;
	}
	/**
	 * Get all list record from Search Index to improve speed search.
	 * @return
	 */
	public ArrayList<SearchItem> getListSearchIndex() {
		ArrayList<SearchItem> list = new ArrayList<>();
		SQLiteDatabase db = this.getReadableDatabase();
		String selectquery = "SELECT *FROM " + TABLE_SEARCH_INDEX + " ORDER BY " + COLUMN_DATE + " DESC";
		Cursor cursor = db.rawQuery(selectquery, null);
		if (cursor.moveToFirst()) {
			do {
				SearchItem record = new SearchItem();
				record.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
				record.setPhoneNumber(cursor.getString(cursor.getColumnIndex(COLUMN_PHONENUMBER)));
				record.setNameContact(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CONTACT)));
				record.setDate(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE)));
				record.setPath(cursor.getString(cursor.getColumnIndex(COLUMN_PATH)));
				record.setDuration(cursor.getInt(cursor.getColumnIndex(COLUMN_DURATION)));
				record.setStatus(cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS)));
				record.setSync(cursor.getInt(cursor.getColumnIndex(COLUMN_SYNC)));
				record.setNote(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE)));
				record.setId_original(cursor.getInt(cursor.getColumnIndex(COLUMN_ID_ORIGINAL)));
				list.add(record);
			} while (cursor.moveToNext());
		}

		cursor.close();
//		db.close();

		return list;
	}
	
	public ArrayList<RecordModel> getListRecordbyPhoneNumber(String table,String phonenumber){
		ArrayList<RecordModel> list = new ArrayList<RecordModel>();
		SQLiteDatabase db = this.getReadableDatabase();
		String[] selectionArg = new String[1];
		selectionArg[0]       = phonenumber + '%';
		String selectquery = "SELECT *FROM " + table 
				+ " WHERE " + COLUMN_PHONENUMBER + " LIKE ? "
				+ " ORDER BY " + COLUMN_DATE + " DESC";
		Cursor cursor = db.rawQuery(selectquery, selectionArg);
		if (cursor.moveToFirst()) {
			do {
				RecordModel record = new RecordModel();
				record.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
				record.setPhoneNumber(cursor.getString(cursor.getColumnIndex(COLUMN_PHONENUMBER)));
				record.setDate(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE)));
				record.setPath(cursor.getString(cursor.getColumnIndex(COLUMN_PATH)));
				record.setDuration(cursor.getInt(cursor.getColumnIndex(COLUMN_DURATION)));
				record.setStatus(cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS)));
				record.setSync(cursor.getInt(cursor.getColumnIndex(COLUMN_SYNC)));
				record.setNote(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE)));
				list.add(record);
			} while (cursor.moveToNext());
		}

		cursor.close();
//		db.close();
		return list;
	}
	
	/**
	 * Get total record in table database
	 * @param table
	 * @return
	 */
	public int getRecordCount(String table){
		SQLiteDatabase db = this.getReadableDatabase();
		String countQuery = "SELECT  * FROM " + table;
		Cursor cursor = db.rawQuery(countQuery, null);
		int cnt = cursor.getCount();
	    cursor.close();
//	    db.close();
	    return cnt;
	}

}
