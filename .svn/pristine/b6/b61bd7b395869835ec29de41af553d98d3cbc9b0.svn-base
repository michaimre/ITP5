package at.itp.uno.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.util.Log;
import at.itp.uno.activity.Activity_WifiClient;


public class Wifi_Broadcastreceiver extends BroadcastReceiver {

	private WifiManager wifi_m;
	private Activity_WifiClient wifi_activity;
	private boolean isWifiEnabled;
	
	//Constructor
	public Wifi_Broadcastreceiver (WifiManager wifi_m , Activity_WifiClient wifi_activity){
		super();
		this.wifi_m = wifi_m;
		this.wifi_activity = wifi_activity; 
	}
	
	public boolean isWifiEnabled(){	
		return isWifiEnabled;
	}

	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		if(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION.equals(action)){
			Log.v("Wifi BroadcastReceiver -->","SUPPLICANT_CONNECTION_CHANGE");
			boolean state = intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED,false);
			if(state == true){
				Log.v("BroadcastReceiver -->", "Connection Available");
			}
			else{
				Log.v("BroadcastReceiver -->", "No Connection Available");	
			}
		}
		else if(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)){
			Log.v("Wifi BroadcastReceiver -->","SUPPLICANT_STATE_CHANGED");
			SupplicantState state = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
			if(state.compareTo(SupplicantState.COMPLETED) == 0){
				Log.v("BroadcastReceiver -->", "SupplicantStateCompleted");
			/*	for(int i=0; i <900;i++){ Log.v("s","sd"+i);}
				
				Log.d("WifiPreference", "enableNetwork returned " + wifi_m.getConnectionInfo().toString());
				Log.d("WifiPreference", "enableNetwork returned " + wifi_m.getDhcpInfo());
				Log.d("WifiPreference", "enableNetwork returned " + wifi_m.pingSupplicant());
				ConnectivityManager cm;
				cm = (ConnectivityManager) wifi_activity.getSystemService(Context.CONNECTIVITY_SERVICE);
				boolean x = cm.requestRouteToHost(1, wifi_m.getDhcpInfo().gateway);
				Log.d("Connectivity RoutToHost", "Return" + x  );
				Client_AsyncTask task = new Client_AsyncTask(context);
				task.execute();*/
				
			}
		}
		 
		
	}

}
