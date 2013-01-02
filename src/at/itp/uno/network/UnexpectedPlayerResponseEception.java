package at.itp.uno.network;

import java.io.IOException;

public class UnexpectedPlayerResponseEception extends IOException {
	
	private static final long serialVersionUID = -5096987028387020933L;

	public UnexpectedPlayerResponseEception() {
		super();
	}

	public UnexpectedPlayerResponseEception(String detailMessage) {
		super(detailMessage);
	}
}
