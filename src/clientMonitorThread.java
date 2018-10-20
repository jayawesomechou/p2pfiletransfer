
// A thread for broker to manager clients

import java.net.*;
import java.io.*;

public class clientMonitorThread implements Runnable{
	private Socket clientSocket;
	 
	public clientMonitorThread(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	
	@Override
	public void run() {
		try {
			InetAddress ip = clientSocket.getInetAddress();
			int port = clientSocket.getPort();
		
			InputStream is = clientSocket.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(is);
			OutputStream os = clientSocket.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);
			while (true) {
				String request = (String) ois.readObject();
				
				// Client registers a file
				if (request.equals("R")) {
					String fileName = (String) ois.readObject();
					MyFile file = new MyFile(fileName, ip, port);
					
					boolean existed = false;
					for (int i = 0; i < broker.fileList.size(); i++) {
						if (broker.fileList.get(i).getFileName().equals(fileName)) {
							existed = true;
							oos.writeObject("Register Fail! File already registered.");
							oos.flush();
						}
					}
					if (!existed) {
						oos.writeObject("File Registered");
						broker.fileList.add(file);
						System.out.println("Client " + ip + ":" + port + " registered " + fileName);
					}
				} 
				// Client unregisters a file
				else if (request.equals("U")) {
					String fileName = (String) ois.readObject();
					boolean existed = false;
					for (int i = 0; i < broker.fileList.size(); i++) {
						if (broker.fileList.get(i).getFileName().equals(fileName)) {
							existed = true;
							if (!ip.equals(broker.fileList.get(i).getIP())) {
								oos.writeObject("You did not register " + fileName);
								oos.flush();
							} else {
								broker.fileList.remove(i);
								oos.writeObject("You have successfully unregistered " + fileName);
								oos.flush();
								System.out.println("Client " + ip + ":" + port + " unregistered " + fileName);
							}
						}
					}
					if (!existed) {
						oos.writeObject("Unregister fail! The file is not registered.");
						oos.flush();
					}
				} 
				// Client searches for a file 
				else if (request.equals("S")) {
					String fileName = (String) ois.readObject();
					boolean existed = false;
					for (int i = 0; i < broker.fileList.size(); i++) {
						if (broker.fileList.get(i).getFileName().equals(fileName)) {
							existed = true;
							InetAddress hostIP = broker.fileList.get(i).getIP();
							oos.writeObject("Establishing connection with host...");
							oos.flush();
							oos.writeObject(hostIP);
							oos.flush();
							System.out.println("Client " + ip + ":" + port + " requested " + fileName);
						}
					}
					if (!existed) {
						oos.writeObject("Sorry. The requested file does not exist.");
						oos.flush();
					}
				} 
				// Client browses the available files
				else if (request.equals("B")) {
					if (broker.fileList.size() == 0) {
						oos.writeObject("There are no files registered.");
						oos.flush();
					} else {
						String data = "The registered files are";
						for (int i = 0; i < broker.fileList.size(); i++) 
							data += "\n" + broker.fileList.get(i).getFileName();
						oos.writeObject(data);
						oos.flush();
					}
				}
				// Client exits
				else if (request.equals("E")) {
					System.out.println("Client " + ip + ":" + port + " is now disconnected.");
					clientSocket.close();
					break;
				}
				
			}
		} catch (Exception E) {
			System.out.println(E);
		}
	}
	
}