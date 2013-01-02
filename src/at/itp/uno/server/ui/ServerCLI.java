package at.itp.uno.server.ui;

import at.itp.uno.server.ServerUI;

public class ServerCLI implements ServerUI{

	public ServerCLI(){
		System.out.println("Server CLI loaded");
	}
	
	@Override
	public synchronized void showMessage(String message) {
		System.out.println(message);
	}

	@Override
	public synchronized void showError(String error) {
		System.err.println(error);
	}

}
