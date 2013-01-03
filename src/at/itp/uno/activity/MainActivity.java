package at.itp.uno.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import at.itp.uno.client.core.ClientLogic;
import at.itp.uno.server.core.ServerLogic;
import at.itp.uno.wifi.ServiceConnection_Service_WifiAdmin;
import at.itp.uno.wifi.Service_WifiAdmin;
import at.itp.uno.wifi.Service_WifiAdmin.Binder_Service_WifiAdmin;
import at.itp_uno_wifi_provider.R;

public class MainActivity extends Activity implements Button.OnClickListener, ServiceBindable {

	private Button b_startGame;
	private Button b_joinGame;
	private Binder_Service_WifiAdmin _service = null;
	private ServiceConnection_Service_WifiAdmin connection = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		b_startGame = (Button)findViewById(R.id.b_startGame);
		b_startGame.setOnClickListener(this);
		b_joinGame = (Button)findViewById(R.id.b_joinGame);
		b_joinGame.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("UNO Main", "onresume");
		ClientLogic.resetLogic();
		if(ServerLogic.RUNNING){
			if(bindService(new Intent(this, Service_WifiAdmin.class), connection = new ServiceConnection_Service_WifiAdmin(this),BIND_AUTO_CREATE)){
				if(_service != null) _service.stopLogic();
				if(connection != null){
					unbindService(connection);
					connection = null;
				}
				Intent i = new Intent(this, Service_WifiAdmin.class);
				stopService(i);
			}
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d("UNO Main", "onrestart");
	}

	@Override
	public void onClick(View v) {
		Log.v("Here","Here");
		Intent intent;
		if(v.equals(b_startGame)){
			Log.v("MainActivity","StartGame");
			Intent i = new Intent(this, Service_WifiAdmin.class);
			//stopService(i);
			startService(i);

			intent = new Intent(this, Activity_Lobby.class);
			intent.putExtra("textViewResourceId", android.R.layout.simple_list_item_multiple_choice);
		}
		else{
			Log.v("MainActivity","JoinGame");

			intent = new Intent(this, Activity_WifiClient.class);
			//			intent.putExtra("textViewResourceId", android.R.layout.simple_list_item_1);
		}
		//		startActivityForResult(intent, 0);
		//		MainActivity.this.finish();
		startActivity(intent);


		/*
		if(v.equals(b_startGame)){
			Intent i = new Intent(this, Service_WifiAdmin.class);
			startService(i);
			i = new Intent(this, Activity_Lobby.class);
			startActivity(i);
		}
		else{
			Log.v("There","There");
			Intent i = new Intent(this, Activity_WifiClient.class);
			startActivity(i); 

		}*/

	}

	@Override
	public void setIBinder(Binder_Service_WifiAdmin binder){
		Log.d("UNO main", "setIBinder");
		_service = binder;

	}
}
