package at.itp.uno.client.core;

import java.io.IOException;

import at.itp.uno.client.ClientUI;
import at.itp.uno.server.core.UnoServer;

/**
 * @deprecated
 * 
 * @author Akwyp
 *
 */
public class UnoClient {

	private ClientUI clientUI;
	private ClientLobbyAdmin clientLobbyAdmin;
	private ClientLogic clientLogic;
	
	public UnoClient(ClientUI clientUI){
		this.clientUI = clientUI;
		this.clientLobbyAdmin = new ClientLobbyAdmin(clientUI);
		this.clientLogic = new ClientLogic(clientUI);
	}

	public ClientLogic getClientLogic() {
		return clientLogic;
	}

	public boolean openNewGameLobby() {
		int port = 26000;
		new UnoServer(port);
		try {
			clientLobbyAdmin.connectTo("localhost", port);
			//clientLogic.connectTo("localhost", port);
		} catch (IOException e) {
			e.printStackTrace();
			clientUI.showError(e.getMessage());
			return false;
		}
		return true;
	}

	public boolean joinGame(String host, int port) {
		/*
		try {
			clientLogic.connectTo(host, port);
		} catch (IOException e) {
			e.printStackTrace();
			clientUI.showError(e.getMessage());
			return false;
		}
		*/
		return true;
	}
	
	public void startGame(){
		clientLobbyAdmin.sendStartCommand();
	}

}
