package at.itp.uno.server.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import android.util.Log;
import at.itp.uno.data.ServerPlayer;
import at.itp.uno.gamelogic.DummyPlayer;
import at.itp.uno.gamelogic.GameTable;
import at.itp.uno.network.SocketFactory;
import at.itp.uno.network.UnexpectedPlayerResponseEception;
import at.itp.uno.network.UnoSocketWrapper;
import at.itp.uno.network.protocol.ProtocolMessages;
import at.itp.uno.server.ServerUI;

public class ServerLogic implements Runnable{

	public static boolean RUNNING = false;
	
	private ServerUI serverUI;
	private ServerSocket serverSocket;
	private int port;
	private boolean lobbyOpen, forcestop;

	private boolean gameStarted;

	private GameTable gameTable;

	public ServerLogic(SocketFactory socketFactory, int port, ServerUI serverUI) throws IOException{
		this(socketFactory.createServerSocket(port), port, serverUI);
	}

	public ServerLogic(ServerSocket serverSocket, int port, ServerUI serverUI){
		RUNNING = true;
		this.serverUI=serverUI;
		this.port=port;
		this.serverSocket = serverSocket;

		gameStarted = false;
		lobbyOpen = false;
		forcestop = false;

		gameTable = new GameTable(serverUI);
	}

	private void closeServerSocket(){
		if(!serverSocket.isClosed()){
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		serverUI.showMessage("Starting server loop");
		//Open server, gather players
		lobbySetup();
		if(gameStarted){
			gameTable.startGame();
			//Set up table and players
			gameTable.setUpGame();
			//Do game stuff
			gameLoop();

			//Restart thingy here
		}
		else{
			serverUI.showMessage("Didn't start game");
		}
		serverUI.showMessage("Server loop stopping");
	}

	/////////////
	//GameLobby//
	/////////////

	/**
	 * Gathers connecting player in a <code>GameTable</code> and creates a <code>LobbyAdminListener</code> to listen for 
	 * lobby admin commands.
	 */
	private void lobbySetup(){
		serverUI.showMessage("Starting lobby setup");
		lobbyOpen = true;
		try {
			//serverSocket = socketFactory.createServerSocket(port);
			//			serverUI.showMessage("Waiting for admin connection");
			//			lobbyAdminListener = new LobbyAdminListener(this, new UnoSocketWrapper(serverSocket.accept(), 0));
			//			new Thread(lobbyAdminListener).start();
			//			serverUI.showMessage("Admin connected");
			int cid = 1;
			while(!serverSocket.isClosed() && !forcestop){
				try{
					//TODO timeout set to 0 while in lobby
					serverUI.showMessage("Waiting for player to join");
					UnoSocketWrapper tmpsocket = new UnoSocketWrapper(serverSocket.accept(), 0);
					synchronized (gameTable) {
						try{
							ServerPlayer newPlayer = new ServerPlayer(cid, tmpsocket);
							gameTable.addPlayer(newPlayer);
							cid++;
						}
						catch(IOException ioe){
							ioe.printStackTrace();
						}
					}
				}
				catch(SocketException se){
					startGame();
				}
			}
			//lobbyAdminListener.closeSocket();
		} catch (IOException e) {
			e.printStackTrace();
		}
		lobbyOpen = false;
	}

	public void startGame() {
		serverUI.showMessage("Lobby closing, game starting");
		gameStarted = true;
		closeServerSocket();
	}

	public boolean kickPlayer(int playerID) {
		synchronized (gameTable) {
			return gameTable.kickPlayer(playerID);
		}
	}

	public void closeLobby() {
		if(lobbyOpen){
			closeServerSocket();
			synchronized(gameTable){
				gameTable.closeTable();
			}
			gameStarted = false;
			lobbyOpen = false;
		}
	}

	/////////////
	//GameLogic//
	/////////////

	public void gameLoop(){
		serverUI.showMessage("Starting game loop");
		//Boradcast player queue
//		gameTable.broadcastQueue();
		
		ServerPlayer currentPlayer = null;
		while(!forcestop && (currentPlayer = gameTable.nextTurn())!=null){
			boolean endofturn = Boolean.FALSE;
			serverUI.showMessage("Next player: "+currentPlayer.toString());
			try {
				if(gameTable.getCardsToDraw()>0){
					serverUI.showMessage("Player needs to draw cards");
					if(gameTable.getWildColor()>=0){
						serverUI.showMessage("Wild four detected, force draw");
						currentPlayer.drawFourOnTop();
						gameTable.forceDraw(currentPlayer);
					}
					else{
						currentPlayer.drawTwoOnTop();
						while(!forcestop && !endofturn){
							serverUI.showMessage("Waiting for player decision");
							int action = currentPlayer.getAction();
							switch(action){
							case ProtocolMessages.GTM_PLAYCARD:
								if(gameTable.playCardAction(currentPlayer)){
									endofturn=Boolean.TRUE;
									currentPlayer.setEndOfTurn();
								}
								break;

							case ProtocolMessages.GTM_DRAWCARD:
								gameTable.drawCardAction(currentPlayer);
								endofturn=Boolean.TRUE;
								currentPlayer.setEndOfTurn();
								break;

							default:
								if(action<0){
									throw new UnexpectedPlayerResponseEception("Error code received: "+action);
								}
								break;
							}
						}
					}
				}
				else{
					while(!forcestop && !endofturn){
						serverUI.showMessage("Waiting for player action");
						int action = currentPlayer.getAction();
						switch(action){
						case ProtocolMessages.GTM_PLAYCARD:
							if(gameTable.playCardAction(currentPlayer)){
								endofturn=Boolean.TRUE;
								currentPlayer.setEndOfTurn();
							}
							break;

						case ProtocolMessages.GTM_DRAWCARD:
							gameTable.drawCardAction(currentPlayer);
							endofturn=Boolean.TRUE;
							currentPlayer.setEndOfTurn();
							break;

						case ProtocolMessages.GTM_ACCUSE:
							gameTable.playerAccuseAction(currentPlayer);
							break;

						default:
							if(action<0){
								throw new UnexpectedPlayerResponseEception("Error code received: "+action);
							}
							break;
						}
					}
				}
			}  catch(IOException e) {
				gameTable.playerDisconnected(currentPlayer);
				e.printStackTrace();
			}
			try {
				switch(currentPlayer.getAction()){
				case ProtocolMessages.GTM_CALLUNO:
					if(endofturn){
						gameTable.callUnoAction(currentPlayer);
					}
					break;

				default:
					break;
				}
			} catch(SocketTimeoutException ste){
				//end of grace period
				try{
					if(!currentPlayer.isUnoCalled()){
						gameTable.noUnoCalled(currentPlayer);
					}
				}
				catch(IOException ioe){
					gameTable.playerDisconnected(currentPlayer);
					ioe.printStackTrace();
				}
			} catch (IOException e) {
				gameTable.playerDisconnected(currentPlayer);
				e.printStackTrace();
			}
			gameTable.endTurn();
		}
		gameTable.endGame();
	}

	public void addDebugPlayers() {
		new DummyPlayer(port);
		new DummyPlayer(port);
	}

	public void retainPlayers(ArrayList<String> checkedPlayers) {
		gameTable.retainPlayers(checkedPlayers);
	}

	public boolean isLobbyOpen() {
		return lobbyOpen;
	}

	public void stopLogic(){
		try {
			forcestop = true;
			if(serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
			gameTable.purge();
			RUNNING = false;
		} catch (IOException e) {
			Log.d("UNO server", "force stopping, don't mind me");
			Log.e("UNO server", e.getMessage());
		}
	}
}

