package at.itp.uno.data;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import at.itp.uno.network.UnoSocketWrapper;
import at.itp.uno.network.protocol.ProtocolMessages;


public class ServerPlayer extends Player{

	private boolean unoCalled;

	/**
	 * Creates a new <code>Player</code>.
	 * Sends its ID over the <code>Socket</code> and receives the playername.
	 * @param id Unique ID to identify the <code>Player</code> on a <code>GameTable</code>
	 * @param socket The <code>UnoSocketWrapper</code> to be used for communication
	 * @throws IOException 
	 */
	public ServerPlayer(int id, UnoSocketWrapper socket) throws IOException{
		super(socket);
		setId(id);
		socket.write(id);
		setName(socket.readString());
		unoCalled=Boolean.FALSE;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if(!socket.isClosed()){
			socket.close();
		}
	}

	@Override
	public String toString(){
		StringBuffer ret = new StringBuffer(50);
		ret.append("id: ");
		ret.append(getId());
		ret.append(", name: ");
		ret.append(getName());
		ret.append(", socket open: "+(!socket.isClosed()));
		return ret.toString();
	}

	public boolean isUnoCalled() {
		return unoCalled;
	}

	public void setUnoCalled(boolean unoCalled) {
		this.unoCalled = unoCalled;
	}

	public void setSocket(UnoSocketWrapper socket) {
		this.socket = socket;
	}

	public void closeGame() throws IOException {
		socket.write(ProtocolMessages.GM_GAMECLOSING);
	}

	public void dealCard(Card card) throws IOException {
		socket.write(ProtocolMessages.GTM_DEALCARD);
		socket.write(card.getFace());
	}

	public int getAction() throws IOException { 
		return socket.read();
	}

	public void startGame() throws IOException{
		socket.write(ProtocolMessages.LM_START);
	}

	public void sendTopCard(Card topCard) throws IOException {
		socket.write(ProtocolMessages.GTM_TOPCARD);
		socket.write(topCard.getFace());
	}

	public void startTurn(ServerPlayer nextplayer) throws IOException {
		socket.write(ProtocolMessages.GTM_STARTTURN);
		socket.write(nextplayer.getId());
		if(nextplayer.getId()==getId()){
			unoCalled=Boolean.FALSE;
		}
	}

	public Card getPlayedCard() throws IOException {
		return new Card((short)socket.read());
	}

	public Collection<? extends Card> getHandCards() throws IOException {
		LinkedList<Card> cards = new LinkedList<Card>();
		socket.write(ProtocolMessages.GTM_SENDHANDCARDS);
		int card;
		while((card=socket.read())!=ProtocolMessages.GTM_ENDOFHANDCARDS){
			cards.add(new Card((short) card));
		}
		return cards;
	}

	public void setEndOfTurn() throws IOException {
		socket.setTimeout(UnoSocketWrapper.EOTTIMEOUT);
		unoCalled = Boolean.FALSE;
	}

	public void endTurn() throws IOException {
		//socket.write(ProtocolMessages.GTM_ENDOFTURN);
		socket.setTimeout(UnoSocketWrapper.TIMEOUT);
	}

	public int getAccusedPlayer() throws IOException {
		return socket.read();
	}

	public void unoPenalty() throws IOException {
		socket.write(ProtocolMessages.GTM_ACCUSE);
	}

	public void sendAccuseSuccess(int success) throws IOException {
		socket.write(success);
	}

	public void playerJoined(ServerPlayer player) throws IOException {
		socket.write(ProtocolMessages.LM_PLAYERJOINED);
		socket.write(player.getId());
		socket.write(player.getName());
	}

	public void playerDropped(ServerPlayer player) throws IOException {
		socket.write(ProtocolMessages.GM_PLAYERDROPPED);
		socket.write(player.getId());
	}

	public void sendCurrentPlayerList(LinkedList<ServerPlayer> players) throws IOException {
		socket.write(ProtocolMessages.LM_STARTOFPLAYERLIST);
		for(ServerPlayer p:players){
			if(p.getId()!=getId()){
				socket.write(p.getId());
				socket.write(p.getName());
			}
		}
		socket.write(ProtocolMessages.LM_ENDOFPLAYERLIST);
	}

	public void sendBroadcastMessage(int msg) throws IOException {
		socket.write(msg);
	}

	public void sendBroadcastMessage(String msg) throws IOException {
		socket.write(msg);
	}

	public void sendPlayedCardResult(boolean validAction) throws IOException {
		if(validAction){
			socket.write(ProtocolMessages.GTM_VALIDPLAY);
		}
		else{
			socket.write(ProtocolMessages.GTM_INVALIDPLAY);
		}
	}

	public void gameWon() throws IOException {
		socket.write(ProtocolMessages.GTM_GAMEWON);
	}
}
