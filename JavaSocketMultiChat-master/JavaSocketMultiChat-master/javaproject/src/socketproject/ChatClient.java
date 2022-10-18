package socketproject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;



public class ChatClient {

	Socket socket;
	DataInputStream dis;
	DataOutputStream dos;
	String chatName;
	

	
	
	public ChatClient() {
	}

	public  void connect() throws IOException {
		socket = new Socket("localhost", 50001);
		dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());
		System.out.println("[클라이언트] 서버에 연결됨");		
	}

	public void send(String json) throws IOException {
		dos.writeUTF(json);
		dos.flush();
	}

	public void disconnect() throws IOException {
		socket.close();
	}

	
}