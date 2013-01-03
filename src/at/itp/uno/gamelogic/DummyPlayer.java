package at.itp.uno.gamelogic;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

import android.util.Log;
import at.itp.uno.client.ClientGameUI;
import at.itp.uno.client.ClientLobbyUI;
import at.itp.uno.client.core.ClientLogic;
import at.itp.uno.data.Card;
import at.itp.uno.data.ClientPlayer;

public class DummyPlayer extends Thread implements ClientLobbyUI, ClientGameUI{

	private static int ID = 0;
	
	private int port;
	private Socket socket;
	private ClientLogic clientLogic;
	private LinkedList<Card> cards;
	private int myid = ID++;
	
	public DummyPlayer(int port){
		cards = new LinkedList<Card>();
		clientLogic = ClientLogic.getDummyLogic();
		clientLogic.setClientLobbyUI(this);
		clientLogic.joinGame("localhost", port);
	}
	
	@Override
	public void run() {
		
	}


	@Override
	public void showMessage(String message) {
		Log.d("UNO Dummy"+myid, message);
	}

	@Override
	public void showDebug(String message) {
		// TODO Auto-generated method stub
		Log.d("UNO Dummy"+myid, message);
	}

	@Override
	public void showError(String error) {
		// TODO Auto-generated method stub
		Log.d("UNO Dummy"+myid, error);
	}

	@Override
	public void receivedCard(Card card) {
		// TODO Auto-generated method stub
		Log.d("UNO Dummy"+myid, ""+card.getFace());	
		cards.add(card);
	}

	@Override
	public void receivedTopCard(Card card) {
		// TODO Auto-generated method stub
		Log.d("UNO Dummy"+myid, ""+card.getFace());
	}

	@Override
	public void startTurn(boolean ownTurn, int playerId) {
		// TODO Auto-generated method stub
		Log.d("UNO Dummy"+myid, ""+ownTurn+", "+playerId);
	}

	@Override
	public void doAction() {
		// TODO Auto-generated method stub
		Log.d("UNO Dummy"+myid, "doaction");
		try {
			for(Card c:cards){
				if(clientLogic.playCard(c))
					return;
			}
			if(cards.size()==1)
				clientLogic.callUno();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("UNO Dummy"+myid, e.getMessage());
		}
		try {
			clientLogic.drawCard();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void playCard(Card card) {
		// TODO Auto-generated method stub
		Log.d("UNO Dummy"+myid, ""+card.getFace());
	}

	@Override
	public void drawCard() {
		// TODO Auto-generated method stub
		Log.d("UNO Dummy"+myid, "drawcard");
	}

	@Override
	public void callUno() {
		// TODO Auto-generated method stub
		Log.d("UNO Dummy"+myid, "calluno");
	}

	@Override
	public void playerJoined(ClientPlayer aPlayer) {
		// TODO Auto-generated method stub
		Log.d("UNO Dummy"+myid, aPlayer.getName());
	}

	@Override
	public void gameStarting() {
		Log.d("UNO Dummy"+myid, "gamestarting");
		clientLogic.setClientGameUI(this);
	}

	@Override
	public void playerDropped(ClientPlayer aPlayer) {
		Log.d("UNO Dummy"+myid, "dropped"+aPlayer.toString());
	}

	@Override
	public void gameClosing() {
		Log.d("UNO Dummy"+myid, "gameclosing");
	}
	
}
