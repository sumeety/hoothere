package com.sumeet.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;

import com.sumeet.callback.AlertDialogListSelectionInterface;
import com.sumeet.hoothere.AlertDlgCallback;


public class AlertDialogManager {

	private static AlertDialogManager mInstance;
	private Context mContext;
	
	private AlertDialogManager (Context context) {
		
		mContext = context;
	}
	
	public static AlertDialogManager sharedManager (Context context) {
		
		if (mInstance == null) {
			
			mInstance = new AlertDialogManager(context);
		}
		
		return mInstance;
	}
	
	//----------------- show success and failer alert -------------------------//
    public void showSuccesAlertDialog(String strMessage) {
    	
		// show alert dialog
		AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
		alert.setCancelable(true);
		alert.setTitle("Information!");
		alert.setMessage(strMessage);
		alert.show();
    }
    
    public void showErrorAlertDialog(String strMessage) {
    	
		// show alert dialog
		AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
		alert.setCancelable(true);
		alert.setTitle("Error!");
		alert.setMessage(strMessage);
		alert.show();
    }
    
    public void showNetworkErrorAlertDialog() {
    	
		// show alert dialog
		AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
		alert.setCancelable(true);
		alert.setTitle("Error!");
		alert.setMessage("Network Error!\nPlease check your network and try again.");
		alert.show();
    }
    
    public void showConfirmDialog(final String strTitle, final String strMessage, final String strOK, final String strCancel, final AlertDlgCallback callback){
    	final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
    	alert.setCancelable(true);
    	alert.setTitle(strTitle);
    	alert.setMessage(strMessage);
    	if (strOK != null){
	    	alert.setPositiveButton(strOK, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (callback != null)
						callback.onOK();
				}
			});
    	}
    	
    	if (strCancel != null){
	    	alert.setNegativeButton(strCancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (callback != null)
						callback.onCancel();
				}
			});
    	}
    	
    	alert.show();
    }
    
    
    //----------------- show confirm dialog ---------------------------
    public void showExitConfirmAlertDialog() {
    	
		// show alert dialog
    	AlertDialog.Builder alert_confirm = new AlertDialog.Builder(mContext);
    	alert_confirm.setTitle("Question");
    	alert_confirm.setMessage("Do you really want to exit app?").setCancelable(false);
    	alert_confirm.setPositiveButton("Sure", new DialogInterface.OnClickListener() {
    		
    		@Override
    		public void onClick(DialogInterface dialog, int which) {
    			
    			dialog.dismiss();
    			System.exit(0);
    		}
    	});
    	
    	alert_confirm.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    		
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	        
    	    	dialog.dismiss();
    	    	return;
    	    }
    	});
    	
    	AlertDialog alert = alert_confirm.create();
    	alert.show();
    }
    
    public static void showListOptionsDialog(final Context context, String title, String items[], final AlertDialogListSelectionInterface alertListInterface) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.select_dialog_item, items);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		if(null != title) builder.setTitle(title);
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) { // pick from
				if(null != alertListInterface) alertListInterface.onItemSelected(context,item);
			}
		});
		final AlertDialog dialog = builder.create();
		dialog.show();
	}
}
