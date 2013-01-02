package at.itp.uno.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import at.itp.uno.wifi.Service_WifiAdmin;
import at.itp.uno.wifi.Socket_AsyncTask;
import at.itp_uno_wifi_provider.R;

public class Activity_WifiServer extends Activity implements Button.OnClickListener{
private Button OpenSocket, Localhost;
private TextView Text;	
private WifiManager wifi_m;
private Intent i = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_wifiserver);
	    OpenSocket = (Button)findViewById(R.id.b_OpenSocket);
	    Localhost = (Button)findViewById(R.id.b_Localhost);
	    Localhost.setOnClickListener(this);
//	    OpenSocket.setOnClickListener(this);
	    Text = (TextView)findViewById(R.id.textView1);
	    i = new Intent(this, Service_WifiAdmin.class);
	    startService(i);
	}



	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if(v.equals(OpenSocket)){
			Text.setText("starting Socket");
			Socket_AsyncTask task = new Socket_AsyncTask(this, Text);
			task.execute();
			Text.setText("Socket started");
		}
		else{
			Log.i("There","LocalhostConnection");
			Intent i = new Intent(this,Activity_ServerGame.class);
			startActivity(i);
		}
		
	}
	
	@Override
		protected void onDestroy() {
			super.onDestroy();
			Log.v("Service Will Stop","Service Will Stop");
			stopService(i);
		}

}
