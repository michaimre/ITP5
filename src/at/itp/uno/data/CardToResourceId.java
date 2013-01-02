package at.itp.uno.data;

import at.itp_uno_wifi_provider.R;

public class CardToResourceId {

	public int getResourceId(Card paramCard) {
		if (paramCard.getColor() == 0)
			return getBlackCardRId(paramCard.getValue());
		else if (paramCard.getColor() == CardFaces.RED)
			return getRedCardRId(paramCard.getValue());
		else if (paramCard.getColor() == CardFaces.GREEN)
			return getGreenCardRId(paramCard.getValue());
		else if (paramCard.getColor() == CardFaces.BLUE)
			return getBlueCardRId(paramCard.getValue());
		else if (paramCard.getColor() == CardFaces.YELLOW)
			return getYellowCardRId(paramCard.getValue());
		else
			return R.drawable.schwarz_rueckseite;
	}

	private int getBlackCardRId(short cardId) {
		if (cardId == CardFaces.WILD)
			return R.drawable.schwarz_farbenwechsel;
		if (cardId == CardFaces.WILDFOUR)
			return R.drawable.schwarz_plusvier;
		else
			return R.drawable.schwarz_rueckseite;
	}

	private int getBlueCardRId(short cardId) {

		switch (cardId) {
		case CardFaces.ONE:
			return R.drawable.blau_1;
		case CardFaces.TWO:
			return R.drawable.blau_2;
		case CardFaces.THREE:
			return R.drawable.blau_3;
		case CardFaces.FOUR:
			return R.drawable.blau_4;
		case CardFaces.FIVE:
			return R.drawable.blau_5;
		case CardFaces.SIX:
			return R.drawable.blau_6;
		case CardFaces.SEVEN:
			return R.drawable.blau_7;
		case CardFaces.EIGHT:
			return R.drawable.blau_8;
		case CardFaces.NINE:
			return R.drawable.blau_9;
		case CardFaces.ZERO:
			return R.drawable.blau_0;
		case CardFaces.REVERSE:
			return R.drawable.blau_richtungswechsel;
		case CardFaces.DRAWTWO:
			return R.drawable.blau_pluszwei;
		case CardFaces.SKIP:
			return R.drawable.blau_stop;
		default:
			return R.drawable.schwarz_rueckseite;
		}
	}

	private int getGreenCardRId(short cardId) {

		switch (cardId) {
		case CardFaces.ONE:
			return R.drawable.gruen_1;
		case CardFaces.TWO:
			return R.drawable.gruen_2;
		case CardFaces.THREE:
			return R.drawable.gruen_3;
		case CardFaces.FOUR:
			return R.drawable.gruen_4;
		case CardFaces.FIVE:
			return R.drawable.gruen_5;
		case CardFaces.SIX:
			return R.drawable.gruen_6;
		case CardFaces.SEVEN:
			return R.drawable.gruen_7;
		case CardFaces.EIGHT:
			return R.drawable.gruen_8;
		case CardFaces.NINE:
			return R.drawable.gruen_9;
		case CardFaces.ZERO:
			return R.drawable.gruen_0;
		case CardFaces.REVERSE:
			return R.drawable.gruen_richtungswechsel;
		case CardFaces.DRAWTWO:
			return R.drawable.gruen_pluszwei;
		case CardFaces.SKIP:
			return R.drawable.gruen_stop;
		default:
			return R.drawable.schwarz_rueckseite;
		}
	}

	private int getRedCardRId(short cardId) {
		switch (cardId) {
		case CardFaces.ONE:
			return R.drawable.rot_1;
		case CardFaces.TWO:
			return R.drawable.rot_2;
		case CardFaces.THREE:
			return R.drawable.rot_3;
		case CardFaces.FOUR:
			return R.drawable.rot_4;
		case CardFaces.FIVE:
			return R.drawable.rot_5;
		case CardFaces.SIX:
			return R.drawable.rot_6;
		case CardFaces.SEVEN:
			return R.drawable.rot_7;
		case CardFaces.EIGHT:
			return R.drawable.rot_8;
		case CardFaces.NINE:
			return R.drawable.rot_9;
		case CardFaces.ZERO:
			return R.drawable.rot_0;
		case CardFaces.REVERSE:
			return R.drawable.rot_richtungswechsel;
		case CardFaces.DRAWTWO:
			return R.drawable.rot_pluszwei;
		case CardFaces.SKIP:
			return R.drawable.rot_stop;
		default:
			return R.drawable.schwarz_rueckseite;
		}
	}

	private int getYellowCardRId(short cardId) {
		switch (cardId) {
		case CardFaces.ONE:
			return R.drawable.gelb_1;
		case CardFaces.TWO:
			return R.drawable.gelb_2;
		case CardFaces.THREE:
			return R.drawable.gelb_3;
		case CardFaces.FOUR:
			return R.drawable.gelb_4;
		case CardFaces.FIVE:
			return R.drawable.gelb_5;
		case CardFaces.SIX:
			return R.drawable.gelb_6;
		case CardFaces.SEVEN:
			return R.drawable.gelb_7;
		case CardFaces.EIGHT:
			return R.drawable.gelb_8;
		case CardFaces.NINE:
			return R.drawable.gelb_9;
		case CardFaces.ZERO:
			return R.drawable.gelb_0;
		case CardFaces.REVERSE:
			return R.drawable.gelb_richtungswechsel;
		case CardFaces.DRAWTWO:
			return R.drawable.gelb_pluszwei;
		case CardFaces.SKIP:
			return R.drawable.gelb_stop;
		default:
			return R.drawable.schwarz_rueckseite;
		}
	}

}
