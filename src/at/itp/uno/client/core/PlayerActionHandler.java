package at.itp.uno.client.core;

import java.io.IOException;

import at.itp.uno.client.ClientUI;
import at.itp.uno.data.Card;
import at.itp.uno.data.Player;

public abstract class PlayerActionHandler {
	
	protected ClientUI clientUI;
	
	public PlayerActionHandler(ClientUI clientUI){
		this.clientUI=clientUI;
	}
	
	//Lobby actions
	public abstract boolean joinGame(String host, int port);
	public abstract boolean openNewGameLobby();
	public abstract void startGame();
	
	//Game actions
	public abstract void playCard(Card card) throws IOException;
	public abstract void drawCard() throws IOException;
	public abstract void callUno() throws IOException;
	public abstract void accusePlayer(Player player) throws IOException;

}
