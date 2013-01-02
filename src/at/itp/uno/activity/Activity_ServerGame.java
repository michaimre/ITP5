package at.itp.uno.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import at.itp.uno.client.ClientGameUI;
import at.itp.uno.client.core.ClientLogic;
import at.itp.uno.data.Card;
import at.itp.uno.data.CardFaces;
import at.itp.uno.data.CardToResourceId;
import at.itp.uno.data.ClientPlayer;
import at.itp.uno.network.protocol.ProtocolMessages;
import at.itp.uno.wifi.Client_AsyncTask;
import at.itp.uno.wifi.ServiceConnection_Service_WifiAdmin;
import at.itp.uno.wifi.Service_WifiAdmin;
import at.itp.uno.wifi.Service_WifiAdmin.Binder_Service_WifiAdmin;
import at.itp_uno_wifi_provider.R;

public class Activity_ServerGame extends Activity implements View.OnClickListener, ClientGameUI {

	private Button b_sendBroadcast;
	private Binder_Service_WifiAdmin _service = null;
	private ServiceConnection_Service_WifiAdmin connection = null;
	private EditText et_broadcastMessage;

	private List<Card> cardsList = new ArrayList();
	private CardToResourceId cdti;
	private LinearLayout horizontalLayout, stapelLayout;
	
	private LinearLayout.LayoutParams layoutParams;
	private Random randomGenerator = new Random();
	private ImageView stapel_ab;
	private ImageView stapel_hin;
	private ImageView mischen; 
	
	private ClientLogic clientLogic;
	
	/** Handles UI updates
	 *  Receives messages from the logic thread
	 */
	private final Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.arg1){
			
				
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
		
		clientLogic = ClientLogic.getInstance();
		clientLogic.setClientGameUI(this);

		cdti = new CardToResourceId();
		
		stapel_ab = (ImageView) findViewById(R.id.imageView_stapel_ab);
		stapel_hin = (ImageView) findViewById(R.id.imageView_stapel_hin);
		mischen = (ImageView) findViewById(R.id.imageView_mischen);
		
		stapel_ab.setOnClickListener(this);
		stapel_hin.setOnClickListener(this);
		mischen.setOnClickListener(this);
		
		horizontalLayout = (LinearLayout) findViewById(R.id.scrollViewLinearLayout);
		stapelLayout = (LinearLayout) findViewById(R.id.linearLayout_stapel);
		horizontalLayout.setOnClickListener(this);
		
		cardsList.add(new Card((short) CardFaces.BLUE, (short) 1));
		cardsList.add(new Card((short) 0, (short) 14));
		cardsList.add(new Card((short) CardFaces.YELLOW, (short) 7));
		cardsList.add(new Card((short) CardFaces.RED, (short) 9));
		cardsList.add(new Card((short) CardFaces.GREEN, (short) 11));

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

		int j = horizontalLayout.indexOfChild(v);

		if (v.equals(stapel_ab)) {
			addCardToHand();
		} else if (v.equals(mischen)) {
			sortCards(cardsList);
		} else {
			removeCardFromHand(j);
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

	private void addCardToHand() {
		Card localCard = randomCard();
		cardsList.add(localCard);
		drawCardsOnScrollView(localCard);
	}

	private void removeCardFromHand(int cardId) {
		horizontalLayout.removeViewAt(cardId);
		stapel_hin.setImageResource(cdti.getResourceId(cardsList.get(cardId)));
		cardsList.remove(cardId);
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

	@Override
	public void showMessage(String message) {
		Log.d("UNO Game", message);
	}

	@Override
	public void showDebug(String message) {
		Log.d("UNO Game", message);
	}

	@Override
	public void showError(String error) {
		Log.e("UNO Game", error);
	}

	@Override
	public void receivedCard(Card card) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receivedTopCard(Card card) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startTurn(boolean ownTurn) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doAction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playCard(Card card) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawCard() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void callUno() {
		// TODO Auto-generated method stub
		
	}
}
