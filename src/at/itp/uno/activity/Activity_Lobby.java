package at.itp.uno.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import at.itp.uno.client.ClientLobbyUI;
import at.itp.uno.client.core.ClientLogic;
import at.itp.uno.data.ClientPlayer;
import at.itp.uno.network.protocol.ProtocolMessages;
import at.itp.uno.wifi.ServiceConnection_Service_WifiAdmin;
import at.itp.uno.wifi.Service_WifiAdmin;
import at.itp.uno.wifi.Service_WifiAdmin.Binder_Service_WifiAdmin;
import at.itp_uno_wifi_provider.R;

public class Activity_Lobby extends Activity implements Button.OnClickListener, ClientLobbyUI, ServiceBindable{

	private ListView lv_players;
	private Button b_startGame, b_main, b_debug;
	private ArrayList<String> _spieler;
	private WifiManager wifi_m;
	private Intent i = null;
	private Binder_Service_WifiAdmin _service = null;
	private ServiceConnection_Service_WifiAdmin connection = null;
	private ClientLogic clientLogic;
	private int _listItemStyle;
	private ListViewAdapterPlayers adapterPlayers;
	private boolean isHost;
	
	/** Handles UI updates
	 *  Receives messages from the logic thread
	 */
	private final Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.arg1){
			case ProtocolMessages.LM_PLAYERJOINED:
				handlePlayerJoined((ClientPlayer)msg.getData().getSerializable("player"));
				break;
				
			case ProtocolMessages.GM_GAMECLOSING:
				handlGameClosing();
				break;
				
			case ProtocolMessages.GM_PLAYERDROPPED:
				handlePlayerDropped((ClientPlayer)msg.getData().getSerializable("player"));
				break;
				
			case ProtocolMessages.LM_START:
				handleGameStarting();
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
		this.setContentView(R.layout.activity_lobby);
		lv_players = (ListView)findViewById(R.id.lv_players);
		lv_players.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		b_startGame = (Button)findViewById(R.id.b_startGame);
		b_startGame.setOnClickListener(this);
		b_main = (Button)findViewById(R.id.b_main);
		b_main.setOnClickListener(this);
		b_debug = (Button)findViewById(R.id.b_ldebug);
		b_debug.setOnClickListener(this);
		_spieler = new ArrayList<String>();
		Bundle extras = getIntent().getExtras();
		_listItemStyle = extras.getInt("textViewResourceId");
		lv_players = (ListView)findViewById(R.id.lv_players);
		
		clientLogic = ClientLogic.getInstance();
		clientLogic.setClientLobbyUI(this);
		clientLogic.setActivity(this);
		
//		clientLogic.getSelf().setName("Hugo");
		
		if(_listItemStyle == android.R.layout.simple_list_item_multiple_choice){
			lv_players.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			clientLogic.joinGame("localhost", 30600);
			isHost = Boolean.TRUE;
		}
		else{
			isHost = Boolean.FALSE;
			b_startGame.setVisibility(Button.INVISIBLE);
			b_main.setVisibility(Button.INVISIBLE);
			//b_debug.setVisibility(Button.INVISIBLE);
			lv_players.setChoiceMode(ListView.CHOICE_MODE_NONE);
			clientLogic.joinGame(getIntent().getExtras().getString("AccessPointIP"), 30600);
		}
		adapterPlayers = new ListViewAdapterPlayers (Activity_Lobby.this, _listItemStyle , _spieler);
		lv_players.setAdapter(adapterPlayers);
	}


	@Override
	public void onClick(View v) {
		if(v.equals(b_startGame)){
			Log.i("There","LocalhostConnection");

			SparseBooleanArray checked = lv_players.getCheckedItemPositions();
			ArrayList<String> checkedPlayers = new ArrayList<String>();
			for (int i = 0; i < lv_players.getCount(); i++){
				if (checked.get(i)) {
					checkedPlayers.add(_spieler.get(i));
				} 
			}
			
			if(checkedPlayers.size()>0){
//			if(checkedPlayers.size()>1){
				_service.startGame(checkedPlayers);
	
				Intent i = new Intent(this,Activity_ServerGame.class);
				startActivity(i);	
			}
			else{
				Toast.makeText(this, "Need at least two players", Toast.LENGTH_LONG).show();
			}
		}
		else if(v.equals(b_main)){

		}
		else if(v.equals(b_debug)){
			_service.addDebugPlayers();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(isHost)
			bindService(new Intent(this, Service_WifiAdmin.class), connection = new ServiceConnection_Service_WifiAdmin(this),BIND_AUTO_CREATE);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(connection != null){
			unbindService(connection);
			connection = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.v("Service Will Stop","Service Will Stop");
		//stopService(i);
	}

	public void setIBinder(Binder_Service_WifiAdmin binder){
		_service = binder;

	}

	/////
	//logic thread message callbacks
	/////
	
	@Override
	public void showMessage(String message) {
		Log.d("UNO lobby", message);
	}


	@Override
	public void showDebug(String message) {
		Log.d("UNO lobby", message);
	}


	@Override
	public void showError(String error) {
		Log.e("UNO lobby", error);
	}

	@Override
	public void playerJoined(ClientPlayer aPlayer) {
		Message msg = new Message();
		msg.arg1 = ProtocolMessages.LM_PLAYERJOINED;
		msg.getData().putSerializable("player", aPlayer);
		handler.sendMessage(msg);
	}

	@Override
	public void gameStarting() {
		Message msg = new Message();
		msg.arg1 = ProtocolMessages.LM_START;
		handler.sendMessage(msg);
	}
	
	@Override
	public void playerDropped(ClientPlayer aPlayer) {
		Message msg = new Message();
		msg.arg1 = ProtocolMessages.GM_PLAYERDROPPED;
		msg.getData().putSerializable("player", aPlayer);
		handler.sendMessage(msg);
	}

	@Override
	public void gameClosing() {
		Message msg = new Message();
		msg.arg1 = ProtocolMessages.GM_GAMECLOSING;
		handler.sendMessage(msg);
	}

	/////
	//UI update messages
	/////
	
	public void handlePlayerJoined(ClientPlayer aPlayer) {
		adapterPlayers.add(aPlayer.getName());
		adapterPlayers.notifyDataSetChanged();
	}

	public void handleGameStarting() {
		Intent i = new Intent(this,Activity_ServerGame.class);
		startActivity(i);
	}
	
	public void handlePlayerDropped(ClientPlayer aPlayer) {
		for(int i=0;i<adapterPlayers.getCount();i++){
			if(adapterPlayers.getItem(i).compareToIgnoreCase(aPlayer.getName())==0){
				adapterPlayers.remove(adapterPlayers.getItem(i));
				adapterPlayers.notifyDataSetChanged();
				break;
			}
		}
	}

	public void handlGameClosing() {
		Toast.makeText(this, "You've been kicked", Toast.LENGTH_LONG).show();
		startActivity(new Intent(this,MainActivity.class));
	}
}