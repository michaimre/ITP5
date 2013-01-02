package at.itp.uno.server.core;

import java.io.IOException;
import java.net.SocketException;

import at.itp.uno.network.UnoSocketWrapper;
import at.itp.uno.network.protocol.ProtocolMessages;

public class LobbyAdminListener implements Runnable{

	private ServerLogic serverLoop;
	private UnoSocketWrapper socket;
	private boolean listening;

	public LobbyAdminListener(ServerLogic serverLoop, UnoSocketWrapper socket){
		this.serverLoop=serverLoop;
		this.socket=socket;
		listening = true;
	}

	public void closeSocket() throws IOException{
		listening = false;
		if(!socket.isClosed()){
			socket.close();
		}
	}

	@Override
	public void run() {
		try {
			while(listening){
				try{
					int msg = socket.read();
					switch(msg){
						case ProtocolMessages.LAM_STARTGAME:
							serverLoop.startGame();
							break;
	
						case ProtocolMessages.LAM_KICKPLAYER:
							int player = socket.read();
							serverLoop.kickPlayer(player);
							break;
	
						case ProtocolMessages.LAM_CLOSELOBBY:
							serverLoop.closeLobby();
							break;	
						
						default:
							
					}
				}
				catch(SocketException se){
					listening=false;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
