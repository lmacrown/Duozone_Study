package finalsocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

import org.json.JSONObject;

public class ClientControlChat extends ChatClient{
	
	
	
	public void receive() {
		Thread thread = new Thread(() -> {
			try {
				while(true) {
					String json = dis.readUTF();
					JSONObject root = new JSONObject(json);
					String chatName = root.getString("chatName");
					String message = root.getString("message");
					System.out.println("["+chatName+"] "+message);
				}
			} catch(Exception e1) {
			}
		});
		thread.start();
	}
	
	
	public void chatCreate(Scanner scanner) {
		try {

			String chatRoomName;
			System.out.println("생성할 채팅방 이름: ");
			chatRoomName = scanner.nextLine(); 


			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("command", "chatCreate");
			jsonObject.put("chatRoomName", chatRoomName);

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
				}
			}
			
			
			jsonObject.put("command", "endchat");
			json = jsonObject.toString();
			send(json);
			
			disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
