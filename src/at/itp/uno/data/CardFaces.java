package at.itp.uno.data;

import android.graphics.Color;

public class CardFaces {
	
	//faces 6 bits
	public static final short VALUEBITS = 6;
	
	public static final short ONE = 1;
	public static final short TWO = 2;
	public static final short THREE = 3;
	public static final short FOUR = 4;
	public static final short FIVE = 5;
	public static final short SIX = 6;
	public static final short SEVEN = 7;
	public static final short EIGHT = 8;
	public static final short NINE = 9;
	public static final short ZERO = 10;
	public static final short SKIP = 11;
	public static final short DRAWTWO = 12;
	public static final short REVERSE = 13;
	public static final short WILD = 14;
	public static final short WILDFOUR = 15;
	
	//colors
	public static final short CLEARMASK = (short)(Math.pow(2, CardFaces.VALUEBITS+1)-1);
	
	public static final short RED = (1 << VALUEBITS+1);
	public static final short GREEN = (1 << VALUEBITS+2);
	public static final short BLUE = (1 << VALUEBITS+3);
	public static final short YELLOW = (1 << VALUEBITS+4);
	
	//color strings
	public static final String NO_STRING = "No color";
	public static final String RED_STRING = "Red";
	public static final String GREEN_STRING = "Green";
	public static final String BLUE_STRING = "Blue";
	public static final String YELLOW_STRING = "Yellow";
	
	public static boolean isValidColor(short color){
		int i=0;
		if((color & RED) != 0){
			i++;
		}
		if((color & GREEN) != 0){
			i++;
		}
		if((color & BLUE) != 0){
			i++;
		}
		if((color & YELLOW) != 0){
			i++;
		}
		return i<=1;
	}
	
	public static String getColorString(short color){
		if((color & RED) != 0){
			return RED_STRING;
		}
		else if((color & GREEN) != 0){
			return GREEN_STRING;
		}
		else if((color & BLUE) != 0){
			return BLUE_STRING;
		}
		else if((color & YELLOW) != 0){
			return YELLOW_STRING;
		}
		return NO_STRING;
	}
	
	public static int getColor(short color){
		if((color & RED) != 0){
			return Color.RED;
		}
		else if((color & GREEN) != 0){
			return Color.GREEN;
		}
		else if((color & BLUE) != 0){
			return Color.BLUE;
		}
		else if((color & YELLOW) != 0){
			return Color.YELLOW;
		}
		return Color.GRAY;
	}

	public static String getValueString(short value) {
		String ret = "invalid";
		switch(value){
			case ONE: case TWO: case THREE: case FOUR: case FIVE: case SIX: case SEVEN: case EIGHT: case NINE: case ZERO:
				ret = ""+(value%10);
				break;

			case SKIP:
				ret = "skip";
				break;
				
			case DRAWTWO:
				ret = "draw two";
				break;
				
			case REVERSE:
				ret = "reverse";
				break;
				
			case WILD:
				ret = "wild";
				break;
				
			case WILDFOUR:
				ret = "wild draw four";
				break;
		}
		return ret;
	}

}
