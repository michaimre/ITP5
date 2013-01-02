package at.itp.uno.server.ui;

import android.util.Log;
import at.itp.uno.server.ServerUI;

public class AndroidLogUI implements ServerUI {

	@Override
	public void showMessage(String message) {
		Log.d("UNO server", message);
	}

	@Override
	public void showError(String error) {
		Log.e("UNO server", error);
	}

}
