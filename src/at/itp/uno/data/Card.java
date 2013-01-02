package at.itp.uno.data;



public class Card implements Comparable<Card>{
	
	private short face;
	
	public Card(short face){
		if(!CardFaces.isValidColor(face)){
			throw new IllegalArgumentException("Invalid color for this card");
		}
		this.face=face;
	}
	
	public Card(short value, short color){
		if(!CardFaces.isValidColor(color)){
			throw new IllegalArgumentException("Invalid color for this card");
		}
		this.face = (short) (value | color);
	}

	@Override
	public int compareTo(Card paramCard) {
		int i = 0;
		
		if (paramCard.getColor() < this.getColor())
			i = 1;
		
		if (paramCard.getColor() > this.getColor())
			i = -1;
		
		if (i != 0)
			return i;
		else {
			if (paramCard.getValue() < this.getValue())
				i = -1;
			else if (paramCard.getValue() > this.getValue())
				i = 1;
			else
				i = 0;	
		}
		return i;
	}
	
	@Override
	public String toString(){
		StringBuffer ret = new StringBuffer();
		ret.append(CardFaces.getColorString(getColor()));
		ret.append(" - ");
		ret.append(CardFaces.getValueString(getValue()));
		ret.append(" (");
		ret.append(getFace());
		ret.append(")");
		return ret.toString();
	}
	
	public short getFace() {
		return face;
	}

	public void setFace(short face) {
		this.face = face;
	}

	public short getValue(){
		return (short) (face & CardFaces.CLEARMASK);
	}
	
	public short getColor(){
		return (short) ((face >> CardFaces.VALUEBITS) << CardFaces.VALUEBITS);
	}
	
	public boolean isColor(short color){
		return (face & color) != 0;
	}
	
	public boolean matchesColor(Card card){
		return this.getColor()==card.getColor();
	}

}
