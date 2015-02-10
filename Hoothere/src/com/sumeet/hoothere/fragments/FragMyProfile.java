package com.sumeet.hoothere.fragments;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.sumeet.callback.NetAPICallBack;
import com.sumeet.communication.CommunicationAPIManager;
import com.sumeet.global.Define;
import com.sumeet.global.Global;
import com.sumeet.global.WebConfig;
import com.sumeet.hoothere.MainActivity;
import com.sumeet.hoothere.R;
import com.sumeet.model.UserInformation;
import com.sumeet.util.AlertDialogManager;

public class FragMyProfile extends Fragment{
	
	private View mRootView;
	private boolean mbEditName = false;
    private Uri m_uriCameraFile;
    private Bitmap m_bmpAvatar = null;
    private File m_tempFile;
    private boolean mFlagFromMainMenu = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) parent.removeView(mRootView);
		}
		try {
            m_tempFile = new File(Environment.getExternalStorageDirectory(), Define.TEMP_AVATAR_FILE_NAME);
			mRootView = inflater.inflate(R.layout.fragment_myprofile, container, false);
			initUI();
			eventHandler();
		}catch(InflateException e){
			e.printStackTrace();
		}
		return mRootView;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if (mFlagFromMainMenu) MainActivity.instance.handleFooterTabs(Define.MAIN_FOOTER_TAB_INDEX_ME);
	}
	
	private void initUI(){
		((EditText)mRootView.findViewById(R.id.edt_frag_myprofile_content_name)).setEnabled(false);
		((EditText)mRootView.findViewById(R.id.edt_frag_myprofile_content_phone)).setEnabled(false);
		mRootView.findViewById(R.id.txt_frag_myprofile_header_save).setVisibility(View.GONE);
		
		if (!mFlagFromMainMenu){
			mRootView.findViewById(R.id.iv_frag_myprofile_header_back).setVisibility(View.VISIBLE);
			mRootView.findViewById(R.id.txt_frag_myprofile_header_logout).setVisibility(View.GONE);
		}
		
		if (Global.GUser != null){
			((EditText)mRootView.findViewById(R.id.edt_frag_myprofile_content_name)).setText(Global.GUser.fullName == null || Global.GUser.fullName.equals("null") ? "" : Global.GUser.fullName);
			((EditText)mRootView.findViewById(R.id.edt_frag_myprofile_content_phone)).setText(Global.GUser.mobile == null || Global.GUser.mobile.equals("null") ? "" : Global.GUser.mobile);
			((TextView)mRootView.findViewById(R.id.txt_frag_myprofile_content_email)).setText(Global.GUser.email == null || Global.GUser.email.equals("null") ? "" : Global.GUser.email);
			((TextView)mRootView.findViewById(R.id.txt_frag_myprofile_content_status)).setText(Global.GUser.availabilityStatus == null || Global.GUser.availabilityStatus.equals("null") ? 
					getString(R.string.change_status_want) : Global.GUser.availabilityStatus);
			
//			if (Global.GUser.activationStatus == null){
//				((TextView)mRootView.findViewById(R.id.txt_frag_myprofile_verify_status)).setText(R.string.verify);
//				((TextView)mRootView.findViewById(R.id.txt_frag_myprofile_verify_status)).setTextColor(getResources().getColor(R.color.color_text_blue_5d90cf));
//				((ImageView)mRootView.findViewById(R.id.iv_frag_myprofile_verify_status)).setImageResource(R.drawable.icon_verify);
//				mRootView.findViewById(R.id.rl_frag_myprofile_verify_section).setEnabled(true);
//				mRootView.findViewById(R.id.rl_frag_myprofile_verify_section).setClickable(true);
//			}else if (Global.GUser.activationStatus.equals(Define.USER_ACTIVATION_STATUS_A)){
//				((TextView)mRootView.findViewById(R.id.txt_frag_myprofile_verify_status)).setText(R.string.verified);
//				((TextView)mRootView.findViewById(R.id.txt_frag_myprofile_verify_status)).setTextColor(getResources().getColor(R.color.color_myprofile_verified));
//				((ImageView)mRootView.findViewById(R.id.iv_frag_myprofile_verify_status)).setImageResource(R.drawable.icon_verified);
//				mRootView.findViewById(R.id.rl_frag_myprofile_verify_section).setEnabled(false);
//				mRootView.findViewById(R.id.rl_frag_myprofile_verify_section).setClickable(false);
//			}else if(Global.GUser.activationStatus.equals(Define.USER_ACTIVATION_STATUS_P)){
//				((TextView)mRootView.findViewById(R.id.txt_frag_myprofile_verify_status)).setText(R.string.verify);
//				((TextView)mRootView.findViewById(R.id.txt_frag_myprofile_verify_status)).setTextColor(getResources().getColor(R.color.color_text_blue_5d90cf));
//				((ImageView)mRootView.findViewById(R.id.iv_frag_myprofile_verify_status)).setImageResource(R.drawable.icon_verify);
//				mRootView.findViewById(R.id.rl_frag_myprofile_verify_section).setEnabled(true);
//				mRootView.findViewById(R.id.rl_frag_myprofile_verify_section).setClickable(true);
//			}
			
			if (Global.GUser.isMobileVerified){
				((TextView)mRootView.findViewById(R.id.txt_frag_myprofile_verify_status)).setText(R.string.verified);
				((TextView)mRootView.findViewById(R.id.txt_frag_myprofile_verify_status)).setTextColor(getResources().getColor(R.color.color_myprofile_verified));
				((ImageView)mRootView.findViewById(R.id.iv_frag_myprofile_verify_status)).setImageResource(R.drawable.icon_verified);
				mRootView.findViewById(R.id.rl_frag_myprofile_verify_section).setEnabled(false);
				mRootView.findViewById(R.id.rl_frag_myprofile_verify_section).setClickable(false);
			}else{
				((TextView)mRootView.findViewById(R.id.txt_frag_myprofile_verify_status)).setText(R.string.verify);
				((TextView)mRootView.findViewById(R.id.txt_frag_myprofile_verify_status)).setTextColor(getResources().getColor(R.color.color_text_blue_5d90cf));
				((ImageView)mRootView.findViewById(R.id.iv_frag_myprofile_verify_status)).setImageResource(R.drawable.icon_verify);
				mRootView.findViewById(R.id.rl_frag_myprofile_verify_section).setEnabled(true);
				mRootView.findViewById(R.id.rl_frag_myprofile_verify_section).setClickable(true);
			}
			
			if (Global.GUser.availabilityStatus != null && !Global.GUser.availabilityStatus.equals("null")){
				if (Global.GUser.availabilityStatus.equals(getString(R.string.change_status_want))){
					((ImageView)mRootView.findViewById(R.id.iv_frag_myprofile_content_status)).setImageResource(R.drawable.icon_status_want_plan);
				}else if (Global.GUser.availabilityStatus.equals(getString(R.string.change_status_go_out))){
					((ImageView)mRootView.findViewById(R.id.iv_frag_myprofile_content_status)).setImageResource(R.drawable.icon_status_go_out);
				}else{
					((ImageView)mRootView.findViewById(R.id.iv_frag_myprofile_content_status)).setImageResource(R.drawable.icon_status_busy);
				}
			}else{
				((ImageView)mRootView.findViewById(R.id.iv_frag_myprofile_content_status)).setImageResource(R.drawable.icon_status_want_plan);
			}
			
			AQuery aq = new AQuery((ImageView)mRootView.findViewById(R.id.iv_frag_myprofile_content_avatar));
			ImageOptions imageOption = new ImageOptions();			
			imageOption.animation = AQuery.FADE_IN_FILE;
			imageOption.memCache = false;
			imageOption.fileCache = false;
			imageOption.preset = BitmapFactory.decodeResource(getResources(), R.drawable.defaultpic_large);
			imageOption.fallback = R.drawable.defaultpic_large;
			String strAvatarUrl = String.format(Define.USER_AVATAR_IMAGE_URL, WebConfig.BASE_URL, Global.GUser == null ? 0 : Global.GUser.userid);
			aq.image(strAvatarUrl, imageOption);
		}
	}
	
	private void eventHandler(){
		
		mRootView.findViewById(R.id.rl_frag_myprofile_verify_section).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragVerifyNumber fragVerifyNumber = new FragVerifyNumber();
				if (Global.GUser != null) fragVerifyNumber.setUserID(String.format("%d", Global.GUser.userid));
				if (Global.GUser != null && Global.GUser.mobile != null && !Global.GUser.mobile.equals("null")){
					fragVerifyNumber.setPhoneNumber(Global.GUser.mobile);
				}else{
					fragVerifyNumber.setPhoneNumber("");
				}
				MainActivity.instance.pushFragment(fragVerifyNumber, true);
			}
		});
		
		mRootView.findViewById(R.id.ll_frag_myprofile_content_mypast_events).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard((EditText)mRootView.findViewById(R.id.edt_frag_myprofile_content_name));
				MainActivity.instance.pushFragment(new FragMyPastEvents(), true);
			}
		});
		
		mRootView.findViewById(R.id.iv_frag_myprofile_header_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard((EditText)mRootView.findViewById(R.id.edt_frag_myprofile_content_name));
				MainActivity.instance.onBackPressed();
			}
		});
		
		mRootView.findViewById(R.id.txt_frag_myprofile_header_logout).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.instance.pushFragment(new FragLogo(), false);
				MainActivity.config.saveLoggedIn(false);
				MainActivity.config.removeSavedInfo();
			}
		});
		
		mRootView.findViewById(R.id.iv_frag_myprofile_content_avatar_camera).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard((EditText)mRootView.findViewById(R.id.edt_frag_myprofile_content_name));
				mRootView.findViewById(R.id.txt_frag_myprofile_header_save).setVisibility(View.VISIBLE);
				selectAvatarImage();
			}
		});
		
		mRootView.findViewById(R.id.rl_frag_myprofile_content_name_edit).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mRootView.findViewById(R.id.txt_frag_myprofile_header_save).setVisibility(View.VISIBLE);
				mbEditName = !mbEditName;
				if (mbEditName){
					((EditText)mRootView.findViewById(R.id.edt_frag_myprofile_content_name)).setEnabled(true);
					((EditText)mRootView.findViewById(R.id.edt_frag_myprofile_content_name)).requestFocus();
					displayKeyboard((EditText)mRootView.findViewById(R.id.edt_frag_myprofile_content_name));
				}else{
					((EditText)mRootView.findViewById(R.id.edt_frag_myprofile_content_name)).setEnabled(false);
					hideKeyboard((EditText)mRootView.findViewById(R.id.edt_frag_myprofile_content_name));
				}
			}
		});
		
		mRootView.findViewById(R.id.ll_frag_myprofile_content_pwd).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard((EditText)mRootView.findViewById(R.id.edt_frag_myprofile_content_name));
				MainActivity.instance.pushFragment(new FragChangePwd(), true);
			}
		});
		
		mRootView.findViewById(R.id.rl_frag_myprofile_content_status_edit).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		
		mRootView.findViewById(R.id.btn_frag_myprofile_content_myhosted_events).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		
		mRootView.findViewById(R.id.txt_frag_myprofile_header_save).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveProfile();
			}
		});
	}
	
	private void displayOKAlertDialog(String strMessage){
		AlertDialogManager.sharedManager(MainActivity.instance).showConfirmDialog(getString(R.string.alert_dlg_title_hoothere),
																				strMessage, 
																				getString(R.string.alert_dlg_btn_ok), 
																				null, 
																				null);
	}
	
	private void displayOKAlertDialog(int resId){
		AlertDialogManager.sharedManager(MainActivity.instance).showConfirmDialog(getString(R.string.alert_dlg_title_hoothere), 
																				getString(resId), 
																				getString(R.string.alert_dlg_btn_ok), 
																				null, 
																				null);
	}
	
	public void setFlagFromMainMenu(boolean bFlag){
		mFlagFromMainMenu = bFlag;
	}
	
    private void selectAvatarImage() {
		MainActivity.instance.m_curFrag = this;
        final CharSequence[] items = {Define.TITLE_CAMERA, Define.TITLE_GALLERY};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(Define.TITLE_SELECTPICTURE);
        builder.setCancelable(true);
        builder.setPositiveButton(getString(R.string.alert_dlg_cancel), null);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        chooseFromCamera();
                        break;
                    case 1:
                        chooseFromGallery();
                        break;
                    default:
                        break;
                }
            }
        }).show();
    }

    private void chooseFromCamera() {
    	try{
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.ENGLISH);
	        String fileName = dateFormat.format(new Date()) + ".jpg";
	        File photo = new File(Environment.getExternalStorageDirectory(), fileName);
	        m_uriCameraFile = Uri.fromFile(photo);
	        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
	        intent.putExtra(MediaStore.EXTRA_OUTPUT, m_uriCameraFile);
	        startActivityForResult(intent, Define.ACTION_REQUEST_CAMERA);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }

    private void chooseFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, Define.ACTION_REQUEST_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_CANCELED) {
            switch (requestCode) {
            case Define.ACTION_REQUEST_GALLERY:
                Uri selectedImage = data.getData();
                if (!performCrop(selectedImage)) {
                    String selectedImagePath = getPath(selectedImage);
                    m_bmpAvatar = getBitmapFromUri(Uri.parse(selectedImagePath));
                    ((ImageView)mRootView.findViewById(R.id.iv_frag_myprofile_content_avatar)).setImageBitmap(m_bmpAvatar);
                }
                break;

            case Define.ACTION_REQUEST_CAMERA:
                if (!performCrop(m_uriCameraFile)) {
                    m_bmpAvatar = getBitmapFromUri(m_uriCameraFile);
                    ((ImageView)mRootView.findViewById(R.id.iv_frag_myprofile_content_avatar)).setImageBitmap(m_bmpAvatar);
                }
                break;

            case Define.ACTION_REQUEST_PIC_CROP:
                if (data != null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inDensity = 72;
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    m_bmpAvatar = BitmapFactory.decodeFile(m_tempFile.getPath(), options);
                    ((ImageView)mRootView.findViewById(R.id.iv_frag_myprofile_content_avatar)).setImageBitmap(m_bmpAvatar);
                }
                break;
            }
        }
    }

    private Boolean performCrop(Uri picUri) {
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setType("image/*");

            List<ResolveInfo> list = MainActivity.instance.getPackageManager().queryIntentActivities(intent, 0);
            if (list.size() < 1)
                return false;

            Intent cropIntent = new Intent(intent);
            int index = 0;
            if (list.size() > 1)
                index = 1;

            ResolveInfo res = list.get(index);
            cropIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 600);
            cropIntent.putExtra("outputY", 600);
            cropIntent.putExtra("scale", true);
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(m_tempFile));
            cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            cropIntent.putExtra("return-data", false);

            startActivityForResult(cropIntent, Define.ACTION_REQUEST_PIC_CROP);
        }
        catch (ActivityNotFoundException anfe) {
        	Toast.makeText(getActivity(), R.string.toast_crop_not_supported, Toast.LENGTH_SHORT).show();
            return false;
        }catch(Exception e){
        	e.printStackTrace();
        	return false;
        }

        return true;
    }

    private String getPath(Uri uri) {
        if (uri == null) {
            return null;
        }

        try{
	        String[] projection = { MediaStore.Images.Media.DATA };
	        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
	        if (cursor != null) {
	            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	            cursor.moveToFirst();
	            return cursor.getString(column_index);
	        }
        }catch(Exception e){
        	e.printStackTrace();
        	return null;
        }
        return uri.getPath();
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap thumb = BitmapFactory.decodeFile(uri.getPath(), options);
            return thumb;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void uploadAvatar(){
    	if (m_bmpAvatar == null) return;
    	
		byte[] avatarData = null;

        if (m_bmpAvatar != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            m_bmpAvatar.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            avatarData = stream.toByteArray();
        }
        
        if (avatarData == null) return;
		new CommunicationAPIManager(MainActivity.instance).sendUpdateAvatarRequest(avatarData, new NetAPICallBack(){

			@Override
			public void succeed(JSONObject responseObj) {
				
			}

			@Override
			public void failed(JSONObject errorObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							displayOKAlertDialog(R.string.alert_dlg_uploading_photo_error);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}

			@Override
			public void failed(VolleyError error) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							displayOKAlertDialog(R.string.alert_dlg_uploading_photo_error);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}
		});
    }
    
    private void saveProfile(){
    	hideKeyboard((EditText)mRootView.findViewById(R.id.edt_frag_myprofile_content_name));
    	if (Global.GUser == null) return;
		uploadAvatar();
		final UserInformation user = Global.GUser;
		String strFullName = ((EditText)mRootView.findViewById(R.id.edt_frag_myprofile_content_name)).getText().toString();
		String names[] = strFullName.split(" ");
		if (names != null && names.length > 0){
			if (names.length == 1){
				user.firstName = names[0].trim();
				user.lastName = "";
				user.middleName = "";
			}else{
				if (names.length == 2){
					user.firstName = names[0].trim();
					user.lastName = names[1].trim();
					user.middleName = "";
				}else{
					user.firstName = names[0].trim();
					user.lastName = names[names.length - 1].trim();
					user.middleName = "";
					for (int i = 1; i < names.length - 1; i++){
						if (i == names.length - 2){
							user.middleName += names[i].trim();
						}else{
							user.middleName += names[i].trim() + " ";
						}
					}
				}
			}
		}
		
		final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_updating_profile));
		new CommunicationAPIManager(MainActivity.instance).sendUpdateMyProfileRequest(user.asJSONForUpdate(), new NetAPICallBack(){
			@Override
			public void succeed(final JSONObject responseObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (progressDlg != null) progressDlg.dismiss();
							if (responseObj == null){
								Global.GUser = user;
								displayOKAlertDialog(R.string.alert_dlg_update_profile_success);
							}else if (responseObj.has(Define.USERINFO_ID)){
								Global.GUser = new UserInformation(responseObj);
								displayOKAlertDialog(R.string.alert_dlg_update_profile_success);
							}else if(responseObj.has(Define.ERRORMESSAGE_TAG)){
								displayOKAlertDialog(responseObj.optString(Define.ERRORMESSAGE_TAG));
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}

			@Override
			public void failed(final JSONObject errorObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (progressDlg != null) progressDlg.dismiss();
							if (errorObj != null && errorObj.has(Define.ERRORMESSAGE_TAG)){
								String errorMessage = errorObj.optString(Define.ERRORMESSAGE_TAG);
								displayOKAlertDialog(errorMessage == null || errorMessage.equals("null") || errorMessage.isEmpty() ? getString(R.string.alert_dlg_update_profile_error) : errorMessage);
							}else{
								displayOKAlertDialog(R.string.alert_dlg_update_profile_success);
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}

			@Override
			public void failed(final VolleyError error) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (progressDlg != null) progressDlg.dismiss();
							if (error != null){
								if (error.getMessage() != null && error.getMessage().equals(Define.JSONEXCEPTION_SUCCESS)){
									displayOKAlertDialog(R.string.alert_dlg_update_profile_success);
								}else if (error.getMessage() == null){
									displayOKAlertDialog(R.string.alert_dlg_update_profile_success);
								}else{
									displayOKAlertDialog(R.string.alert_dlg_update_profile_error);
								}
							}else{
								displayOKAlertDialog(R.string.alert_dlg_update_profile_error);
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}
		});
    }
    
    private void hideKeyboard(EditText edt){
		InputMethodManager imm = (InputMethodManager)MainActivity.instance.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
    }
    
    private void displayKeyboard(EditText edt){
		InputMethodManager imm = (InputMethodManager)MainActivity.instance.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(edt, InputMethodManager.SHOW_FORCED);
    }
}