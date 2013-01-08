package at.itp.uno.client;

import at.itp.uno.data.ClientPlayer;

public interface ClientLobbyUI extends ClientUI{
	
	public void playerJoined(ClientPlayer aPlayer);
	public void playerDropped(ClientPlayer aPlayer);
	public void gameStarting();
	public void gameClosing();
	
}
