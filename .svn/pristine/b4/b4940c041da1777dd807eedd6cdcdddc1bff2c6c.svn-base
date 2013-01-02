package at.itp.uno.data;

import java.io.IOException;

import at.itp.uno.network.UnoSocketWrapper;

public class ClientPlayer extends Player{

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
