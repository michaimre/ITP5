package at.itp.uno.gamelogic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import android.util.Log;
import at.itp.uno.data.Card;
import at.itp.uno.data.CardFaces;
import at.itp.uno.data.Deck;
import at.itp.uno.data.ServerPlayer;
import at.itp.uno.network.UnoSocketWrapper;
import at.itp.uno.network.protocol.ProtocolMessages;
import at.itp.uno.server.ServerUI;


public class GameTable {

	private LinkedList<ServerPlayer> players;
	private LinkedList<ServerPlayer> playerqueue;
	private Deck deck;
	private ServerUI serverUI;
	private Card topCard;
	private boolean skipNextPlayer;
	private int cardsToDraw;
	private boolean rotateForward;
	private short wildColor;

	public GameTable(ServerUI serverUI){
		this.serverUI=serverUI;
		deck = new Deck();
		players = new LinkedList<ServerPlayer>();
		playerqueue = new LinkedList<ServerPlayer>();
		skipNextPlayer = false;
		cardsToDraw = 0;
		rotateForward = true;
		wildColor = -1;
	}

	public int getCardsToDraw() {
		return cardsToDraw;
	}

	public short getWildColor() {
		return wildColor;
	}

	public LinkedList<ServerPlayer> getPlayers() {
		return players; 
	}

	public ServerPlayer getPlayerWithID(int id){
		for(ServerPlayer p:players){
			if(p.getId()==id){
				return p;
			}
		}
		return null;
	}

	public ServerPlayer getActivePlayerWithID(int id){
		for(ServerPlayer p:playerqueue){
			if(p.getId()==id){
				return p;
			}
		}
		return null;
	}

	public boolean addPlayer(ServerPlayer player) throws IOException{
		serverUI.showMessage("Player added to table: "+player.toString());
		for(ServerPlayer p:players){
			p.playerJoined(player);
		}
		player.sendCurrentPlayerList(players);
		return this.players.add(player);
	}

	public boolean kickPlayer(int playerID) {
		ServerPlayer playertokick = null;
		for(ServerPlayer p:players){
			if(p.getId()==playerID){
				playertokick = p;
				break;
			}
		}
		return kickPlayer(playertokick);
	}

	public boolean kickPlayer(ServerPlayer player){
		if(player!=null){
			serverUI.showMessage("Trying to kick player: "+player.toString());
			if(players.contains(player)){
				try {
					player.closeGame();
				} catch (IOException e) {
					e.printStackTrace();
				}
				players.remove(player);
				serverUI.showMessage("Kicked player: "+player.toString());
				for(ServerPlayer p:players){
					try{
						p.playerDropped(p);
					}
					catch(IOException ioe){
						//gracefully drop to avoid possible player drop recursions
					}
				}
				return true;
			}
		}
		serverUI.showError("Couldn't find player");
		return false;
	}

	public void playerDisconnected(ServerPlayer player){
		serverUI.showError("Player disconnected: "+player.toString());
		kickPlayer(player);
	}

	public void setUpGame() {
		serverUI.showMessage("Setting up a new game");
		deck.resetDeck(null);
		//Deal player hands
		for(ServerPlayer p:players){
			try {
				//TODO reset timeout
				p.getSocket().setTimeout(UnoSocketWrapper.TIMEOUT);
				p.startGame();
				serverUI.showMessage("Dealing hand to player: "+p.toString());
				for(int i=0;i<7;i++){
					dealCard(p);
					//					Card tmpcard = deck.drawCard();
					//					serverUI.showMessage(tmpcard.toString());
					//					p.dealCard(tmpcard);
				}
			} catch (IOException ioe) {
				playerDisconnected(p);
				ioe.printStackTrace();
			}
		}
		//Draw first card
		drawFirst();
		serverUI.showMessage("Top card is: "+topCard.toString());
		//Shuffle players
		playerqueue = new LinkedList<ServerPlayer>(players);
		Collections.shuffle(playerqueue);
		serverUI.showMessage("Player queue:");
		for(ServerPlayer p:playerqueue){
			try {
				p.sendTopCard(topCard);
			} catch (IOException e) {
				playerDisconnected(p);
				e.printStackTrace();
			}
			serverUI.showMessage(p.toString());
		}
		serverUI.showMessage("GameTable ready");
	}

	public void closeTable() {
		serverUI.showMessage("Closing table");
		//Remove players
		for(ServerPlayer p:players){
			kickPlayer(p.getId());
		}
		players.clear();
	}

	/**
	 * Broadcasts the start of a new turn.
	 * Broadcasts the id of the current player.
	 * Returns the current player. 
	 * @return
	 */
	public ServerPlayer nextTurn() {
		ServerPlayer nextplayer = null;
		if(playerqueue.size()>0){
			nextplayer = playerqueue.getFirst();
			for(int i=0;i<playerqueue.size();i++){
				ServerPlayer player = playerqueue.get(i);
				try {
					player.startTurn(nextplayer);
				} catch (IOException e) {
					playerDisconnected(player);
					e.printStackTrace();
				}
			}
		}
		return nextplayer;
	}

	public boolean drawFirst() {
		if(topCard==null){
			while(topCard==null){
				topCard = deck.drawCard();
				if(topCard.getValue()==14 || topCard.getValue()==15){
					deck.reshuffleCard(topCard);
					topCard=null;
				}
			}
			return true;
		}
		else{
			return false;
		}
	}

	public void dealCard(ServerPlayer player) throws IOException {
		Card card = deck.drawCard();
		if(card==null){
			//fetch play hands
			LinkedList<Card> handcards = new LinkedList<Card>();
			for(ServerPlayer p:playerqueue){
				handcards.addAll(p.getHandCards());
			}
			handcards.add(topCard);
			//reset deck
			deck.resetDeck(handcards);
			//draw card
			card = deck.drawCard();
		}
		serverUI.showMessage("Deal card: "+card.toString()+" to player: "+player.toString());
		player.dealCard(card);
	}

	public boolean resolveAction(Card playedCard) throws IOException{
		if((wildColor<0 && playedCard.getColor() == topCard.getColor()) || playedCard.getColor()==wildColor){
			if(cardsToDraw>0 && playedCard.getValue()==CardFaces.DRAWTWO){
				cardsToDraw+=2;
				changeTopCard(playedCard);
			}
			else{
				switch(playedCard.getValue()){
				case CardFaces.ONE: case CardFaces.TWO: case CardFaces.THREE: case CardFaces.FOUR: case CardFaces.FIVE: case CardFaces.SIX: case CardFaces.SEVEN: case CardFaces.EIGHT: case CardFaces.NINE: case CardFaces.ZERO:
					changeTopCard(playedCard);
					return true;

				case CardFaces.SKIP:
					skipNextPlayer=true;
					changeTopCard(playedCard);
					return true;

				case CardFaces.DRAWTWO:
					cardsToDraw+=2;
					changeTopCard(playedCard);
					return true;

				case CardFaces.REVERSE:
					rotateForward = !rotateForward;
					changeTopCard(playedCard);
					return true;

				case CardFaces.WILD:
					setWildColor();
					changeTopCard(playedCard);
					return true;

				case CardFaces.WILDFOUR:
					cardsToDraw+=4;
					setWildColor();
					changeTopCard(playedCard);
					return true;
				}
			}
		}
		return false;
	}

	public void changeTopCard(Card newTopCard){
		topCard = newTopCard;
		broadcastMessage(ProtocolMessages.GTM_TOPCARD, null);
		broadcastMessage(topCard.getValue(), null);
		wildColor = -1;
	}

	public void skipNextPlayer(){
		skipNextPlayer = true;
	}

	public void setWildColor() throws IOException{
		wildColor = (short)playerqueue.getFirst().getSocket().read();
	}

	public void forceDraw(ServerPlayer currentPlayer) throws IOException{
		for(int i=0;i<cardsToDraw;i++){
			dealCard(currentPlayer);
		}
		cardsToDraw = 0;
	}

	public void playerAccuseAction(ServerPlayer accusingPlayer) throws IOException {
		ServerPlayer accused = getActivePlayerWithID(accusingPlayer.getAccusedPlayer());
		int success = ProtocolMessages.GTM_NEGATIVEACCUSE;
		if(accused!=null && !accused.isUnoCalled()){
			unoPenalty(accused);
			success = ProtocolMessages.GTM_POSITIVEACCUSE;
		}
		accusingPlayer.sendAccuseSuccess(success);
	}

	private void unoPenalty(ServerPlayer player) throws IOException {
		player.unoPenalty();
		player.dealCard(deck.drawCard());
		player.dealCard(deck.drawCard());
		player.setUnoCalled(Boolean.TRUE);
	}

	public boolean playCardAction(ServerPlayer currentPlayer) throws IOException {
		boolean validPlay = false;
		validPlay = resolveAction(currentPlayer.getPlayedCard());
		Log.d("UNO Server", "valid play: "+validPlay);
		currentPlayer.sendPlayedCardResult(validPlay);
		if(validPlay){
			broadcastMessage(ProtocolMessages.GTM_PLAYCARD, currentPlayer);
		}
		return validPlay;
	}

	public void drawCardAction(ServerPlayer currentPlayer) throws IOException {
		broadcastMessage(ProtocolMessages.GTM_DRAWCARD, currentPlayer);
		if(cardsToDraw>0){
			forceDraw(currentPlayer);
		}
		else{
			dealCard(currentPlayer);
		}
	}

	public void callUnoAction(ServerPlayer currentPlayer) throws IOException {
		broadcastMessage(ProtocolMessages.GTM_CALLUNO, currentPlayer);
		currentPlayer.setUnoCalled(Boolean.TRUE);
	}

	public void endTurn(){
		for(ServerPlayer p:playerqueue){
			try{
				p.endTurn();
			}
			catch(IOException ioe){
				playerDisconnected(p);
			}
		}
		rotatePlayerqueue();
	}

	public boolean rotatePlayerqueue(){
		if(playerqueue.size()>1){
			if(rotateForward){
				playerqueue.addLast(playerqueue.poll());
			}
			else{
				playerqueue.addFirst(playerqueue.removeLast());
			}
			if(skipNextPlayer){
				broadcastMessage(ProtocolMessages.GTM_SKIPPLAYER, null);
				broadcastMessage(playerqueue.getFirst().getId(), null);
				skipNextPlayer=false;
				return rotatePlayerqueue();
			}
			return true;
		}
		return false;
	}

	public void endGame(){
		for(ServerPlayer p:players){
			try {
				p.closeGame();
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally{
				if(p.getSocket()!=null && !p.getSocket().isClosed()){
					try {
						p.getSocket().close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void broadcastMessage(int msg, ServerPlayer currentPlayer){
		for(ServerPlayer p:playerqueue){
			if(currentPlayer!=null && p.getId()==currentPlayer.getId()){
				continue;
			}
			try{
				p.sendBroadcastMessage(msg);
			}
			catch(IOException ioe){
				playerDisconnected(p);
			}
		}
	}

	public void retainPlayers(ArrayList<String> checkedPlayers) {
		LinkedList<Integer> playersToKick = new LinkedList<Integer>();
		for(ServerPlayer p:players){
			if(!checkedPlayers.contains(p.getName()))
				playersToKick.add(p.getId());
		}
		for(Integer i:playersToKick){
			kickPlayer(i);
		}
	}

	public void startGame() {
		for(ServerPlayer p:players){
			try {
				p.startGame();
			} catch (IOException e) {
				kickPlayer(p.getId());
			}
		}
	}

}
