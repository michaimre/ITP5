package at.itp.uno.data;

import at.itp.uno.network.UnoSocketWrapper;

public abstract class Player {
	
	private int id;
	private String name;
	protected UnoSocketWrapper socket;
	private int cards;
	
	public Player(UnoSocketWrapper socket){
		this.socket=socket;
		this.id = -1;
		this.name= "";
		this.cards = 0;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UnoSocketWrapper getSocket() {
		return socket;
	}

	public int getCards() {
		return cards;
	}

	public void setCards(int cards) {
		this.cards = cards;
	}
	
	public int addCard(){
		return ++cards;
	}
	
	public int removeCard(){
		return --cards;
	}

}
