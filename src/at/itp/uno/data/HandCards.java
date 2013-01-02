package at.itp.uno.data;

import java.util.LinkedList;

public class HandCards {
	
	private LinkedList<Card> cards;
	
	public HandCards(){
		cards = new LinkedList<Card>();
	}
	
	public void addCard(Card card) {
		cards.add(card);
	}

}
