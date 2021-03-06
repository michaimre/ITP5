package at.itp.uno.wifi;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class Socket_AsyncTask extends AsyncTask<String, String, String> {
	private Context _context;
	private TextView _textView;
	public Socket_AsyncTask(Context c, TextView Text){
		_context = c;
		_textView = Text;
	}
	@Override
	protected String doInBackground(String... arg0) {
		ServerSocket serverSocket = null;
		Socket socket = null;
		String st = null;
		
		try {
			   serverSocket = new ServerSocket(30600);
			   Log.v("Server", "Listening :30600");
			   socket = serverSocket.accept();
			  } catch (IOException e) {
			   // TODO Auto-generated catch block
			   e.printStackTrace();
			  }
		
		try {
			
			Log.v("waiting", "waiting");
			Log.v("Info","Is Connected:"+socket.isConnected() + "RemoteSocket:" + socket.getRemoteSocketAddress().toString());
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            st = input.readLine();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		return st;
	}
	
	 
	 protected void onPostExecute(String result) {
		 _textView.setText("message:" + result);


	    }

}

