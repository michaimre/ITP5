package at.itp.uno.wifi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import at.itp.uno.server.core.ServerLogic;
import at.itp.uno.server.ui.AndroidLogUI;

public class Service_WifiAdmin extends Service {
	
	private WifiManager wifi_m;	
	private IBinder serviceBinder = null;
	ServerSocket serverSocket = null;
	ArrayList<Socket> clientSockets = null; //Die Client Sockets Liste muss m�glicherweise gelockt werden (m�glicher Datenzugriffkonflikt?)
	private Thread_WaitingForClientConnections thread_WaitingForClients = null;
	private ServerLogic serverLogic;
	
	@Override
	public void onCreate() {
		// The service is being created
		wifi_m = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		wifi_m.createWifiLock(1, "WifiLock");
		Log.d("Service_Wifi_Admin -->","OnCreate");
		clientSockets = new ArrayList<Socket>();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			serverLogic = new ServerLogic(new ServerSocket(30600), 30600, new AndroidLogUI());
			new Thread(serverLogic).start();
		} catch (IOException e) {
			Log.e("UNO service", e.getMessage());
		}
		//if(startServerSocket()){
			//thread_WaitingForClients = new Thread_WaitingForClientConnections();
			//thread_WaitingForClients.start();
			Log.i("Service", "OnStartCommand --> finished");
		//}
		// The service is starting, due to a call to startService()
		return START_NOT_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// A client is binding to the service with bindService()
		Log.i("Service", "OnBind");
		if(serviceBinder == null){
			Log.i("Service", "OnBind --> new ServiceBinder");
			serviceBinder = new Binder_Service_WifiAdmin();
		}
		return serviceBinder;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		// All clients have unbound with unbindService()
		return true;
	}
	 
	@Override
	public void onRebind(Intent intent) {
		// A client is binding to the service with bindService(),
	    // after onUnbind() has already been called
	}

	@Override
	public void onDestroy() {
		// The service is no longer used and is being destroyed
		if(serverLogic.isLobbyOpen()){
			serverLogic.closeLobby();
		}
	}

	
	private boolean startServerSocket(){	
		try {
			   	serverSocket = new ServerSocket(30600);
			   	Log.i("Service", "startServerSocket() -->Listening :30600");
			   	return true;	  
		} 
		catch (IOException e) {
				e.printStackTrace();
				return false;
		}
	}
		
	
	public class Binder_Service_WifiAdmin extends Binder{
		
		//f�r die Communication zwischen Service und Activity!!!!!
		
		
		//diese beiden Methoden sind nur zu Testzwecken hier implementiert, sp�ter sollte dies vom Service selbst ausgehen
		public int sendTestBroadcast(String message){
			//send message to all Players
			Thread_sendUnoBroadcast th = new Thread_sendUnoBroadcast(message);
			th.start();
			return 0;
		}
		
		public int sendTestMessage(Socket playerSocket, String message){
			//send message to one specific player
			return sendUnoMessage(playerSocket,message);
		}
		
		public void kickPlayer(){
//			serverLogic.kickPlayer()
		}

		public void addDebugPlayers() {
			serverLogic.addDebugPlayers();
		}

		public void startGame(ArrayList<String> checkedPlayers) {
			serverLogic.retainPlayers(checkedPlayers);
			serverLogic.startGame();
		}
				
	}
	
	//Thread der die Socketverbindungen zu den Clients aufbaut
	private class Thread_WaitingForClientConnections extends Thread{
		
		@Override
		public void run(){
			
			Log.i("Service", "Waiting Thread --> run()");
			waitingForClients();
			Log.i("Service", "Waiting Thread --> Closing");
			
		}
		
		private void waitingForClients(){
			Log.i("Service", "Waiting Thread --> inside waiting For Clients");
			Socket clientSocket = null; 
			if (serverSocket != null){
				 try {
					while(clientSockets.size() < 4){ //l�uft so lange, bis die max. Spieleranzahl erreicht ist
						clientSocket = null;
						Log.i("Service", "Waiting Thread --> waiting, waiting");
						clientSocket = serverSocket.accept();
						Log.i("Info","Is Connected:"+clientSocket.isConnected() + "RemoteSocket:" + clientSocket.getRemoteSocketAddress().toString());
						clientSockets.add(clientSocket);
					}
				 } catch (IOException e) {
					e.printStackTrace();
					Log.d("Service", "Waiting Thread --> accept_Error!!!");
				 }
			}
		}
		
	}
	
	private Integer sendUnoBroadcast(String message){
		//send message to all Players
		Integer spieler = 1;
		DataInputStream inputStream = null;
		DataOutputStream outputStream = null;
		for (Socket s : clientSockets){
			if(spieler < 4){ //nur jetzt weil der localhost noch als server fungiert!!!!!!
				try {
					inputStream = new DataInputStream(s.getInputStream());
					outputStream = new DataOutputStream(s.getOutputStream());
					Log.i("Service", "SendBroadcast");
					outputStream.writeUTF(message);
					Log.i("Service", "AfterSending");
					String line = inputStream.readUTF();
					Log.i("Service", "Receive from Spieler" + line);
					spieler++;
				} catch (IOException e) {
					e.printStackTrace();
					return -1;
				}
			}
		}
		Log.i("Service", "before Return");
		return 0;
	}
	
	private int sendUnoMessage(Socket playerSocket, String message){
		//send message to one specific player
		return 0;
	}
	
	//Thread der die Socketverbindungen zu den Clients aufbaut
		private class Thread_sendUnoBroadcast extends Thread{
			private String _message;
			
			public Thread_sendUnoBroadcast(String message){
				_message = message;
			}
			
			@Override
			public void run(){
				
				Log.i("Service", "Thread_sendUnoBroadcast --> run()");
				sendUnoBroadcast(_message);
				Log.i("Service", "Thread_sendUnoBroadcast --> Closing"); //warum wird das hier nie ausgegeben???
				
			}
		}
	
}
