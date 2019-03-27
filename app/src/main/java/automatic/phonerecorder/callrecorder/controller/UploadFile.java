package automatic.phonerecorder.callrecorder.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import automatic.phonerecorder.callrecorder.R;
import automatic.phonerecorder.callrecorder.utils.PreferUtils;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.UploadRequest;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxFileSizeException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import automatic.phonerecorder.callrecorder.utils.MyConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class UploadFile extends AsyncTask<String, Long, Boolean> implements MyConstants{
	
	private DropboxAPI<?> dropboxApi;
	private Context mContext;
	
	private NotificationController mNotificationHelper;
	private final int mNotificationUploadId = 1988;
	private String mErrorMsg;
	
	public UploadFile(Context context, DropboxAPI<?> dropboxApi) {
		this.mContext = context.getApplicationContext();
		this.dropboxApi = dropboxApi;
		mNotificationHelper = new NotificationController(context);
	}
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		// Show notification
		mNotificationHelper.createNotification(mNotificationUploadId,
				R.drawable.ic_sync,
				mContext.getString(R.string.sync_notification_content_text),
				mContext.getString(R.string.sync_notification_content_title));
	}

	@Override
	protected Boolean doInBackground(String... modeUpload) {
		// TODO Auto-generated method stub
		FileInputStream fileInputStream;
		try {
			if (modeUpload[0].equals(MODE_UPLOAD_FOLDER) ) {
				List<File> listFile = getAllRecordFromSdcard();
				File[] mFilesToUpload = listFile.toArray(new File[listFile.size()]);
				for (int i = 0; i < mFilesToUpload.length; i++) {
					File file = mFilesToUpload[i];
					// publishProgress(Long.parseLong(""+i));
					fileInputStream = new FileInputStream(file);
					String path = PreferUtils.getNameFolderSaveData(mContext)
								+ File.separator 
								+ file.getParentFile().getName()
								+ File.separator
								+ file.getName();
					if(DEBUG) Log.i(TAG, "MODE_UPLOAD_FOLDER : " +path);
					UploadRequest request = dropboxApi.putFileOverwriteRequest(path, fileInputStream,
									file.length(), null);
					request.upload();
					if (isCancelled()) return false;
				}
				return true;
			}else if (modeUpload[0].equals(MODE_UPLOAD_ONE_FILE)) {
				File file = new File(modeUpload[2]);

				 //Uploading the newly created file to Dropbox.
				fileInputStream = new FileInputStream(file);
				String path = PreferUtils.getNameFolderSaveData(mContext)
						+ File.separator 
						+ file.getParentFile().getName()
						+ File.separator
						+ file.getName();
				if(DEBUG) Log.i(TAG, "MODE_UPLOAD_ONE_FILE : " +path);
				UploadRequest requestOneFile = dropboxApi.putFileOverwriteRequest(path, fileInputStream,
									file.length(), null);
				requestOneFile.upload();
				if (isCancelled()) return false;
				 
				return true;
			}else {
				if(DEBUG) Log.i(TAG, "Don't have any mode upload");
				return false;
			}

		} catch (DropboxUnlinkedException e) {
			// This session wasn't authenticated properly or user unlinked
			if (DEBUG)
				Log.e(TAG, "DropboxUnlinkedException :"
						+ "This app wasn't authenticated properly.");
		} catch (DropboxFileSizeException e) {
			// File size too big to upload via the API
			mErrorMsg = "This recording is too big to be uploaded";
			if (DEBUG)
				Log.e(TAG, "DropboxFileSizeException: " + mErrorMsg);
		} catch (DropboxPartialFileException e) {
			// We canceled the operation
			mErrorMsg = "Upload canceled";
			if (DEBUG)
				Log.e(TAG, "DropboxPartialFileException: " + mErrorMsg);
		} catch (DropboxServerException e) {
			// Server-side exception. These are examples of what could
			// happen,
			// but we don't do anything special with them here.
			if (e.error == DropboxServerException._401_UNAUTHORIZED) {
				// Unauthorized, so we should unlink them. You may want to
				// automatically log the user out in this case.
				if (DEBUG)
					Log.e(TAG, "DropboxServerException: "
							+ "401_UNAUTHORIZED");
			} else if (e.error == DropboxServerException._403_FORBIDDEN) {
				// Not allowed to access this
				if (DEBUG)
					Log.e(TAG, "DropboxServerException: " + "403_FORBIDDEN");
			} else if (e.error == DropboxServerException._404_NOT_FOUND) {
				// path not found (or if it was the thumbnail, can't be
				// thumbnailed)
				if (DEBUG)
					Log.e(TAG, "DropboxServerException: " + "404_NOT_FOUND");
			} else if (e.error == DropboxServerException._507_INSUFFICIENT_STORAGE) {
				// user is over quota
				if (DEBUG)
					Log.e(TAG, "DropboxServerException: "
							+ "507_INSUFFICIENT_STORAGE");
			} else {
				// Something else
				if (DEBUG)
					Log.e(TAG, "DropboxServerException: " + "UNKNOW ERROR");
			}
			// This gets the Dropbox error, translated into the user's
			// language
			mErrorMsg = e.body.userError;
			if (mErrorMsg == null) {
				mErrorMsg = e.body.error;
			}
		} catch (DropboxIOException e) {
			e.printStackTrace();
			// Happens all the time, probably want to retry automatically.
			mErrorMsg = "Network error.  Try again.";
			if (DEBUG)
				Log.e(TAG, "DropboxIOException: " + mErrorMsg);
		} catch (DropboxParseException e) {
			// Probably due to Dropbox server restarting, should retry
			mErrorMsg = "Dropbox error.  Try again.";
			if (DEBUG)
				Log.e(TAG, "DropboxIOException: " + mErrorMsg);
		} catch (DropboxException e) {
			// Unknown error
			mErrorMsg = "Unknown error.  Try again.";
			if (DEBUG)
				Log.e(TAG, "DropboxIOException: " + mErrorMsg);
		} catch (FileNotFoundException e) {
			if (DEBUG)
				Log.e(TAG, "FileNotFoundException: ");
		} catch (AbstractMethodError e) {
			// TODO: handle exception
			if (DEBUG)
				Log.e(TAG, "AbstractMethodError: ");
		}

		return false;
		
	}
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		// Hide notifications
//		mNotificationHelper.completed(mNotificationUploadId);
	}
	
	/**
	 * Get all file record in my save folder
	 * 
	 * @return
	 */
	private List<File> getAllRecordFromSdcard() {
		List<File> listAllFile = new ArrayList<File>();
		File parentDir = new File(MyFileManager.getFilePath(),
				PreferUtils.getNameFolderSaveData(mContext));
		listAllFile = listDir(parentDir, mContext);
		return listAllFile;
		
	}
	private List<File> listDir (File parentDir, Context context){
		File[] files = parentDir.listFiles();
		List<File> fileList = new ArrayList<File>();
		for (File file : files) {
			if (file.isDirectory()) {			
				fileList.addAll(listDir(file, mContext));
			} else {
				fileList.add(file);
			}
		}
		return fileList;
	}

}
