package at.itp.uno.client;

import at.itp.uno.data.Card;
import at.itp.uno.data.ClientPlayer;

public interface ClientUI {
	
	public void showMessage(String message);
	public void showDebug(String message);
	public void showError(String error);

}
