package ch19.sec14;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;

import ch19.sec14.ChatServerWatchService;

public class ChatServer {
	//필드
	ServerSocket serverSocket;
	ExecutorService threadPool = Executors.newFixedThreadPool(100);
	Map<String, SocketClient> chatRoom = Collections.synchronizedMap(new HashMap<>());
	MemberRepository memberRepository = new MemberRepository();
	
	
	List<SocketClient> clients;
	RoomManager roomManager = new RoomManager(clients);

	//메소드: 서버 시작
	public void start() throws IOException {
		
		memberRepository.loadMember();
		
		serverSocket = new ServerSocket(50001);	
		System.out.println( "[서버] 시작됨");
		
		Thread thread = new Thread(() -> {
			try {
				while(true) {
					Socket socket = serverSocket.accept();
					SocketClient sc = new SocketClient(this, socket,roomManager);
				}
			} catch(IOException e) {
			}
		});
		thread.start();
	}
	//메소드: 클라이언트 연결시 SocketClient 생성 및 추가
	public void addSocketClient(SocketClient socketClient) {
		String key = socketClient.chatName + "@" + socketClient.clientIp;
		chatRoom.put(key, socketClient);
		System.out.println("입장: " + key);
		System.out.println("현재 채팅자 수: " + chatRoom.size() + "\n");
	}

	//메소드: 클라이언트 연결 종료시 SocketClient 제거
	public void removeSocketClient(SocketClient socketClient) {
		String key = socketClient.chatName + "@" + socketClient.clientIp;
		chatRoom.remove(key);
		System.out.println("나감: " + key);
		System.out.println("현재 채팅자 수: " + chatRoom.size() + "\n");
	}		
	//메소드: 모든 클라이언트에게 메시지 보냄
	public void sendToAll(SocketClient sender, String message) {
		JSONObject root = new JSONObject();
		root.put("clientIp", sender.clientIp);
		root.put("chatName", sender.chatName);
		root.put("message", message);
		String json = root.toString();
		
		//귀속말 존재 여부 확인 
		if (message.indexOf("@") == 0) {
			int pos = message.indexOf(" ");
			String key = message.substring(1, pos);
			message = "(귀속말)" + message.substring(pos+1);
			
			SocketClient targetClient = chatRoom.get(key);
			if (null != targetClient) {
				targetClient.send(json);	
			}
			
		} else {
			//체팅방에 있은 모든 사람(본인제외) 
			chatRoom.values().stream()
			.filter(socketClient -> socketClient != sender)
			.forEach(socketClient -> socketClient.send(json));
		}
	}
	
	public void sendMessage(SocketClient sender, String message) throws IOException {
		JSONObject root = new JSONObject();
		//root.put("clientIp", sender.clientIp);
		//root.put("chatName", sender.chatName);
		root.put("message", message);
		String json = root.toString();
		/*
		//귀속말 존재 여부 확인 
		if (message.indexOf("@") == 0) {
			int pos = message.indexOf(" ");
			String key = message.substring(1, pos);
			message = "(귀속말)" + message.substring(pos+1);
			
			SocketClient targetClient = chatRoom.get(key);
			if (null != targetClient) {
				targetClient.send(json);	
			}
			
		} else {
			//체팅방에 있은 모든 사람(본인제외) 
			chatRoom.values().stream()
			.filter(socketClient -> socketClient != sender)
			.forEach(socketClient -> socketClient.send(json));
		}
		*/
		
		//ChatClient.chatTitle = root.getString("chatTitle");
		//String chatTitle = sender.room.title;
//		System.out.println(ChatClient.chatTitle+"에 입력");
//		FileWriter filewriter = new FileWriter("C:/Temp/"+ChatClient.chatTitle+".db", true);
//		System.out.println(ChatClient.chatTitle+"접속");
		String chatTitle = roomManager.loadRoom(sender.chatName).title;
		System.out.println(chatTitle+"에 입력");
		FileWriter filewriter = new FileWriter("C:/Temp/"+chatTitle+".db", true);
		System.out.println(chatTitle+"접속");
		filewriter.write(message);
		filewriter.flush();
		filewriter.write("\n");
		filewriter.close();

		
	
		for (SocketClient c : roomManager.loadRoom(sender.chatName).clients) {
            if (!c.equals(sender)) {
                c.send(json);
                
            }
            System.out.println(c);
        }
        
		System.out.println(sender);
		//sender.send(json);
	}
	
	//메소드: 서버 종료
	public void stop() {
		try {
			serverSocket.close();
			threadPool.shutdownNow();
			chatRoom.values().stream().forEach(sc -> sc.close());
			System.out.println( "[서버] 종료됨 ");
		} catch (IOException e1) {}
	}
	
	public synchronized void registerMember(Member member) throws Member.ExistMember {
		memberRepository.insertMember(member);
	}
	
	public synchronized Member findByUid(String uid) throws Member.NotExistUidPwd {
		return memberRepository.findByUid(uid);
	}
	
	//메소드: 메인
	public static void main(String[] args) {	
		try {
			ChatServer chatServer = new ChatServer();
			chatServer.start();
		
			//String path = "C:\\Users\\한국소프트웨어산업협회\\git\\douzone\\lombok_app\\bin\\ch19\\sec14";
			//System.out.println(path);
			
			//서버의 클래스 폴더 모니터링  
			//Thread watchThread = new Thread(new ChatServerWatchService(path));
			//watchThread.setDaemon(true);
			//watchThread.start();

			System.out.println("----------------------------------------------------");
			System.out.println("서버를 종료하려면 q를 입력하고 Enter.");
			System.out.println("----------------------------------------------------");
			
			Scanner scanner = new Scanner(System.in);
			while(true) {
				String key = scanner.nextLine();
				if(key.equals("q")) 	break;
			}
			scanner.close();
			chatServer.stop();
		} catch(IOException e) {
			System.out.println("[서버] " + e.getMessage());
		}
	}
}