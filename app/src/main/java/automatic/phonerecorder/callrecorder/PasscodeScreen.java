package automatic.phonerecorder.callrecorder;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import automatic.phonerecorder.callrecorder.utils.Utilities;
import automatic.phonerecorder.callrecorder.utils.MyConstants;
import automatic.phonerecorder.callrecorder.utils.PreferUtils;

public class PasscodeScreen {
	private Activity mActivity;
	private View mPasscodeScreen;
	private DrawerLayout mDrawerLayout;
	
	private Button mNumPad_0;
	private Button mNumPad_1;
	private Button mNumPad_2;
	private Button mNumPad_3;
	private Button mNumPad_4;
	private Button mNumPad_5;
	private Button mNumPad_6;
	private Button mNumPad_7;
	private Button mNumPad_8;
	private Button mNumPad_9;
	private ImageButton mNumPad_erase;
	
	private EditText mPinField_1;
	private EditText mPinField_2;
	private EditText mPinField_3;
	private EditText mPinField_4;
	
	private TextView mResetPassword;
	

	public void initLayout(Activity activity,View passcodeScreen,Button numPad_0, Button numPad_1,
			Button numPad_2, Button numPad_3, Button numPad_4, Button numPad_5,
			Button numPad_6, Button numPad_7, Button numPad_8, Button numPad_9,
			ImageButton numPad_erase , EditText pinfield_1,EditText pinfield_2,EditText pinfield_3,EditText pinfield_4,
			TextView resetPassword) {
		
		this.mActivity = activity;
		this.mPasscodeScreen = passcodeScreen;
		
		this.mNumPad_0 = numPad_0;
		this.mNumPad_1 = numPad_1;
		this.mNumPad_2 = numPad_2;
		this.mNumPad_3 = numPad_3;
		this.mNumPad_4 = numPad_4;
		this.mNumPad_5 = numPad_5;
		this.mNumPad_6 = numPad_6;
		this.mNumPad_7 = numPad_7;
		this.mNumPad_8 = numPad_8;
		this.mNumPad_9 = numPad_9;
		this.mNumPad_erase = numPad_erase;
		
		this.mPinField_1 = pinfield_1;
		this.mPinField_2 = pinfield_2;
		this.mPinField_3 = pinfield_3;
		this.mPinField_4 = pinfield_4;
		
		this.mResetPassword = resetPassword;
		
		this.mNumPad_0.setOnClickListener(mNumPad_0_handler);
		this.mNumPad_1.setOnClickListener(mNumPad_1_handler);
		this.mNumPad_2.setOnClickListener(mNumPad_2_handler);
		this.mNumPad_3.setOnClickListener(mNumPad_3_handler);
		this.mNumPad_4.setOnClickListener(mNumPad_4_handler);
		this.mNumPad_5.setOnClickListener(mNumPad_5_handler);
		this.mNumPad_6.setOnClickListener(mNumPad_6_handler);
		this.mNumPad_7.setOnClickListener(mNumPad_7_handler);
		this.mNumPad_8.setOnClickListener(mNumPad_8_handler);
		this.mNumPad_9.setOnClickListener(mNumPad_9_handler);
		this.mNumPad_erase.setOnClickListener(mNumPad_erase_handler);
		this.mResetPassword.setOnClickListener(mResetpassword_handler);
		
	}
	
	public void setDrawerLayout(DrawerLayout drawer){
		this.mDrawerLayout = drawer;
	}


	public void showPasscode(){
		mPasscodeScreen.setVisibility(View.VISIBLE);
//		mToolbar.setDisplayHomeAsUpEnabled(false);
//		MainActivity.isShowPasscodeScreen = true;
		mActivity.invalidateOptionsMenu();
	}
	
	public void hidePasscode(){
		mPasscodeScreen.setVisibility(View.GONE);
//		mToolbar.setDisplayHomeAsUpEnabled(true);
//		MainActivity.isShowPasscodeScreen = false;
		mActivity.invalidateOptionsMenu();
		if(mDrawerLayout != null ) {
			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

		}
		
	}

	OnClickListener mNumPad_0_handler = new OnClickListener() {
		public void onClick(View v) {
			setPassword(0);
		}
	};
	OnClickListener mNumPad_1_handler = new OnClickListener() {
		public void onClick(View v) {
			setPassword(1);
		}
	};
	OnClickListener mNumPad_2_handler = new OnClickListener() {
		public void onClick(View v) {
			setPassword(2);
		}
	};
	OnClickListener mNumPad_3_handler = new OnClickListener() {
		public void onClick(View v) {
			setPassword(3);
		}
	};
	OnClickListener mNumPad_4_handler = new OnClickListener() {
		public void onClick(View v) {
			setPassword(4);
		}
	};
	OnClickListener mNumPad_5_handler = new OnClickListener() {
		public void onClick(View v) {
			setPassword(5);
		}
	};
	OnClickListener mNumPad_6_handler = new OnClickListener() {
		public void onClick(View v) {
			setPassword(6);
		}
	};
	OnClickListener mNumPad_7_handler = new OnClickListener() {
		public void onClick(View v) {
			setPassword(7);
		}
	};
	OnClickListener mNumPad_8_handler = new OnClickListener() {
		public void onClick(View v) {
			setPassword(8);
		}
	};
	OnClickListener mNumPad_9_handler = new OnClickListener() {
		public void onClick(View v) {
			setPassword(9);
		}
	};
	
	OnClickListener mResetpassword_handler = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			showDialogResetPassword(mActivity);
		}


	};
	private void showDialogResetPassword(final Activity activity) {
		// TODO Auto-generated method stub
		final Dialog resetPasswordDialog = new Dialog(activity);
		
		resetPasswordDialog.setTitle(activity.getString(R.string.passcode_reset_password));
		resetPasswordDialog.setContentView(R.layout.dialog_reset_password);
		final EditText old_password = (EditText) resetPasswordDialog
				.findViewById(R.id.dialog_reset_confirm_edt_old_password);
		final EditText new_password = (EditText) resetPasswordDialog
				.findViewById(R.id.dialog_reset_confirm_edt_new_password);
		final EditText passwordConfirm = (EditText) resetPasswordDialog
				.findViewById(R.id.dialog_reset_confirm_edt_confirmpassword);
		final TextSwitcher message = (TextSwitcher) resetPasswordDialog
				.findViewById(R.id.dialog_reset_confirm_txt_waring);
		message.setInAnimation(activity, R.anim.shake_animation);
		message.setInAnimation(activity, R.anim.shake_animation);
		message.setFactory(new ViewFactory() {
			@Override
			public View makeView() {
				TextView textView = new TextView(activity);
				textView.setTextColor(Color.parseColor("#FF7F27"));
				textView.setGravity(Gravity.CENTER_HORIZONTAL);
				textView.setTypeface(Typeface.DEFAULT_BOLD);
				return textView;
			}
		});
		Button OK = (Button) resetPasswordDialog
				.findViewById(R.id.dialog_reset_confirm_btn_ok);
		Button cancel = (Button) resetPasswordDialog
				.findViewById(R.id.dialog_reset_confirm_btn_cancel);
		OK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Xu ly reset password tai day
				if (!Utilities.md5Digest(old_password.getText().toString().trim())
						.matches(PreferUtils.getStringPreferences(
										activity,
										MyConstants.KEY_PASSWORD_FOR_PRIVATE_MODE))) {
					message.setVisibility(View.VISIBLE);
					message.setText(activity.getString(R.string.old_password_is_incorrect));
				} else if (Utilities.isEditTextEmpty(new_password)) {
					message.setVisibility(View.VISIBLE);
					message.setText(activity.getString(R.string.password_is_empty));
				} else if (!Utilities.ConpareEdit(new_password, passwordConfirm)) {
					message.setVisibility(View.VISIBLE);
					message.setText(activity.getString(R.string.wrong_match_password));
				} else if(new_password.length()<4){
					message.setVisibility(View.VISIBLE);
					message.setText(activity.getString(R.string.wrong_lenght_password));
				} else {
					resetPasswordDialog.dismiss();
					PreferUtils.saveStringPreferences(
							activity,
							MyConstants.KEY_PASSWORD_FOR_PRIVATE_MODE,
							Utilities.md5Digest(new_password.getText().toString()
									.trim()));
				}

			}
			
		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				resetPasswordDialog.dismiss();
			}
			
		});
		resetPasswordDialog.show();
	}
	OnClickListener mNumPad_erase_handler = new OnClickListener() {
		public void onClick(View v) {
			if(!mPinField_4.getText().toString().equals("")){
				mPinField_4.setText("");
				mPinField_4.setSelected(false);
			}else if (!mPinField_3.getText().toString().equals("")) {
				mPinField_3.setText("");
				mPinField_3.setSelected(false);
			}else if (!mPinField_2.getText().toString().equals("")) {
				mPinField_2.setText("");
				mPinField_2.setSelected(false);
			}else if (!mPinField_1.getText().toString().equals("")) {
				mPinField_1.setText("");
				mPinField_1.setSelected(false);
			}
		}
	};
	
	@SuppressWarnings("deprecation")
	private void setPassword(int number){
		if(mPinField_1.getText().toString().equals("")){
			mPinField_1.setText(""+number);
			mPinField_1.setSelected(true);
			mPinField_1.setTextColor(mActivity.getResources().getColor(R.color.actionmodeBackground));

		}else if (mPinField_2.getText().toString().equals("")) {
			mPinField_2.setText(""+number);
			mPinField_2.setSelected(true);
			mPinField_2.setTextColor(mActivity.getResources().getColor(R.color.actionmodeBackground));

		}else if (mPinField_3.getText().toString().equals("")) {
			mPinField_3.setText(""+number);
			mPinField_3.setSelected(true);
			mPinField_3.setTextColor(mActivity.getResources().getColor(R.color.actionmodeBackground));

		}else if (mPinField_4.getText().toString().equals("")) {
			mPinField_4.setText(""+number);
			mPinField_4.setSelected(true);
			mPinField_4.setTextColor(mActivity.getResources().getColor(R.color.actionmodeBackground));
			
			new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
		        @Override
		        public void run() {
		            //Do something here
		        	verifyPassword();
		        }
		    }, 250);
		}
		
		
	}
	
	private void verifyPassword(){
		String number1 = mPinField_1.getText().toString();
		String number2 = mPinField_2.getText().toString();
		String number3 = mPinField_3.getText().toString();
		String number4 = mPinField_4.getText().toString();
		if(Utilities.md5Digest(number1+number2+number3+number4).matches(
				PreferUtils.getStringPreferences(mActivity, MyConstants.KEY_PASSWORD_FOR_PRIVATE_MODE))){
			mPinField_1.setText("");
			mPinField_1.setSelected(false);
			mPinField_2.setText("");
			mPinField_2.setSelected(false);
			mPinField_3.setText("");
			mPinField_3.setSelected(false);
			mPinField_4.setText("");
			mPinField_4.setSelected(false);
			hidePasscode();
			PreferUtils.savebooleanPreferences(mActivity, MyConstants.KEY_IS_LOGINED, true);
		}else {
			Animation shake = AnimationUtils.loadAnimation(mActivity, R.anim.wrong_pass_animation);
			mPinField_1.startAnimation(shake);
			mPinField_1.setText("");
			mPinField_1.setSelected(false);
			
			mPinField_2.startAnimation(shake);
			mPinField_2.setText("");
			mPinField_2.setSelected(false);
			
			mPinField_3.startAnimation(shake);
			mPinField_3.setText("");
			mPinField_3.setSelected(false);
			
			mPinField_4.startAnimation(shake);
			mPinField_4.setText("");
			mPinField_4.setSelected(false);
		}
		
	}


}
