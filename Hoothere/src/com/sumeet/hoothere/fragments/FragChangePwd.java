package com.sumeet.hoothere.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.sumeet.callback.NetAPICallBack;
import com.sumeet.communication.CommunicationAPIManager;
import com.sumeet.global.Define;
import com.sumeet.hoothere.AlertDlgCallback;
import com.sumeet.hoothere.MainActivity;
import com.sumeet.hoothere.R;
import com.sumeet.util.AlertDialogManager;

public class FragChangePwd extends Fragment{
	
	private View mRootView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) parent.removeView(mRootView);
		}
		try {
			mRootView = inflater.inflate(R.layout.fragment_change_pwd, container, false);
			initUI();
			eventHandler();
		}catch(InflateException e){
			e.printStackTrace();
		}
		return mRootView;
	}
	
	private void initUI(){
		
	}
	
	private void hideKeyboard(){
		InputMethodManager imm = (InputMethodManager)MainActivity.instance.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(((EditText)mRootView.findViewById(R.id.edt_frag_change_pwd_content_current_pwd)).getWindowToken(), 0);
		imm.hideSoftInputFromWindow(((EditText)mRootView.findViewById(R.id.edt_frag_change_pwd_content_new_pwd)).getWindowToken(), 0);
		imm.hideSoftInputFromWindow(((EditText)mRootView.findViewById(R.id.edt_frag_change_pwd_content_confirm_pwd)).getWindowToken(), 0);
	}
	
	private void displayOKAlertDialog(String strMessage, AlertDlgCallback callback){
		AlertDialogManager.sharedManager(MainActivity.instance).showConfirmDialog(getString(R.string.alert_dlg_title_hoothere), 
																				strMessage, 
																				getString(R.string.alert_dlg_btn_ok), 
																				null, 
																				callback);
	}
	
	private void displayOKAlertDialog(int resId, AlertDlgCallback callback){
		displayOKAlertDialog(getString(resId), callback);
	}
	
	private void eventHandler(){
		mRootView.findViewById(R.id.iv_frag_change_pwd_header_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				MainActivity.instance.onBackPressed();
			}
		});
		
		mRootView.findViewById(R.id.btn_frag_change_pwd_content_cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				MainActivity.instance.onBackPressed();
			}
		});
		
		mRootView.findViewById(R.id.btn_frag_change_pwd_content_save).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				final String strCurPwd = ((EditText)mRootView.findViewById(R.id.edt_frag_change_pwd_content_current_pwd)).getText().toString();
				final String strNewPwd = ((EditText)mRootView.findViewById(R.id.edt_frag_change_pwd_content_new_pwd)).getText().toString();
				final String strConfirmPwd = ((EditText)mRootView.findViewById(R.id.edt_frag_change_pwd_content_confirm_pwd)).getText().toString();
				if (strCurPwd.isEmpty() || strNewPwd.isEmpty() || strConfirmPwd.isEmpty()){
					displayOKAlertDialog(getString(R.string.alert_dlg_change_pwd_blank_pwd), null);
					return;
				}
				if (strNewPwd.compareTo(strConfirmPwd) != 0){
					displayOKAlertDialog(getString(R.string.alert_dlg_change_pwd_not_equal_pwd), null);
					return;
				}
				
				JSONObject params = new JSONObject();
				try {
					params.put(Define.CHANGE_PWD_CUR, ((EditText)mRootView.findViewById(R.id.edt_frag_change_pwd_content_current_pwd)).getText().toString());
					params.put(Define.CHANGE_PWD_NEW, ((EditText)mRootView.findViewById(R.id.edt_frag_change_pwd_content_new_pwd)).getText().toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
 				final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_updating_pwd));
				new CommunicationAPIManager(MainActivity.instance).sendUpdatePwdRequest(params, new NetAPICallBack(){

					@Override
					public void succeed(final JSONObject responseObj) {
						MainActivity.instance.runOnUiThread(new Runnable(){
							@Override
							public void run(){
								try{
									if (progressDlg != null) progressDlg.dismiss();
									if (responseObj == null){
										displayOKAlertDialog(R.string.alert_dlg_change_pwd_success, new AlertDlgCallback() {
																										@Override
																										public void onOK() {
																											MainActivity.instance.onBackPressed();
																										}
																										
																										@Override
																										public void onCancel() {
																											
																										}
																									});
									}else{
										if (responseObj.has(Define.ERRORMESSAGE_TAG)){
											displayOKAlertDialog(responseObj.optString(Define.ERRORMESSAGE_TAG), null);
										}else{
											displayOKAlertDialog(R.string.alert_dlg_change_pwd_error, null);
										}
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
										displayOKAlertDialog(errorObj.optString(Define.ERRORMESSAGE_TAG), null);
									}else{
										displayOKAlertDialog(R.string.alert_dlg_change_pwd_error, null);
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
											displayOKAlertDialog(R.string.alert_dlg_change_pwd_success, null);
										}else if (error.getMessage() == null){
											displayOKAlertDialog(R.string.alert_dlg_change_pwd_error, null);
										}else{
											displayOKAlertDialog(error.getMessage(), null);
										}
									}else{
										displayOKAlertDialog(R.string.alert_dlg_change_pwd_error, null);
									}
								}catch (Exception e){
									e.printStackTrace();
								}
							}
						});
					}
				});
			}
		});
	}
}
