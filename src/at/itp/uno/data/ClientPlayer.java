package at.itp.uno.data;

import java.io.IOException;
import java.io.Serializable;

import at.itp.uno.network.UnoSocketWrapper;

public class ClientPlayer extends Player implements Serializable{

	private static final long serialVersionUID = -9059057620906445372L;

	public ClientPlayer(int playerid){
		super(null);
		setId(playerid);
	}
	
	public ClientPlayer(UnoSocketWrapper socket, String name) throws IOException{
		super(socket);
		setId(socket.read());
		setName(name+getId());
		socket.write(getName());
	}
	
}
