
// A thread to send files

import java.nio.file.*;
import java.net.*;
import java.nio.file.Files;
import java.io.*;

public class peerSendFile implements Runnable {

	@Override
	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(5678);
			while(true) {
				Socket receiveFileSocket = serverSocket.accept();
				
				// Read the requested file name
				InputStream ris = receiveFileSocket.getInputStream();
				ObjectInputStream rois = new ObjectInputStream(ris);
				String fileName = (String) rois.readObject();
				// Convert the file into a byte array
				byte[] data = Files.readAllBytes(Paths.get(fileName));
				File file = new File(fileName);
				OutputStream ros = receiveFileSocket.getOutputStream();
				ObjectOutputStream roos = new ObjectOutputStream(ros);
				roos.writeObject(data);
				roos.flush();
				roos.writeObject(file);
				roos.flush();
				
			}
		} catch (Exception E) {
			System.out.println(E);
		}

	}

}