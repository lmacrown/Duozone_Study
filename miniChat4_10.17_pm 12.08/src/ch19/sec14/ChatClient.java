package ch19.sec14;

import java.awt.BorderLayout;
import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Base64;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.json.JSONObject;


public class ChatClient {
	//필드
	Socket socket;
	DataInputStream dis;
	DataOutputStream dos;
	String chatName;
	String chatRoomName;
	static String chatTitle;
	Room room;


	//메소드: 서버 연결
	public  void connect() throws IOException {
		socket = new Socket("localhost", 50001);
		dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());
		System.out.println("[클라이언트] 서버에 연결됨");		
	}

	//메소드: JSON 받기
	public void receive() {
		Thread thread = new Thread(() -> {
			try {
				while(true) {
					String json = dis.readUTF();
					JSONObject root = new JSONObject(json);
					//String clientIp = root.getString("clientIp");
					//String chatName = root.getString("chatName");
					String message = root.getString("message");
					System.out.println(message);
					
				}
			} catch(Exception e1) {
				//System.out.println("[클라이언트] 서버 연결 끊김");
				//System.exit(0);
			}
		});
		thread.start();
	}

	//메소드: JSON 보내기
	public void send(String json) throws IOException {
		dos.writeUTF(json);
		dos.flush();
	}

	//메소드: 서버 연결 종료
	public void disconnect() throws IOException {
		socket.close();
	}	

	public boolean login(Scanner scanner) {
		try {
			String uid;
			String pwd;
			boolean result = false;
			System.out.println("\n1. 로그인 작업");
			System.out.print("아이디 : ");
			uid = scanner.nextLine();
			System.out.print("비밀번호 : ");
			pwd = scanner.nextLine();

			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("command", "login");
			jsonObject.put("uid", uid);
			jsonObject.put("pwd", pwd);

			System.out.println("jsonObject = " + jsonObject.toString());
			send(jsonObject.toString());

			result = loginResponse();

			disconnect();
			return result;


		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean loginResponse() throws Exception {
		String json = dis.readUTF();
		JSONObject root = new JSONObject(json);
		String statusCode = root.getString("statusCode");
		String message = root.getString("message");

		if (statusCode.equals("0")) {
			System.out.println("로그인 성공");
			return true;
		} else {
			System.out.println(message);
			return false;
		}
	}

	public void registerMember(Scanner scanner) {

	}

	public void passwdSearch(Scanner scanner) {
		try {
			String uid;

			System.out.println("\n3. 비밀번호 찾기");
			System.out.print("아이디 : ");
			uid = scanner.nextLine();

			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("command", "passwdSearch");
			jsonObject.put("uid", uid);
			String json = jsonObject.toString();
			send(json);

			passwdSearchResponse();

			disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void passwdSearchResponse() throws Exception {
		String json = dis.readUTF();
		JSONObject root = new JSONObject(json);
		String statusCode = root.getString("statusCode");
		String message = root.getString("message");

		if (statusCode.equals("0")) {
			System.out.println("비밀번호 : " + root.getString("pwd"));
		} else {
			System.out.println(message);
		}
	}

	public void chatCreate(Scanner scanner,ChatClient chatClient) {
		try {
			
			
			System.out.println("생성할 채팅방 이름: ");
			chatRoomName = scanner.nextLine(); 
			OutputStream os = new FileOutputStream("C:/Temp/"+chatRoomName+".db");

			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("command", "chatCreate");
			jsonObject.put("chatRoomName", chatRoomName);
			//chattitle 받아오기
			String json = jsonObject.toString();
			send(json);

			messagePrintResponse();

			disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean chatEnter(Scanner scanner) {
		try {
			String select;
			boolean isEnter;
			System.out.println("입장할 채팅방 번호: ");
			select = scanner.nextLine();
			System.out.println("채팅방 닉네임: ");
			chatName = scanner.nextLine();

			connect();
			
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("command", "chatEnter");
			jsonObject.put("chatNo", select);
			//jsonObject.put("chatName", chatName);
			//jsonObject.put("command", "incoming");
			jsonObject.put("data", chatName);
			String json = jsonObject.toString();
			send(json);
			
			isEnter = chatEnterResponse();
			disconnect();



			return isEnter;


		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean chatEnterResponse() throws Exception {
		String json = dis.readUTF();
		JSONObject root = new JSONObject(json);

		String statusCode = root.getString("statusCode");
		String message = root.getString("message");
		System.out.println(message);
		
		chatTitle = root.getString("chatTitle");
		System.out.println(chatTitle+"불러옴");
		
	
		
		if (statusCode.equals("0")) 
			return true;
		else 
			return false;

	}
	
	public void chatList() {
		try {
			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("command", "chatlist");

			String json = jsonObject.toString();
			send(json);

			messagePrintResponse();
			disconnect();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void exitRoom() {
		try {
			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("command", "chatexit");

			String json = jsonObject.toString();
			send(json);

			messagePrintResponse();
			disconnect();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void removeRoom(Scanner scanner) {
		try {
			String select;
			System.out.println("삭제할 채팅방 번호: ");
			select = scanner.nextLine();


			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("command", "chatrm");
			jsonObject.put("chatNo", select);

			String json = jsonObject.toString();
			send(json);

			messagePrintResponse();
			disconnect();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void messagePrintResponse() throws Exception {
		String json = dis.readUTF();
		JSONObject root = new JSONObject(json);

		String message = root.getString("message");
		
		System.out.println(message);
	}
	
	public void sendMessage(Scanner scanner) {
		try {

			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("command", "chatstart");
			jsonObject.put("chatName", chatName);
			String json = jsonObject.toString();
			
//			FileOutputStream fos = new FileOutputStream("C:/Temp/"+chatRoomName);/* 채팅방이름 추가 */
//			DataOutputStream dos = new DataOutputStream(fos);
//			String chatMessage = null;
			send(json);

			receive();			

			System.out.println("--------------------------------------------------");
			System.out.println("보낼 메시지를 입력하고 Enter");
			System.out.println("채팅를 종료하려면 q를 입력하고 Enter");
			System.out.println("--------------------------------------------------");
			while(true) {
				String message = scanner.nextLine();
				if(message.toLowerCase().equals("q")) {
					jsonObject.put("command", "endchat");
					break;
				} else {
					jsonObject = new JSONObject();
					jsonObject.put("command", "message");
					jsonObject.put("data", message);
					send(jsonObject.toString());
//					//메시지 파일에 저장
//					jsonObject.put(chatMessage, message);
//					dos.writeUTF(chatMessage);
				}
			}
			//scanner.close();
			disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateMember(Scanner scanner) {
		String uid;
		String pwd;
		String name;
		String sex;
		String address;
		String phone;

		try {
			System.out.println("\n4. 회원정보수정");
			System.out.print("아이디 : ");
			uid = scanner.nextLine();
			System.out.print("비번 : ");
			pwd = scanner.nextLine();
			System.out.print("이름 : ");
			name = scanner.nextLine();
			System.out.print("성별[남자(M)/여자(F)] : ");
			sex = scanner.nextLine();
			System.out.print("주소 : ");
			address = scanner.nextLine();
			System.out.print("전화번호 : ");
			phone = scanner.nextLine();

			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("command", "updateMember");
			jsonObject.put("uid", uid);
			jsonObject.put("pwd", pwd);
			jsonObject.put("name", name);
			jsonObject.put("sex", sex);
			jsonObject.put("address", address);
			jsonObject.put("phone", phone);
			String json = jsonObject.toString();
			send(json);

			updateMemberResponse();

			disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	public void updateMemberResponse() throws Exception {
		String json = dis.readUTF();
		JSONObject root = new JSONObject(json);
		String statusCode = root.getString("statusCode");
		String message = root.getString("message");

		if (statusCode.equals("0")) {
			System.out.println("정상적으로 수정되었습니다");
		} else {
			System.out.println(message);
		}
	}
	// 파일전송
	public void fileTrasfer(Scanner scan) throws Exception {
		//파일이름
		System.out.println("파일 이름을 입력하시오");
		String transFileName = scan.next();
		File filename = new File("C:\\temp\\"+transFileName);
		
		
		if (!filename.exists()) {
			System.out.println("파일 없음");
			return;
		}
		
		byte[] arr = new byte[(int)filename.length()];
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(filename));
		in.read(arr);
		in.close();
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("command", "fileTran");
		jsonObject.put("filename", filename.getName());
		jsonObject.put("filetrans", new String(Base64.getEncoder().encode(arr)));
		
		String json = jsonObject.toString();
		connect();
		send(json);
		System.out.println("파일 전송 완료");
	}
	//파일 받기
	public void fileReceive(Scanner scan) throws Exception{
		System.out.println("파일 이름을 입력하시오");
		String transFileName = scan.next();
		File filename = new File("C:\\temp\\"+transFileName);
		
		
		if (!filename.exists()) {
			System.out.println("파일 없음");
			return;
		}
		
		byte[] arr = new byte[(int)filename.length()];
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filename));
		out.write(arr);
		out.close();
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("command", "fileRe");
		jsonObject.put("filename", filename.getName());
		jsonObject.put("filetrans", new String(Base64.getDecoder().decode(arr)));//수정
		
		String json = jsonObject.toString();
		connect();
		send(json);
		System.out.println("파일 받기 완료");
		}
	// 파일리스트 출력
	public void fileListOutput() throws Exception {
		connect();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("command","fileList");
		String json =jsonObject.toString();
		send(json);
		disconnect();
	}
	// 채팅 로그 출력
	public void printChatLog() throws IOException {
		connect();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("command", "chatlog");
		//jsonObject.put(chatName, false);
		String json = jsonObject.toString();
		send(json);
		disconnect();
//		String Data = "C:/Temp/"+chatTitle+".db";
//		File file = new File(Data);
//		Scanner scan = new Scanner(file);
//		while (scan.hasNextLine())
//			System.out.println(scan.nextLine());
	}
	//이미지 파일 불러오기
	public void imageRead() {
			Image image1 = null;
			Image image2 = null;

			try {
				// 파일로부터 이미지 읽기
				File sourceimage = new File("c:\\temp\\targetFile1.jpg");
				image1 = ImageIO.read(sourceimage);

				// InputStream으로부터 이미지 읽기
				InputStream is = new BufferedInputStream(new FileInputStream("c:\\\\temp\\\\targetFile1.jpg"));
				image2 = ImageIO.read(is);

//		        // URL로 부터 이미지 읽기
//		        URL url = new URL("http://www.ojc.asia/images/new_logo.gif");
//		        image3 = ImageIO.read(url);
			} catch (IOException e) {
			}
			// Use a label to display the image
			JFrame frame = new JFrame();

			JLabel label1 = new JLabel(new ImageIcon(image1));
			JLabel label2 = new JLabel(new ImageIcon(image2));

			frame.getContentPane().add(label1, BorderLayout.CENTER);
			frame.getContentPane().add(label2, BorderLayout.NORTH);

			frame.pack();
			frame.setVisible(true);
		}

}