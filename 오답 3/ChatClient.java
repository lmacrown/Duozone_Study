package chatRoom;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

import org.json.JSONObject;

import chatRoom.ChatClient;

public class ChatClient {
	// 필드
	static Socket socket;
	DataInputStream dis;
	DataOutputStream dos;
	static String chatName;

	// 메소드: 서버 연결
	public void connect() throws IOException {
		socket = new Socket("localhost", 50001);
		dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());
		System.out.println("[클라이언트] 서버에 연결됨");
	}

	// 채팅방 존재 확인
	public boolean exist() throws Exception {// dos=채팅방
		File is = new File("C:/Temp/" + dos + ".db");
		if (is.exists()) {
			return true;
		}
		return false;
	}

	// 메소드: JSON 받기
	public void receive() {
		Thread thread = new Thread(() -> {
			try {
				while (true) {
					String json = dis.readUTF();
					JSONObject root = new JSONObject(json);
					String clientIp = root.getString("clientIp");
					String chatName = root.getString("chatName");
					String message = root.getString("message");
					System.out.println("<" + chatName + "@" + clientIp + "> " + message);
				}
			} catch (Exception e1) {
				System.out.println("[클라이언트] 서버 연결 끊김");
				System.exit(0);
			}
		});
		thread.start();
	}

	// 메소드: JSON 보내기
	public void send(String json) throws IOException {
		dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());
		dos.writeUTF(json);
		dos.flush();
	}

	// 메소드 : JSON값 파일에 출력
	public void input(String json, String chatName) throws IOException {
		FileOutputStream fos = new FileOutputStream("C:/Temp/" + chatName + ".db");
		DataOutputStream dos = new DataOutputStream(fos);
		dos.writeUTF(json);
		dos.flush();

	}

	// 메소드: 서버 연결 종료
	public void unconnect() throws IOException {
		socket.close();
	}

	// 방 존재하면 입장
	public void enterChatRoom() throws Exception {
		String json = dis.readUTF();
		JSONObject root = new JSONObject(json);
		String statusCode = root.getString("statusCode");
		String message = root.getString("message");

		if (statusCode.equals("0")) {
			System.out.println("로그인 성공");
		} else {
			System.out.println(message);
		}
	}

	public void messageInput(String inputMessage) throws Exception {
		while (true) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("data", inputMessage);
			String json = jsonObject.toString();
			send(json);

			receive();
		}
	}

	// 메소드: 메인
	public static void main(String[] args) throws Exception {
		try {
			ChatClient chatClient = new ChatClient();
			Socket so = new Socket();
			Scanner scanner = new Scanner(System.in);
			chatClient.connect();

			boolean stop = false;

			while (false == stop) {
				System.out.println("방 이름 입력 : ");
				String chatName = scanner.nextLine();
				File fileName = new File("C:/Temp/" + chatName + ".db");

				if(socket.isConnected()) {
					chatClient.connect();
					
				}
				if (fileName.exists()) {
					// 채팅방 입장
					chatClient.connect();// 방이름 넣고 입장
					System.out.println("방 입장");
					BufferedReader br = new BufferedReader(new FileReader(fileName));// 파일에 있는 값 출력
					int lineNo = 1;
					while (true) {
						String str = br.readLine();
						if (str == null)
							break;
						System.out.println(lineNo + "\t" + str);
						lineNo++;
					}
					br.close();

					while (true) {
						if (chatName.equals("q"))
							break;
						String inputMessage = scanner.nextLine();
						chatClient.messageInput(inputMessage);
						chatClient.input(inputMessage, chatName);

					}
				}

				// 채팅방 내용 입력 메서드로 만들것
				else {
					OutputStream os = new FileOutputStream("C:/Temp/" + chatName + ".db");// 채팅방 생성
					System.out.println("방 생성");

					while (true) {
						if (chatName.equals("q"))
							break;
						String inputMessage = scanner.nextLine();
						chatClient.messageInput(inputMessage);
						chatClient.input(inputMessage, chatName);
					}
				}
			}
			scanner.close();
			chatClient.unconnect();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("[클라이언트] 서버 연결 안됨");
		}
	}
}