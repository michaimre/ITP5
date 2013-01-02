package at.itp.uno.data;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;

public class Deck {

	private Stack<Card> deck;
	private Random rng;

	public Deck(){
		deck = new Stack<Card>();
		rng = new Random();
		fillDeck();
	}

	public void fillDeck(){
		resetDeck(null);
	}

	public void resetDeck(LinkedList<Card> exceptCards) {
		deck = new Stack<Card>();
		if(exceptCards==null){
			exceptCards = new LinkedList<Card>();
		}
		boolean push = true;

		for(int j=1;j<=4;j++){
			short color = (short) (1 << CardFaces.VALUEBITS+j);
			for(short i=1;i<=13;i++){
				Card tmpcard = new Card(i, color);
				for(Card c:exceptCards){
					if(c.getFace()==tmpcard.getFace()){
						push = false;
					}
				}
				if(push){
					deck.push(tmpcard);
					if(i!=10){
						deck.push(tmpcard);
					}
				}
				else{
					push=true;
				}
			}
		}

		//Wild cards
		push = true;
		for(int i=0;i<4;i++){
			Card tmpcard = new Card((short)14, (short)0);
			for(Card c:exceptCards){
				if(c.getFace()==tmpcard.getFace()){
					push = false;
				}
			}
			if(push){
				deck.push(tmpcard);
			}
			else{
				push = true;
			}
			tmpcard = new Card((short)15, (short)0);
			for(Card c:exceptCards){
				if(c.getFace()==tmpcard.getFace()){
					push = false;
				}
			}
			if(push){
				deck.push(tmpcard);
			}
			else{
				push = true;
			}
		}

		Collections.shuffle(deck);
	}

	public Card drawCard(){
		if(hasCards()){
			return deck.pop();
		}
		return null;
	}


	public boolean hasCards(){
		return deck.size()>0;
	}

	public int getSize(){
		return deck.size();
	}

	public void reshuffleCard(Card card){
		int newpos = rng.nextInt(getSize()-1);
		Stack<Card> tmpstack = new Stack<Card>();
		for(int i=0;i<newpos;i++){
			tmpstack.push(deck.pop());
		}
		deck.push(card);
		while(!tmpstack.isEmpty()){
			deck.push(tmpstack.pop());
		}
	}

}
