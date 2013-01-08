package at.itp.uno.server.core;

import java.io.IOException;

import at.itp.uno.network.DesktopSocketFactory;
import at.itp.uno.network.SocketFactory;
import at.itp.uno.server.ServerUI;
import at.itp.uno.server.ui.ServerCLI;

public class UnoServer {
	
	private ServerLogic serverLoop;
	private Thread loopThread;
	
	public UnoServer(int port){
		this(new DesktopSocketFactory(), new ServerCLI(), port);
	}

	public UnoServer(SocketFactory socketFactory, ServerUI serverUI, int port){
		try {
			serverLoop = new ServerLogic(socketFactory, port, serverUI);
			loopThread = new Thread(serverLoop);
			loopThread.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
