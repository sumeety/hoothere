package com.sumeet.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionDetector {

	private Context _context;
	public ConnectionDetector(Context context){
		this._context = context;
	}
	public boolean isConnectingToInternet(){
		ConnectivityManager con = (ConnectivityManager)_context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(con !=null){
			NetworkInfo[] info = con.getAllNetworkInfo();
			if(info != null){
				for(int i = 0;i<info.length;i++){
					if(info[i].getState()==NetworkInfo.State.CONNECTED){
						return true;
					}
				}				
			}			
		}
		return false;
	}
}
