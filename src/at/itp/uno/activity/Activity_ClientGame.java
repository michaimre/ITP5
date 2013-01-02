package at.itp_uno_wifi_provider.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import at.itp_uno_wifi_provider.Client_AsyncTask;
import at.itp_uno_wifi_provider.R;
import at.itp_uno_wifi_provider.R.id;
import at.itp_uno_wifi_provider.R.layout;

public class Activity_ClientGame extends Activity {

	private EditText _spielerName;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    this.setContentView(R.layout.activity_clientgame);
	    _spielerName = (EditText)findViewById(R.id.et_SpielerName);
	    Client_AsyncTask task = new Client_AsyncTask(this,this,_spielerName);
		task.execute();
	}

}
