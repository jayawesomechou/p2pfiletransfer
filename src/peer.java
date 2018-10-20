// The Client for P2PFileTransfer

import java.util.*;
import java.net.*;
import java.io.*;

public class peer {
	public static void main(String[] args) {
		try {
			// Thread to send the file
			Thread T = new Thread(new peerSendFile());
			T.setDaemon(true);
			T.start();
			
			
			Socket clientSocket = new Socket(args[0], 1234);
			Scanner scan = new Scanner(System.in);
			System.out.println("Welcome to use P2PFileTransfer!");
			System.out.println("Commands: \nR: register a new file \nU: unregister a file \nB: Browse the registered files \nS: search for and get a file \nE: exit");
			
			OutputStream os = clientSocket.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);
			InputStream is = clientSocket.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(is);
			
			boolean finished = false;
			while (!finished) {
				System.out.println("Waiting for command...");
				String line = scan.nextLine().toUpperCase();
				// The client registers a file
				if (line.equals("R")) {
					oos.writeObject("R");
					oos.flush();
					
					System.out.println("Enter the file name:");
					String fileName = scan.nextLine();
					while (!new File(fileName).exists()) {
						System.out.println("File does not exist in the current folder!");
						System.out.println("Please check and enter the file name:");
						fileName = scan.nextLine();
					}
					oos.writeObject(fileName);
					oos.flush();
					
					String reply = (String) ois.readObject();
					System.out.println(reply);
				} 
				// The client unregisters a file
				else if (line.equals("U")) {
					oos.writeObject("U");
					oos.flush();
					System.out.println("Enter the file name:");
					String fileName = scan.nextLine();
					oos.writeObject(fileName);
					oos.flush();
					
					String reply = (String) ois.readObject();
					System.out.println(reply);
				}
				// The client searches for a file
				else if (line.equals("S")) {
					oos.writeObject("S");
					oos.flush();
					System.out.println("Enter the file name:");
					String fileName = scan.nextLine();
					oos.writeObject(fileName);
					oos.flush();
					
					String reply = (String) ois.readObject();
					System.out.println(reply);
					if (reply.startsWith("E")) {
						InetAddress hostIP = (InetAddress) ois.readObject();
						Socket receiveFileSocket = new Socket(hostIP, 5678);
						OutputStream ros = receiveFileSocket.getOutputStream();
						ObjectOutputStream roos = new ObjectOutputStream(ros);
						roos.writeObject(fileName);
						roos.flush();
						InputStream ris = receiveFileSocket.getInputStream();
						ObjectInputStream rois = new ObjectInputStream(ris);
						byte[] data = (byte[]) rois.readObject();
						File file = (File) rois.readObject();
						OutputStream out = new FileOutputStream(file);
						out.write(data);
						out.close();
						receiveFileSocket.close();
						
						System.out.println("Successfully acquired " + fileName);
					}
				} 
				// The client exits
				else if (line.equals("E")) {
					oos.writeObject("E");
					oos.flush();
					System.out.println("Thank you for using P2PFileTransfer!");
					finished = true;
				} 
				// The client browses the registered files
				else if (line.equals("B")) {
					oos.writeObject("B");
					oos.flush();
					String data = (String) ois.readObject();
					System.out.println(data);
				}
				else {
					System.out.println("Invalid Input! Please re-enter.");
				}
				
			}
			clientSocket.close();
			scan.close();
		} catch (Exception E) {
			System.out.println(E);
		}
	}
}