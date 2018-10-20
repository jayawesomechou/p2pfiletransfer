// The broker for P2PFileTransfer

import java.net.*;
import java.util.*;

public class broker {
	public static List<MyFile> fileList = new ArrayList<MyFile>();
	
	public static void main(String[] args) throws Exception{
		ServerSocket serverSocket = new ServerSocket(1234);
		System.out.println("Broker is up and running.");
		
		while(true) {
			Socket clientSocket = serverSocket.accept();
			System.out.println("Client " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() + " is now connected.");
			clientMonitorThread cmt = new clientMonitorThread(clientSocket);
			Thread T = new Thread(cmt);
			T.setDaemon(true);
			T.start();
		}
	}
}