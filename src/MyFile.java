// Implementation of a data structure used to save file information on the server

import java.io.Serializable;
import java.net.*;

public class MyFile implements Serializable {
	private String fileName;
	private InetAddress ip;
	private Integer port;
	
	public MyFile(String fileName, InetAddress ip, Integer port) {
		this.fileName = fileName;
		this.ip = ip;
		this.port = port;
	}
	
	public String getFileName() {
		return this.fileName;
	}

	public InetAddress getIP() {
		return this.ip;
	}
	
	public int getPort() {
		return this.port;
	}
}