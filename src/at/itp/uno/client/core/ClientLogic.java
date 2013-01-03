package at.itp.uno.client.core;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.LinkedList;

import at.itp.uno.client.ClientGameUI;
import at.itp.uno.client.ClientLobbyUI;
import at.itp.uno.client.ClientUI;
import at.itp.uno.data.Card;
import at.itp.uno.data.ClientPlayer;
import at.itp.uno.data.HandCards;
import at.itp.uno.data.Player;
import at.itp.uno.network.UnoSocketWrapper;
import at.itp.uno.network.protocol.ProtocolMessages;
import at.itp.uno.server.core.UnoServer;

public class ClientLogic extends PlayerActionHandler implements Runnable, Serializable{

	private static final long serialVersionUID = 7853653519504543217L;

	private static final long ACTIONSLEEPTIME = 60*1000;
	
	private static ClientLogic INSTANCE;

	private String host;
	private int port;
	private boolean gameStarted, myTurn, validPlay, listening;
	private Thread logicThread;
	private UnoServer unoServer; 
	private ClientLobbyAdmin clientLobbyAdmin;

	private ClientPlayer self;
	private HandCards hand;
	private Card topCard;
	private LinkedList<ClientPlayer> otherPlayers;

	private ClientLobbyUI clientLobbyUI;
	private ClientGameUI clientGameUI;
	private ClientUI clientUI;
	
	public static ClientLogic getInstance(){
		if(INSTANCE == null){
			INSTANCE = new ClientLogic();
		}
		return INSTANCE;
	}
	
	public static ClientLogic getDummyLogic(){
		return new ClientLogic();
	}

	protected ClientLogic(){
		super();
		this.host = "";
		this.port = 0;
		otherPlayers = new LinkedList<ClientPlayer>();
		hand = new HandCards();
		myTurn = Boolean.FALSE;
		gameStarted = Boolean.FALSE;
		validPlay = Boolean.FALSE;
		topCard = new Card((short)0);
		logicThread = new Thread(this);
	}

	public ClientGameUI getClientGameUI() {
		return clientGameUI;
	}

	public void setClientGameUI(ClientGameUI clientGameUI) {
		this.clientGameUI = clientGameUI;
		this.clientUI = this.clientGameUI;
	}

	public ClientLobbyUI getClientLobbyUI() {
		return clientLobbyUI;
	}

	public void setClientLobbyUI(ClientLobbyUI clientLobbyUI) {
		this.clientLobbyUI = clientLobbyUI;
		this.clientUI = this.clientLobbyUI;
	}

	@SuppressWarnings("unchecked")
	public LinkedList<ClientPlayer> getOtherPlayers() {
		return (LinkedList<ClientPlayer>)otherPlayers.clone();
	}

	public ClientPlayer getSelf() {
		return self;
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
		listening = Boolean.TRUE;
		try {
			//TODO timeout set to 0 while in lobby
			self = new ClientPlayer(new UnoSocketWrapper(new Socket(host, port), 0), playername);
			clientLobbyUI.playerJoined(self);
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
					
				case ProtocolMessages.GM_PLAYERDROPPED:
					playerDropped(self.getSocket().read());
					break;

				case ProtocolMessages.LM_START:
					clientUI.showDebug("Game starting");
					gameStarted = Boolean.TRUE;
					listening = Boolean.FALSE;
					clientLobbyUI.gameStarting();
					break;
					
				case ProtocolMessages.GM_GAMECLOSING:
					clientLobbyUI.gameClosing();
					self.getSocket().close();
					listening = false;
					gameStarted = false;
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
		while(clientGameUI == null){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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
		clientLobbyUI.playerJoined(aPlayer);
		clientUI.showDebug("Player joined: "+aPlayer.getId()+", "+aPlayer.getName());
	}

	private void playerDropped(int playerid) throws IOException{
		for(ClientPlayer p:otherPlayers){
			if(p.getId() == playerid){
				clientLobbyUI.playerDropped(p);
				clientUI.showDebug("Player dropped: "+p.getId()+", "+p.getName());
				otherPlayers.remove(p);
				break;
			}
		}
	}

	private void receiveCard() throws IOException {
		Card card = new Card((short) self.getSocket().read());
		clientUI.showDebug("Receive card: "+card.toString());
		clientGameUI.receivedCard(card);
		hand.addCard(card);
	}

	private void receiveTopCard() throws IOException {
		Card card = new Card((short) self.getSocket().read());
		clientUI.showDebug("Receive top card: "+card.toString());
		clientGameUI.receivedTopCard(card);
		topCard = card;
	}

	private void startTurn() throws IOException {
		int nextPlayerID = self.getSocket().read();
		clientUI.showDebug("Start turn, id: "+nextPlayerID);
		if(nextPlayerID == self.getId()){
			clientUI.showDebug("My turn");
			clientGameUI.startTurn(Boolean.TRUE, nextPlayerID);
			doAction();
		}
		else{
			clientUI.showDebug("Not my turn");
			clientGameUI.startTurn(Boolean.FALSE, nextPlayerID);
		}
	}

	private void doAction() throws IOException{
		clientUI.showDebug("Do action");
		validPlay = false;
		do{
			clientGameUI.doAction();
			clientUI.showDebug("Going to sleep while waiting for user input");
			try {
				Thread.sleep(ACTIONSLEEPTIME);
			} catch (InterruptedException e) {
				//intended -> player did something
				clientUI.showDebug("bzzzt interrupt");
			}
		}while(!validPlay);
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
	public boolean playCard(Card card) throws IOException {
		clientUI.showDebug("Playing card: "+card.toString());
		clientGameUI.playCard(card);
		self.getSocket().write(ProtocolMessages.GTM_PLAYCARD);
		self.getSocket().write(card.getFace());
		validPlay = (self.getSocket().read() == ProtocolMessages.GTM_VALIDPLAY);
		logicThread.interrupt();
		return validPlay;
	}

	@Override
	public void drawCard() throws IOException {
		clientUI.showDebug("Drawing card");
		clientGameUI.drawCard();
		self.getSocket().write(ProtocolMessages.GTM_DRAWCARD);
		validPlay = true;
		logicThread.interrupt();
	}

	@Override
	public void callUno() throws IOException {
		clientUI.showDebug("Calling uno");
		clientGameUI.callUno();
		self.getSocket().write(ProtocolMessages.GTM_CALLUNO);
		validPlay = true;
		logicThread.interrupt();
	}

	@Override
	public void accusePlayer(Player player) throws IOException {
		// TODO Auto-generated method stub

	}
}
