package at.itp.uno.network;

import java.io.IOException;
import java.net.ServerSocket;

public abstract class SocketFactory {
	
	public abstract ServerSocket createServerSocket(int port) throws IOException;

}
