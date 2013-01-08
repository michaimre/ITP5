package at.itp.uno.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import at.itp.uno.network.protocol.ProtocolMessages;

public class UnoSocketWrapper{
	
	private static final char BS = (char)8;
	private static final char ESC = (char)27;
	public static final int TIMEOUT = 0; //15*1000; TODO change this before releasing ffs
	public static final int EOTTIMEOUT = 2*1000;
	
	private short state;
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	
	public UnoSocketWrapper(Socket socket) throws IOException{
		this(socket, TIMEOUT);
	}

	public UnoSocketWrapper(Socket socket, int timeout) throws IOException{
		this.state=0;
		this.socket=socket;
		this.socket.setSoTimeout(timeout);
		this.in = new DataInputStream(socket.getInputStream());
		this.out = new DataOutputStream(socket.getOutputStream());
	}
	
	public void setTimeout(int timeout) throws IOException{
		socket.setSoTimeout(timeout);
	}
	
	public void write(int msg) throws IOException{
		out.writeInt(msg);
		out.flush();
//		int i;
//		if((i=in.readInt())!=ProtocolMessages.GM_ACK){
//			throw new UnexpectedPlayerResponseEception("GM_ACK expected, got "+ProtocolMessages.getMessageString(i));
//		}
	}
	
	public void write(String msg) throws IOException{
		out.write(BS);
		out.write(msg.getBytes());
		out.write(ESC);
		out.flush();
//		int i;
//		if((i=in.readInt())!=ProtocolMessages.GM_ACK){
//			throw new UnexpectedPlayerResponseEception("GM_ACK expected, got "+ProtocolMessages.getMessageString(i));
//		}
	}
	
	public int read() throws IOException{
		int value = in.readInt();
//		out.writeInt(ProtocolMessages.GM_ACK);
//		out.flush();
		return value;
	}
	
	public String readString() throws IOException{
		StringBuffer line = new StringBuffer();
		char c = (char) in.read();
		if(c!=BS){
			throw new IOException("No String read");
		}
		while((c=(char) in.read())!=ESC){
			line.append(c);
		}
//		out.writeInt(ProtocolMessages.GM_ACK);
//		out.flush();
		return line.toString();
	}
	
	public void setState(short state){
		this.state|=state;
	}
	
	public boolean hasState(short state){
		return ((this.state & state)==state);
	}

	public void close() throws IOException {
		in.close();
		out.close();
		socket.close();
	}
	
	public boolean isClosed(){
		return socket.isClosed();
	}

}
