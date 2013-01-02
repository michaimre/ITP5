package at.itp.uno.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import at.itp.uno.wifi.Client_AsyncTask;
import at.itp.uno.wifi.ServiceConnection_Service_WifiAdmin;
import at.itp.uno.wifi.Service_WifiAdmin;
import at.itp.uno.wifi.Service_WifiAdmin.Binder_Service_WifiAdmin;
import at.itp_uno_wifi_provider.R;

public class Activity_ServerGame extends Activity implements Button.OnClickListener {
	
	private Button b_sendBroadcast;
	private Binder_Service_WifiAdmin _service = null;
	private ServiceConnection_Service_WifiAdmin connection = null;
	private EditText et_broadcastMessage;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_servergame);
//		b_sendBroadcast = (Button)findViewById(R.id.b_sendBroadcast);
//		et_broadcastMessage = (EditText)findViewById(R.id.et_Message);
		b_sendBroadcast.setOnClickListener(this);
		Client_AsyncTask task = new Client_AsyncTask(this,this);
		task.execute();
	}
	


	@Override
	public void onClick(View v) {
		if (connection != null){
			_service.sendTestBroadcast(et_broadcastMessage.getText().toString());
		}
		
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
	
	public void setIBinder(Binder_Service_WifiAdmin binder){
		_service = binder;
		
	}

}
