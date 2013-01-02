package at.itp.uno.client.core;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

import at.itp.uno.client.ClientUI;
import at.itp.uno.data.Card;
import at.itp.uno.data.ClientPlayer;
import at.itp.uno.data.HandCards;
import at.itp.uno.data.Player;
import at.itp.uno.network.UnoSocketWrapper;
import at.itp.uno.network.protocol.ProtocolMessages;
import at.itp.uno.server.core.UnoServer;

public class ClientLogic extends PlayerActionHandler implements Runnable{

	private static final long ACTIONSLEEPTIME = 60*1000;

	private String host;
	private int port;
	private boolean gameStarted;
	private Thread logicThread;
	private UnoServer unoServer; 
	private ClientLobbyAdmin clientLobbyAdmin;

	private ClientPlayer self;
	private HandCards hand;
	private Card topCard;
	private LinkedList<ClientPlayer> otherPlayers;

	public ClientLogic(ClientUI clientUI){
		super(clientUI);
		this.host = "";
		this.port = 0;
		otherPlayers = new LinkedList<ClientPlayer>();
		hand = new HandCards();
		gameStarted = Boolean.FALSE;
		topCard = new Card((short)0);
		logicThread = new Thread(this);
	}

	@Override
	public boolean joinGame(String host, int port){
		this.host=host;
		this.port=port;
		logicThread.start();
		return true;
	}
	
	@Override
	public boolean openNewGameLobby(){
		//TODO server port is here!
		int port = 26000;
		unoServer = new UnoServer(port);
		try {
			clientLobbyAdmin = new ClientLobbyAdmin(clientUI);
			clientLobbyAdmin.connectTo("localhost", port);
			joinGame("localhost", port);
		} catch (IOException e) {
			e.printStackTrace();
			clientUI.showError(e.getMessage());
			return false;
		}
		return true;
	}
	
	@Override
	public void startGame(){
		clientLobbyAdmin.sendStartCommand();
	}

	@Override
	public void run() {
		clientUI.showDebug("Client logic started");

		preGamePreparation();

		lobbyLoop();

		if(gameStarted){
			gameLoop();
		}
	}

	private void preGamePreparation(){
		otherPlayers.clear();
	}

	private void lobbyLoop(){
		clientUI.showDebug("Lobby loop start");
		//TODO playername here!
		String playername = "Player";
		boolean listening = Boolean.TRUE;
		try {
			//TODO timeout set to 0 while in lobby
			self = new ClientPlayer(new UnoSocketWrapper(new Socket(host, port), 0), playername);
			clientUI.playerJoined(self);
			clientUI.showDebug("Self is: "+self.getId()+" ,"+self.getName());
			while(listening){
				clientUI.showDebug("Listening...");
				int action = self.getSocket().read();
				switch(action){
				case ProtocolMessages.LM_STARTOFPLAYERLIST:
					clientUI.showDebug("Receiving player list");
					int playerid;
					while((playerid=self.getSocket().read())!=ProtocolMessages.LM_ENDOFPLAYERLIST){
						playerJoined(playerid);
					}
					clientUI.showDebug("End of player list");
					break;

				case ProtocolMessages.LM_PLAYERJOINED:
					playerJoined(self.getSocket().read());
					break;

				case ProtocolMessages.LM_START:
					clientUI.showDebug("Game starting");
					gameStarted = Boolean.TRUE;
					listening = Boolean.FALSE;
					clientUI.gameStarting();
					break;

				default:
					clientUI.showDebug("default: "+ProtocolMessages.getMessageString(action));

				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			clientUI.showError(e.getMessage());
		}
	}

	private void gameLoop() {
		clientUI.showDebug("Game loop start");
		try {
			while(true){
				clientUI.showDebug("Listening...");
				int action = self.getSocket().read();
				switch(action){
				case ProtocolMessages.GTM_DEALCARD:
					receiveCard();
					break;

				case ProtocolMessages.GTM_TOPCARD:
					receiveTopCard();
					break;

				case ProtocolMessages.GTM_STARTTURN:
					startTurn();
					break;

				default:
					clientUI.showDebug("default: "+ProtocolMessages.getMessageString(action));

				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			clientUI.showError(e.getMessage());
		}
	}

	private void playerJoined(int playerid) throws IOException{
		ClientPlayer aPlayer = new ClientPlayer(playerid);
		aPlayer.setName(self.getSocket().readString());
		otherPlayers.add(aPlayer);
		clientUI.playerJoined(aPlayer);
		clientUI.showDebug("Player joined: "+aPlayer.getId()+", "+aPlayer.getName());
	}

	private void receiveCard() throws IOException {
		Card card = new Card((short) self.getSocket().read());
		clientUI.showDebug("Receive card: "+card.toString());
		clientUI.receivedCard(card);
		hand.addCard(card);
	}

	private void receiveTopCard() throws IOException {
		Card card = new Card((short) self.getSocket().read());
		clientUI.showDebug("Receive top card: "+card.toString());
		clientUI.receivedTopCard(card);
		topCard = card;
	}

	private void startTurn() throws IOException {
		int nextPlayerID = self.getSocket().read();
		clientUI.showDebug("Start turn, id: "+nextPlayerID);
		if(nextPlayerID == self.getId()){
			clientUI.showDebug("My turn");
			clientUI.startTurn(Boolean.TRUE);
			doAction();
		}
		else{
			clientUI.showDebug("Not my turn");
			clientUI.startTurn(Boolean.FALSE);
		}
	}

	private void doAction() throws IOException{
		clientUI.showDebug("Do action");
		clientUI.doAction();
		clientUI.showDebug("Going to sleep while waiting for user input");
		try {
			Thread.sleep(ACTIONSLEEPTIME);
		} catch (InterruptedException e) {
			//intended -> player did something
			clientUI.showDebug("bzzzt interrupt");
		}
		//skip player - end turn
		
		
		/*
		 * old code -> moved to interface methods
		 * 
		clientUI.showDebug("Action is: "+ProtocolMessages.getMessageString(playerAction));
		switch(playerAction){
		case ProtocolMessages.GTM_PLAYCARD:
			playCard(cardToPlay);
			break;

		case ProtocolMessages.GTM_DRAWCARD:
			drawCard();
			break;

		case ProtocolMessages.GTM_CALLUNO:
			callUno();
			break;

		case ProtocolMessages.GTM_ACCUSE:
			break;

		default:
			//end turn
			break;
		}
		*/
	}

	/*
	private void playCard(Card card) throws IOException {
		clientUI.showDebug("Playing card: "+card.toString());
		clientUI.playCard(card);
		self.getSocket().write(ProtocolMessages.GTM_PLAYCARD);
		self.getSocket().write(card.getFace());
	}

	private void drawCard() throws IOException {
		clientUI.showDebug("Drawing card");
		clientUI.drawCard();
		self.getSocket().write(ProtocolMessages.GTM_DRAWCARD);
	}

	private void callUno() throws IOException {
		clientUI.showDebug("Calling uno");
		clientUI.callUno();
		self.getSocket().write(ProtocolMessages.GTM_CALLUNO);
	}
	*/

	@Override
	public void playCard(Card card) throws IOException {
		clientUI.showDebug("Playing card: "+card.toString());
		clientUI.playCard(card);
		self.getSocket().write(ProtocolMessages.GTM_PLAYCARD);
		self.getSocket().write(card.getFace());
		logicThread.interrupt();
	}

	@Override
	public void drawCard() throws IOException {
		clientUI.showDebug("Drawing card");
		clientUI.drawCard();
		self.getSocket().write(ProtocolMessages.GTM_DRAWCARD);
		logicThread.interrupt();
	}

	@Override
	public void callUno() throws IOException {
		clientUI.showDebug("Drawing card");
		clientUI.drawCard();
		self.getSocket().write(ProtocolMessages.GTM_DRAWCARD);
		logicThread.interrupt();
	}

	@Override
	public void accusePlayer(Player player) throws IOException {
		// TODO Auto-generated method stub

	}

}
