package at.itp.uno.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import at.itp.uno.client.ClientGameUI;
import at.itp.uno.client.core.ClientLogic;
import at.itp.uno.data.Card;
import at.itp.uno.data.CardFaces;
import at.itp.uno.data.CardToResourceId;
import at.itp.uno.data.ClientPlayer;
import at.itp.uno.network.protocol.ProtocolMessages;
import at.itp.uno.wifi.ServiceConnection_Service_WifiAdmin;
import at.itp.uno.wifi.Service_WifiAdmin.Binder_Service_WifiAdmin;
import at.itp_uno_wifi_provider.R;

public class Activity_ServerGame extends Activity implements View.OnClickListener, ClientGameUI {
	
	private static final boolean CW = Boolean.TRUE;
	private static final boolean CCW = Boolean.FALSE;
	
	private static final int FALSECARD = 1;
	private static final int NOTYOURTURN = 2;
	private static final int GAMEWON = 3;

	private static final int DIALOG_HOME = 1;
	private static final int DIALOG_CHOOSE_COLOR = 2;
	
	private Button b_sendBroadcast;
	private Binder_Service_WifiAdmin _service = null;
	private ServiceConnection_Service_WifiAdmin connection = null;
	private EditText et_broadcastMessage;

	private List<Card> cardsList;
	private CardToResourceId cdti;
	private LinearLayout horizontalLayout, stapelLayout;

	private LinearLayout.LayoutParams layoutParams;
	private Random randomGenerator = new Random();
	private ImageView stapel_ab;
	private ImageView stapel_hin;
	private ImageView mischen; 
	private ImageView uno; 
	private ImageView turndir; 
	private ImageView gameDialog; 
	private ImageView[] playerTurns;
	private TextView[] playerNames;

	private ClientLogic clientLogic;
	private boolean myTurn, turndirval, setup;
	private int color, cardindex;

	/** Handles UI updates
	 *  Receives messages from the logic thread
	 */
	private final Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.arg1){
			case ProtocolMessages.GTM_TOPCARD:
				handleReceivedTopCard((Card)msg.getData().getSerializable("card"));
				break;

			case ProtocolMessages.GTM_DEALCARD:
				handleReceivedCard((Card)msg.getData().getSerializable("card"));
				break;

			case ProtocolMessages.GTM_STARTTURN:
				handleStartTurn(msg.getData().getBoolean("myturn"), msg.getData().getInt("pid"));
				break;
				
			case ProtocolMessages.GTM_PLAYCARD:
				handlePlayCard((Card)msg.getData().getSerializable("card"));
				break;
			
			case ProtocolMessages.GTM_GAMEWON:
				handleGameWon();
				break;

			case ProtocolMessages.GTM_ACCUSE:
				handlePlayerAccused();
				break;
				
			case ProtocolMessages.LM_STARTOFPLAYERLIST:
				break;
				
			default:
				break;
			}
		}
	};


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_servergame);
		//		b_sendBroadcast = (Button) findViewById(R.id.b_sendBroadcast);
		// et_broadcastMessage = (EditText)findViewById(R.id.et_Message);
		//		b_sendBroadcast.setOnClickListener(this);

		cardsList = new ArrayList<Card>();
		myTurn = false;
		setup = false;

		clientLogic = ClientLogic.getInstance();
		clientLogic.setClientGameUI(this);
		clientLogic.setActivity(this);

		cdti = new CardToResourceId();

		playerTurns = new ImageView[4];
		playerNames = new TextView[4];

		playerTurns[0] = (ImageView)findViewById(R.id.iv_spieler1_turn);
		playerTurns[1] = (ImageView)findViewById(R.id.iv_spieler2_turn);
		playerTurns[2] = (ImageView)findViewById(R.id.iv_spieler3_turn);
		playerTurns[3] = (ImageView)findViewById(R.id.iv_spieler4_turn);

		playerNames[0] = (TextView)findViewById(R.id.tv_spieler1_name);
		playerNames[1] = (TextView)findViewById(R.id.tv_spieler2_name);
		playerNames[2] = (TextView)findViewById(R.id.tv_spieler3_name);
		playerNames[3] = (TextView)findViewById(R.id.tv_spieler4_name);

		for(int i=0;i<4;i++){
			playerTurns[i].setVisibility(ImageView.INVISIBLE);
			playerNames[i].setText("---");
		}
		playerNames[0].setText(clientLogic.getSelf().getName());
		for(int i=0;i<clientLogic.getOtherPlayers().size();i++){
			playerNames[i+1].setText(clientLogic.getOtherPlayers().get(i).getName());
		}

		stapel_ab = (ImageView) findViewById(R.id.imageView_stapel_ab);
		stapel_hin = (ImageView) findViewById(R.id.imageView_stapel_hin);
		mischen = (ImageView) findViewById(R.id.imageView_mischen);
		uno = (ImageView) findViewById(R.id.imageView_uno);
		turndir = (ImageView) findViewById(R.id.iv_turn_direction);
		gameDialog = (ImageView) findViewById(R.id.imageView_gameDialog);
		turndirval = CW;

		stapel_ab.setOnClickListener(this);
		stapel_hin.setOnClickListener(this);
		mischen.setOnClickListener(this);
		uno.setOnClickListener(this);
		gameDialog.setOnClickListener(this);
		
		horizontalLayout = (LinearLayout) findViewById(R.id.scrollViewLinearLayout);
		stapelLayout = (LinearLayout) findViewById(R.id.linearLayout_stapel);
		horizontalLayout.setOnClickListener(this);

		stapel_hin.measure(View.MeasureSpec.makeMeasureSpec(0, 0),
				View.MeasureSpec.makeMeasureSpec(0, 0));

		layoutParams = new LinearLayout.LayoutParams(new Double(
				0.55 * stapel_hin.getMeasuredWidth()).intValue(), new Double(
						0.55 * stapel_hin.getMeasuredHeight()).intValue());

		stapel_ab.setLayoutParams(layoutParams);
		stapel_hin.setLayoutParams(layoutParams);
		drawCardsOnScrollView(cardsList);
		
		cardsList.add(new Card(CardFaces.WILD, (short)0));
		
		setup = true;
	}

	@Override
	public void onClick(View v) {
		// if (connection != null) {
		// _service.sendTestBroadcast(et_broadcastMessage.getText().toString());
		// }
		
		if(myTurn){
			try {
				if (v.equals(stapel_ab)) {
					clientLogic.drawCard();
				} else if(v.equals(uno)) {
					clientLogic.callUno();
				} else {
					cardindex = horizontalLayout.indexOfChild(v);
					color = -1;
					if(cardsList.get(cardindex).getColor()==0){
						showDialog(DIALOG_CHOOSE_COLOR);
					}
					else{
						playCard(cardsList.get(cardindex), 0);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if (v.equals(gameDialog)){
			showDialog(DIALOG_HOME);
		}
		else {
			showToast(NOTYOURTURN);
		}
	}
	
	public void playCard(Card card, int color){
		try {
			Log.d("UNO Game"+clientLogic.getSelf().getName(), "color: "+color);
			if(clientLogic.playCard(cardsList.get(cardindex), color)){
				removeCardFromHand(cardindex);
			}
			else{
				showToast(FALSECARD);
				Log.d("UNO Game"+clientLogic.getSelf().getName(), "invalid card");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setCancelable(true);
		
		switch (id) {
		case 1:
			alertBuilder.setTitle("Game Menu");
			alertBuilder.setItems(R.array.dialogGame_list, new DialogItemListener_game());
			break;
		case 2: 
			alertBuilder.setTitle("Pick a Color!");
			alertBuilder.setItems(R.array.dialogChooseColor_list, new DialogItemListener_color());
			break;
		default:
			break;
		}
		
		alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		alertBuilder.setIcon(R.drawable.app_icon);
		
		AlertDialog dialog = alertBuilder.create(); 
		dialog.show();
		return super.onCreateDialog(id);
	}
	
	private final class DialogItemListener_game implements DialogInterface.OnClickListener{
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (which == 0){
				sortCards(cardsList);
			}
		}
	}
	
	private final class DialogItemListener_color implements DialogInterface.OnClickListener{
		@Override
		public void onClick(DialogInterface dialog, int which) {
			int color = 0;
			if (which == 0) {
				color = CardFaces.RED;
			} else if (which == 1) {
				color = CardFaces.GREEN;
			} else if (which == 3) {
				color = CardFaces.BLUE;
			} else if (which == 4) {
				color = CardFaces.YELLOW;
			}
			playCard(cardsList.get(cardindex), color);
		}
	}
	
	private void showToast(int param) {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_root));

		TextView text = (TextView) layout.findViewById(R.id.toast_text);
		ImageView image = (ImageView) layout.findViewById(R.id.toast_imageView);
		
		switch (param) {
		case FALSECARD:
			image.setImageResource(R.drawable.kick);
			text.setText(R.string.t_cannotPlayCard);
			break;

		case NOTYOURTURN:
			image.setImageResource(R.drawable.kick);
			text.setText(R.string.t_cannotPlayCardOtherPlayersTurn);
			break;
			
		case GAMEWON:
			image.setImageResource(R.drawable.stern);
			text.setText(R.string.t_gameWon);
			
		default:
			break;
		}
		
		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
		toast.show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		/*bindService(new Intent(this, Service_WifiAdmin.class),
				connection = new ServiceConnection_Service_WifiAdmin(this),
				BIND_AUTO_CREATE);*/
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (connection != null) {
			unbindService(connection);
			connection = null;
		}
	}

	public void setIBinder(Binder_Service_WifiAdmin binder) {
		_service = binder;

	}

	private void drawCardsOnScrollView(Card card) {
		ImageView localImageView = new ImageView(getApplicationContext());
		localImageView.setBackgroundResource(cdti.getResourceId(card));
		localImageView.setLayoutParams(layoutParams);
		localImageView.setOnClickListener(this);
		horizontalLayout.addView(localImageView);
	}

	private void drawCardsOnScrollView(List<Card> cardList) {
		for (int i = 0; i < cardList.size(); i++) {
			ImageView localImageView = new ImageView(getApplicationContext());
			localImageView.setImageResource(cdti.getResourceId((Card) cardList
					.get(i)));
			localImageView.setLayoutParams(layoutParams);
			localImageView.setOnClickListener(this);
			this.horizontalLayout.addView(localImageView);
		}
	}

	private void addCardToHand(Card card) {
		//		Card localCard = randomCard();
		cardsList.add(card);
//		drawCardsOnScrollView(card);
		sortCards(cardsList);
	}

	private void removeCardFromHand(int cardId) {
//		horizontalLayout.removeViewAt(cardId);
		//stapel_hin.setImageResource(cdti.getResourceId(cardsList.get(cardId)));
		cardsList.remove(cardId);
		redrawCardsOnScrollView(cardsList);
	}

	private void sortCards(List<Card> paramList) {
		Collections.sort(paramList);
		redrawCardsOnScrollView(paramList);
	}

	private void redrawCardsOnScrollView(List<Card> cardList) {
		horizontalLayout.removeAllViews();
		drawCardsOnScrollView(cardList);
	}

	private Card randomCard() {
		return new Card((short) this.randomGenerator.nextInt(4),
				(short) this.randomGenerator.nextInt(14));
	}

	private void setMyTurn(boolean ownTurn) {
		this.myTurn = ownTurn;
	}

	/////
	//Logic callbacks
	/////

	@Override
	public void showMessage(String message) {
		Log.d("UNO Game"+clientLogic.getSelf().getName(), message);
	}

	@Override
	public void showDebug(String message) {
		Log.d("UNO Game"+clientLogic.getSelf().getName(), message);
	}

	@Override
	public void showError(String error) {
		Log.e("UNO Game"+clientLogic.getSelf().getName(), error);
	}

	@Override
	public void receivedCard(Card card) {
		Message msg = new Message();
		msg.arg1 = ProtocolMessages.GTM_DEALCARD;
		msg.getData().putSerializable("card", card);
		handler.sendMessage(msg);
	}

	@Override
	public void receivedTopCard(Card card) {
		Message msg = new Message();
		msg.arg1 = ProtocolMessages.GTM_TOPCARD;
		msg.getData().putSerializable("card", card);
		handler.sendMessage(msg);
	}

	@Override
	public void startTurn(boolean ownTurn, int playerId) {
		Message msg = new Message();
		msg.arg1 = ProtocolMessages.GTM_STARTTURN;
		msg.getData().putBoolean("myturn", ownTurn);
		msg.getData().putInt("pid", playerId);
		handler.sendMessage(msg);
	}

	@Override
	public void doAction() {
		Log.d("UNO Game"+clientLogic.getSelf().getName(), "doaction");
	}

	@Override
	public void playCard(Card card) {
		Log.d("UNO Game"+clientLogic.getSelf().getName(), "playcard");
		Message msg = new Message();
		msg.arg1 = ProtocolMessages.GTM_PLAYCARD;
		msg.getData().putSerializable("card", card);
		handler.sendMessage(msg);
	}

	@Override
	public void drawCard() {
		Log.d("UNO Game"+clientLogic.getSelf().getName(), "drawcard");
	}

	@Override
	public void callUno() {
		Log.d("UNO Game"+clientLogic.getSelf().getName(), "calluno");
	}

	@Override
	public void gameWon() {
		Log.d("UNO Game"+clientLogic.getSelf().getName(), "gamewon");
		Message msg = new Message();
		msg.arg1 = ProtocolMessages.GTM_GAMEWON;
		handler.sendMessage(msg);
	}

	@Override
	public void playerAccused() {
		Log.d("UNO Game"+clientLogic.getSelf().getName(), "playeraccused");
		Message msg = new Message();
		msg.arg1 = ProtocolMessages.GTM_ACCUSE;
		handler.sendMessage(msg);
	}

	@Override
	public void updateQueue() {
		Log.d("UNO Game"+clientLogic.getSelf().getName(), "updateQueue");
		Message msg = new Message();
		msg.arg1 = ProtocolMessages.LM_STARTOFPLAYERLIST;
		handler.sendMessage(msg);
	}

	@Override
	public void forceDraw() {
		Log.d("UNO Game"+clientLogic.getSelf().getName(), "forceDraw");
		Message msg = new Message();
		msg.arg1 = ProtocolMessages.GTM_FORCEDRAW;
		handler.sendMessage(msg);
	}

	@Override
	public void drawTwo() {
		Log.d("UNO Game"+clientLogic.getSelf().getName(), "drawTwo");
		Message msg = new Message();
		msg.arg1 = ProtocolMessages.GTM_DRAWTWO;
		handler.sendMessage(msg);
	}

	/////
	//Handler methods
	/////

	public void handleReceivedCard(Card card) {
		//		cardsList.add(card);
		//		sortCards(cardsList);
		//		redrawCardsOnScrollView(cardsList);
		addCardToHand(card);
		if(setup){
			Toast.makeText(this, "Got a card: "+card.toString(), Toast.LENGTH_SHORT).show();
		}
	}

	public void handleReceivedTopCard(Card card) {
		stapel_hin.setImageResource(cdti.getResourceId(card));
	}

	public void handleStartTurn(boolean ownTurn, int playerId) {
		setMyTurn(ownTurn);
		for(int i=0;i<4;i++){
			playerTurns[i].setVisibility(ImageView.INVISIBLE);
		}
		if(ownTurn){
			playerTurns[0].setVisibility(ImageView.VISIBLE);
			Toast.makeText(this, "Your turn!", Toast.LENGTH_SHORT).show();
		}
		else{
			for(ClientPlayer cp:clientLogic.getOtherPlayers()){
				if(cp.getId() == playerId){
					for(int i=0;i<4;i++){
						if(playerNames[i+1].getText().toString().compareToIgnoreCase(cp.getName()) == 0){
							playerTurns[i+1].setVisibility(ImageView.VISIBLE);
							break;
						}
					}
					break;
				}
			}
			Toast.makeText(this, "Not your turn!", Toast.LENGTH_SHORT).show();
		}
	}

	public void handleDoAction() {
		Log.d("UNO Game"+clientLogic.getSelf().getName(), "handledoaction");
		Toast.makeText(this, "Did action", Toast.LENGTH_SHORT).show();
	}

	public void handlePlayCard(Card card) {
		Log.d("UNO Game"+clientLogic.getSelf().getName(), "handleplaycard");
	}

	public void handleDrawCard() {
		Log.d("UNO Game"+clientLogic.getSelf().getName(), "handledrawcard");
		Toast.makeText(this, "Took a card", Toast.LENGTH_SHORT).show();
	}

	public void handleCallUno() {
		Log.d("UNO Game"+clientLogic.getSelf().getName(), "handlecalluno");
	}

	public void handleGameWon() {
		Log.d("UNO Game"+clientLogic.getSelf().getName(), "handleGameWon");
		showToast(GAMEWON);
	}

	public void handlePlayerAccused() {
		Log.d("UNO Game"+clientLogic.getSelf().getName(), "handlePlayerAccused");
		Toast.makeText(this, "Didn't call uno! Have some cards.", Toast.LENGTH_SHORT).show();
	}
	
	public void handleUpdateQueue() {
		Log.d("UNO Game"+clientLogic.getSelf().getName(), "handleUpdateQueue");
		playerNames[0].setText(clientLogic.getSelf().getName());
		for(int i=0;i<clientLogic.getOtherPlayers().size();i++){
			playerNames[i].setText(clientLogic.getOtherPlayers().get(i).getName());
		}
	}

	public void handleForceDraw() {
		Log.d("UNO Game"+clientLogic.getSelf().getName(), "handleForceDraw");
		
	}

	public void handleDrawTwo() {
		Log.d("UNO Game"+clientLogic.getSelf().getName(), "handleDrawTwo");
		
	}
}
