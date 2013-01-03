package at.itp.uno.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
	private ImageView[] playerTurns;
	private TextView[] playerNames;

	private ClientLogic clientLogic;
	private boolean myTurn, turndirval;

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
		turndirval = CW;

		stapel_ab.setOnClickListener(this);
		stapel_hin.setOnClickListener(this);
		mischen.setOnClickListener(this);
		uno.setOnClickListener(this);
		turndir.setOnClickListener(this);

		horizontalLayout = (LinearLayout) findViewById(R.id.scrollViewLinearLayout);
		stapelLayout = (LinearLayout) findViewById(R.id.linearLayout_stapel);
		horizontalLayout.setOnClickListener(this);

		stapel_hin.measure(View.MeasureSpec.makeMeasureSpec(0, 0),
				View.MeasureSpec.makeMeasureSpec(0, 0));

		layoutParams = new LinearLayout.LayoutParams(new Double(
				0.60 * stapel_hin.getMeasuredWidth()).intValue(), new Double(
						0.60 * stapel_hin.getMeasuredHeight()).intValue());

		stapel_ab.setLayoutParams(layoutParams);
		stapel_hin.setLayoutParams(layoutParams);
		drawCardsOnScrollView(cardsList);
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
				} else if(v.equals(uno)){
					clientLogic.callUno();
				} else {
					int j = horizontalLayout.indexOfChild(v);
					if(clientLogic.playCard(cardsList.get(j))){
						removeCardFromHand(j);
					}
					else{
						//TODO fancy invalid play notification
						Log.d("UNO Game"+clientLogic.getSelf().getName(), "invalid card");
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
		//TODO fancy "your turn" animation
		Log.d("UNO Game"+clientLogic.getSelf().getName(), "doaction");
	}

	@Override
	public void playCard(Card card) {
		//TODO fancy "card played" animation"
		Log.d("UNO Game"+clientLogic.getSelf().getName(), "playcard");
		Message msg = new Message();
		msg.arg1 = ProtocolMessages.GTM_PLAYCARD;
		msg.getData().putSerializable("card", card);
		handler.sendMessage(msg);
	}

	@Override
	public void drawCard() {
		//TODO fancy "draw card" animation
		Log.d("UNO Game"+clientLogic.getSelf().getName(), "drawcard");
	}

	@Override
	public void callUno() {
		//TODO fancy "uno called" animation
		Log.d("UNO Game"+clientLogic.getSelf().getName(), "calluno");
	}

	/////
	//Handler methods
	/////

	public void handleReceivedCard(Card card) {
		//		cardsList.add(card);
		//		sortCards(cardsList);
		//		redrawCardsOnScrollView(cardsList);
		addCardToHand(card);
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
		}
	}

	public void handleDoAction() {
		// TODO Auto-generated method stub
		Log.d("UNO Game"+clientLogic.getSelf().getName(), "handledoaction");
	}

	public void handlePlayCard(Card card) {
		// TODO Auto-generated method stub
		Log.d("UNO Game"+clientLogic.getSelf().getName(), "handleplaycard");
//		if(card.getValue() == CardFaces.REVERSE){
//			if(turndirval){
//				turndirval = CCW;
//				turndir.setImageResource(R.drawable.turn_gegenuhrzeigersinn);
//			}
//			else{
//				turndirval = CW;
//				turndir.setImageResource(R.drawable.turn_uhrzeigersinn);
//			}
//		}
	}

	public void handleDrawCard() {
		// TODO Auto-generated method stub
		Log.d("UNO Game"+clientLogic.getSelf().getName(), "handledrawcard");
	}

	public void handleCallUno() {
		// TODO Auto-generated method stub
		Log.d("UNO Game"+clientLogic.getSelf().getName(), "handlecalluno");
	}
}
