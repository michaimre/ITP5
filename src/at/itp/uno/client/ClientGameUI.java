package at.itp.uno.client;

import at.itp.uno.data.Card;
import at.itp.uno.data.ClientPlayer;

public interface ClientGameUI extends ClientUI{

	public void receivedCard(Card card);
	public void receivedTopCard(Card card);
	public void startTurn(boolean ownTurn);
	public void doAction();
	public void playCard(Card card);
	public void drawCard();
	public void callUno();
	
}
