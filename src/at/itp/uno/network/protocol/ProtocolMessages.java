package at.itp.uno.network.protocol;

public class ProtocolMessages {
	
	//errors
	public static final int ERR_GENERIC = -1;
	
	public static final int GTM_ENDOFHANDCARDS = -311;
	
	//GenericMessages
	public static final int GM_ACK = 11;
	public static final int GM_GAMECLOSING = 12;
	public static final int GM_PLAYERDROPPED = 13;
	
	//LobbyAdminMessages
	public static final int LAM_STARTGAME = 101;
	public static final int LAM_KICKPLAYER = 102;
	public static final int LAM_CLOSELOBBY = 103;
	
	//LobbyMessages
	public static final int LM_START = 201;
	public static final int LM_PLAYERJOINED = 202;
	public static final int LM_STARTOFPLAYERLIST = 203;
	public static final int LM_ENDOFPLAYERLIST = 204;
	
	//GameTableMessages
	public static final int GTM_DEALCARD = 301;
	public static final int GTM_STARTTURN = 302;
	public static final int GTM_PLAYCARD = 303;
	public static final int GTM_DRAWCARD = 304;
	public static final int GTM_CALLUNO = 305;
	public static final int GTM_ACCUSE = 306;
	public static final int GTM_TOPCARD = 307;
	public static final int GTM_POSITIVEACCUSE = 308;
	public static final int GTM_NEGATIVEACCUSE = 309;
	public static final int GTM_SENDHANDCARDS = 310;
	public static final int GTM_ENDOFTURN = 311;
	public static final int GTM_PLAYRESULT = 312;
	public static final int GTM_VALIDPLAY = 313;
	public static final int GTM_INVALIDPLAY = 314;
	public static final int GTM_SKIPPLAYER = 315;
	public static final int GTM_SETWILDCOLOR = 316;
	public static final int GTM_FORCEDRAW = 317;
	public static final int GTM_GAMEWON = 318;
	
	public static String getMessageString(int code){
		String ret;
		switch(code){
			case ERR_GENERIC:
				ret = "ERR_GENERIC";
				break;
				
			case GTM_ENDOFHANDCARDS:
				ret = "GTM_ENDOFHANDCARDS";
				break;
				
			case GM_ACK:
				ret = "GM_ACK";
				break;
				
			case GM_GAMECLOSING:
				ret = "GM_GAMECLOSING";
				break;
				
			case GM_PLAYERDROPPED:
				ret = "GM_PLAYERDROPPED";
				break;
				
			case LAM_STARTGAME:
				ret = "LAM_STARTGAME";
				break;
				
			case LAM_KICKPLAYER:
				ret = "LAM_KICKPLAYER";
				break;
				
			case LAM_CLOSELOBBY:
				ret = "LAM_CLOSELOBBY";
				break;
				
			case LM_START:
				ret = "LM_START";
				break;
				
			case LM_PLAYERJOINED:
				ret = "LM_PLAYERJOINED";
				break;
				
			case LM_STARTOFPLAYERLIST:
				ret = "LM_STARTOFPLAYERLIST";
				break;
				
			case LM_ENDOFPLAYERLIST:
				ret = "LM_ENDOFPLAYERLIST";
				break;
				
			case GTM_DEALCARD:
				ret = "GTM_DEALCARD";
				break;
				
			case GTM_STARTTURN:
				ret = "GTM_STARTTURN";
				break;
				
			case GTM_PLAYCARD:
				ret = "GTM_PLAYCARD";
				break;
				
			case GTM_DRAWCARD:
				ret = "GTM_DRAWCARD";
				break;
				
			case GTM_CALLUNO:
				ret = "GTM_CALLUNO";
				break;
				
			case GTM_ACCUSE:
				ret = "GTM_ACCUSE";
				break;
				
			case GTM_TOPCARD:
				ret = "GTM_TOPCARD";
				break;
				
			case GTM_POSITIVEACCUSE:
				ret = "GTM_POSITIVEACCUSE";
				break;
				
			case GTM_NEGATIVEACCUSE:
				ret = "GTM_NEGATIVEACCUSE";
				break;
				
			case GTM_SENDHANDCARDS:
				ret = "GTM_SENDHANDCARDS";
				break;
				
			case GTM_ENDOFTURN:
				ret = "GTM_ENDOFTURN";
				break;
				
			case GTM_SKIPPLAYER:
				ret = "GTM_SKIPPLAYER";
				break;
				
			case GTM_SETWILDCOLOR:
				ret = "GTM_SETWILDCOLOR";
				break;
				
			case GTM_FORCEDRAW:
				ret = "GTM_FORCEDRAW";
				break;
				
			case GTM_GAMEWON:
				ret = "GTM_GAMEWON";
				break;
				
			default:
				ret = ""+code;
		}
		return ret;
	}
	
}
