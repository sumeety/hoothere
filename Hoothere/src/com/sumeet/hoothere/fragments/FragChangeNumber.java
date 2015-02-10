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
import com.sumeet.global.Global;
import com.sumeet.hoothere.AlertDlgCallback;
import com.sumeet.hoothere.MainActivity;
import com.sumeet.hoothere.R;
import com.sumeet.model.UserInformation;
import com.sumeet.util.AlertDialogManager;

public class FragChangeNumber extends Fragment{
	
	private View mRootView;
	private EditText mEdPhoneNumber;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) parent.removeView(mRootView);
		}
		try {
			mRootView = inflater.inflate(R.layout.fragment_change_number, container, false);
			initUI();
			eventHandler();
		}catch(InflateException e){
			e.printStackTrace();
		}
		return mRootView;
	}
	
	private void initUI(){
		mEdPhoneNumber = (EditText)mRootView.findViewById(R.id.edt_frag_change_number);
		mEdPhoneNumber.setText("");
	}
	
	@Override
	public void onResume(){
		super.onResume();
		MainActivity.instance.setFooterVisibility(View.GONE);
	}
	
	private void eventHandler(){
		mRootView.findViewById(R.id.iv_frag_change_number_header_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager)MainActivity.instance.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mEdPhoneNumber.getWindowToken(), 0);
				MainActivity.instance.onBackPressed();
			}
		});
		
		mRootView.findViewById(R.id.btn_frag_change_number_submit).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialogManager.sharedManager(MainActivity.instance).showConfirmDialog(getString(R.string.alert_dlg_change_number_title), 
																						String.format(Define.ALERT_DLG_CONFIRM_NUMBER, mEdPhoneNumber.getText().toString()), 
																						getString(R.string.alert_dlg_btn_ok), 
																						getString(R.string.alert_dlg_cancel), 
																						new AlertDlgCallback() {
																							@Override
																							public void onOK() {
																								changeNumber();
																							}
																							
																							@Override
																							public void onCancel() {
																								
																							}
																						});
			}
		});
	}
	
	private void changeNumber(){
		final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_sending_request));
		JSONObject params = new JSONObject();
		try {
			params.put(Define.USERINFO_MOBILE, mEdPhoneNumber.getText().toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		new CommunicationAPIManager(MainActivity.instance).sendChangeNumberRequest(params, new NetAPICallBack(){
			@Override
			public void succeed(final JSONObject responseObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (progressDlg != null) progressDlg.dismiss();
							Global.GUser = new UserInformation(responseObj);
							if (responseObj != null){
								if (responseObj.has(Define.ERRORMESSAGE_TAG)){
									try {
										displayOKAlertDialog(responseObj.getString(Define.ERROR_TAG));
									} catch (JSONException e) {
										displayOKAlertDialog(R.string.alert_dlg_change_number_error);
										e.printStackTrace();
									}
								}else if (responseObj.has(Define.USERINFO_ID)){
									displayOKAlertDialog(R.string.alert_dlg_change_number_success);
									Global.GUser = new UserInformation(responseObj);
									MainActivity.instance.onBackPressed();
								}else{
									try {
										displayOKAlertDialog(responseObj.getString(Define.ERROR_TAG));
									} catch (JSONException e) {
										displayOKAlertDialog(R.string.alert_dlg_change_number_error);
										e.printStackTrace();
									}
								}
							}else{
								displayOKAlertDialog(R.string.alert_dlg_change_number_error);
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
							displayOKAlertDialog(R.string.alert_dlg_change_number_error);
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
							displayOKAlertDialog(R.string.alert_dlg_change_number_error);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
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
}
