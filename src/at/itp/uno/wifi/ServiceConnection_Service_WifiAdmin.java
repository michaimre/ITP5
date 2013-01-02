package at.itp.uno.wifi;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import at.itp.uno.activity.Activity_Lobby;
import at.itp.uno.activity.Activity_ServerGame;
import at.itp.uno.wifi.Service_WifiAdmin.Binder_Service_WifiAdmin;

public class ServiceConnection_Service_WifiAdmin implements ServiceConnection {
	private Activity _activity;
	
	public ServiceConnection_Service_WifiAdmin(Activity a){
		_activity = a;
		
	}
	
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i("Activity","onServiceConnected");
			if(service instanceof Binder_Service_WifiAdmin && _activity instanceof Activity_Lobby){
				Log.i("Activity","onServiceConnected --> it is");
				//((Activity_ServerGame) _activity).setIBinder((Binder_Service_WifiAdmin) service);
				((Activity_Lobby) _activity).setIBinder((Binder_Service_WifiAdmin) service);
			}
	}

	@Override
	public void onServiceDisconnected(ComponentName arg0) {
		// TODO Auto-generated method stub
		
	}

}
