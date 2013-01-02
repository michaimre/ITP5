package at.itp.uno.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import at.itp.uno.wifi.ServiceConnection_Service_WifiAdmin;
import at.itp.uno.wifi.Service_WifiAdmin;
import at.itp.uno.wifi.Service_WifiAdmin.Binder_Service_WifiAdmin;
import at.itp_uno_wifi_provider.R;

public class Activity_WifiServer extends Activity implements Button.OnClickListener{

	private Button Localhost;
	private TextView Text;	
	private WifiManager wifi_m;
	private Intent i = null;
	private Binder_Service_WifiAdmin _service = null;
	private ServiceConnection_Service_WifiAdmin connection = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifiserver);
		Localhost = (Button)findViewById(R.id.b_Localhost);
		Localhost.setOnClickListener(this);
		//OpenSocket.setOnClickListener(this);
		Text = (TextView)findViewById(R.id.textView1);
		//	    i = new Intent(this, Service_WifiAdmin.class);
		//	    startService(i);
		// TODO Auto-generated method stub
	}



	public void onClick(View v) {
		// TODO Auto-generated method stub

		Log.i("There","LocalhostConnection");
		Intent i = new Intent(this,Activity_ServerGame.class);
		startActivity(i);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		bindService(new Intent(this, Service_WifiAdmin.class), connection = new ServiceConnection_Service_WifiAdmin(this),BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(connection != null){
			unbindService(connection);
			connection = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.v("Service Will Stop","Service Will Stop");
		stopService(i);
	}
	
	public void setIBinder(Binder_Service_WifiAdmin binder){
		_service = binder;
		
	}

}
