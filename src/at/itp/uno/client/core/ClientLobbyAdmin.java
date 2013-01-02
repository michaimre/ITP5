package at.itp.uno.client.core;

import java.io.IOException;
import java.net.Socket;

import at.itp.uno.client.ClientUI;
import at.itp.uno.network.UnoSocketWrapper;
import at.itp.uno.network.protocol.ProtocolMessages;

public class ClientLobbyAdmin extends Thread{

	private ClientUI clientUI;
	private String host;
	private int port;
	private UnoSocketWrapper socket;
	private int message;

	public ClientLobbyAdmin(ClientUI clientUI){
		this.clientUI = clientUI;
		this.host = "";
		this.port = 0;
		this.message = 0;
	}
 
	public void connectTo(String host, int port) throws IOException{
		if(!isConnected()){
			this.host = host;
			this.port = port;
			clientUI.showDebug("Connecting to: "+host+":"+port);
			socket = new UnoSocketWrapper(new Socket(host, port));
			clientUI.showDebug("Admin socket connected");
		}
		else{
			throw new IOException("Socket already connected");
		}
	}

	public boolean isConnected(){
		return (socket!=null)?!socket.isClosed():false;
	}
	
	public void sendStartCommand(){
		if(!this.isAlive()){
			this.message = ProtocolMessages.LAM_STARTGAME;
			this.start();
		}
	}

	@Override
	public void run() {
		clientUI.showDebug("Client Lobby Admin started with message: "+ProtocolMessages.getMessageString(this.message));
		try {
			socket.write(message);
		} catch (IOException e) {
			e.printStackTrace();
			clientUI.showError(e.getMessage());
		}
	}
}
