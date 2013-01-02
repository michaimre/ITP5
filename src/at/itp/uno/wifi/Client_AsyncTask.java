package at.itp.uno.wifi;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import at.itp.uno.activity.Activity_ClientGame;

/* Diese Klasse, dieser AsyncTast baut einen Client Socket zum Server auf, und wandert dann in eine Schleife und erwartet solange eine
 * Verbindung zum ServerSocket besteht eine Nachricht und antwortet auf diese
 */

public class Client_AsyncTask extends AsyncTask <String,String,String> {
	private Activity _activity;
	private Context _context;
	private Socket socket = new Socket();
	private EditText et_spielerName = null;
	
	
	
	public Client_AsyncTask(Context c, Activity a, EditText spielerName){
		_context = c;
		_activity = a;
		et_spielerName = spielerName;
		
		try {
			socket.bind(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public Client_AsyncTask(Context c, Activity a){
		_context = c;
		_activity = a;
		try {
			socket.bind(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	protected String doInBackground(String... arg0) {
		SocketAddress sockaddr = null;
		if(_activity instanceof Activity_ClientGame){
			sockaddr = new InetSocketAddress("192.168.43.1", 30600);
			ConnectivityManager cm =(ConnectivityManager)_activity.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = null;
			do{
				netInfo = cm.getActiveNetworkInfo();
			}
			while (netInfo == null || !netInfo.isConnected());
		}
		else{
			sockaddr = new InetSocketAddress("127.0.0.1", 30600);
		}
		
		connectToSocket(sockaddr); //Connect to ServerSocket
		
		if(_activity instanceof Activity_ClientGame){ //nur jetzt so, weil Loacalhost im moment als Server fungiert
			Log.i(" "," " + socket.getRemoteSocketAddress().toString());
			DataInputStream inputStream = null;
			DataOutputStream outputStream = null;
			try {
				inputStream = new DataInputStream(socket.getInputStream());
				outputStream = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			while(socket.isConnected()){
					try {
						Log.i("Client AsyncTask", "Waiting for Line");
						String line = inputStream.readUTF();
						Log.i("Client AsyncTask", "Received Data -->" + line);
						outputStream.writeUTF(et_spielerName.getText().toString());
					} catch (IOException e) {
						e.printStackTrace();
						Log.i("Exception AsyncTask", "IO Exception" + e.toString());
					}
			}
		}
		
		return null;
	}
	
	/*@Override
	protected void onProgressUpdate(String... values) {
		//super.onProgressUpdate(values);
		_spielerName.setText(values[0]);
		
	}*/
	
	private void connectToSocket(SocketAddress socketAddress){
		try{
			socket.connect(socketAddress);
		
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		catch (ConnectException e) {
		Log.v("ConnectException",e.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

}