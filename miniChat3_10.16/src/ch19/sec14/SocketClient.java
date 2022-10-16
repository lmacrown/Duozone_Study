package ch19.sec14;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.json.JSONObject;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SocketClient {
	//필드
	ChatServer chatServer;
	Socket socket;
	DataInputStream dis;
	DataOutputStream dos;
	String clientIp;	
	String chatName;
	Room room;
	RoomManager roomManager;
	
	
	//생성자
	public SocketClient(ChatServer chatServer, Socket socket,RoomManager roomManager) {
		try {
			this.chatServer = chatServer;
			this.socket = socket;
			this.dis = new DataInputStream(socket.getInputStream());
			this.dos = new DataOutputStream(socket.getOutputStream());
			InetSocketAddress isa = (InetSocketAddress) socket.getRemoteSocketAddress();
			this.clientIp = isa.getHostName();
			this.roomManager = roomManager;
			//this.room=roomManager.loadRoom(this);
			receive();
		} catch(IOException e) {
		}
	}	
	//메소드: JSON 받기
	public void receive() {
		chatServer.threadPool.execute(() -> {
			try {
				boolean stop = false;
	
				while(true != stop) {
					String receiveJson = dis.readUTF();		
					
					JSONObject jsonObject = new JSONObject(receiveJson);
					String command = jsonObject.getString("command");
					
					/*
					입장 : incoming,이름
					{command:incoming, data:'홍길동'}
					
					 채팅 : message,내용
					{command:message, data:'안녕'}
					
					*/
					switch(command) {
					case "login":
						login(jsonObject);
						stop = true;
						break;
					case "passwdSearch":
						passwdSearch(jsonObject);
						stop = true;
						break;

					case "updateMember":
						updateMember(jsonObject);
						stop = true;
						break;

					case "incoming":
						this.chatName = jsonObject.getString("data");
						chatServer.sendToAll(this, "들어오셨습니다.");
						chatServer.addSocketClient(this);
						break;
					case "message":
						String message = jsonObject.getString("data");
						
						//chatServer.sendToAll(this, message);
						chatServer.sendMessage(this, message);
						break;
						
					case "chatlist":
						chatList();
						stop = true;
						break;
					case "chatCreate":
						chatCreate(jsonObject);
						stop = true;
						break;
						
					case "chatEnter":
						chatEnter(jsonObject);
						stop = true;
						break;
						
					case "chatexit":
						chatExit();
						stop = true;
						break;
					case "chatrm":
						removeRoom(jsonObject);
						stop = true;
						break;
					case "endchat":
						stop = true;
						break;
					case "chatstart":
						startChat(jsonObject);
						break;

					}
				}
			} catch(IOException e) {
				e.printStackTrace();
				chatServer.sendToAll(this, "나가셨습니다.");
				chatServer.removeSocketClient(this);
			}
		});
	}
	public void chatList() {

		String roomStatus;
	
		roomStatus = "[room status]\n";
        if(roomManager.rooms.size() > 0) {
            for (Room room : roomManager.rooms) {
                roomStatus += String.format("{no : %s, title : %s}\n", room.no, room.title);
            }
            roomStatus = roomStatus.substring(0,roomStatus.length()-1);
        }
        
        //roomStatus += "]";

		JSONObject jsonResult = new JSONObject();


		jsonResult.put("message", roomStatus);


		send(jsonResult.toString());

		close();

	}
	public void startChat(JSONObject jsonObject) {
		
		this.chatName = jsonObject.getString("chatName");
		roomManager.roomRecord.get(chatName).clients.add(this);

	}
	public void chatExit() {
		
		
		

		JSONObject jsonResult = new JSONObject();


		//jsonResult.put("message", );





		send(jsonResult.toString());

		close();

	}
	

	
	public void removeRoom(JSONObject jsonObject) {

		int chatNo = Integer.parseInt(jsonObject.getString("chatNo"));
		JSONObject jsonResult = new JSONObject();
		
		jsonResult.put("message", "해당번호 채팅방이 존재하지 않습니다.");
		
			for(Room room : roomManager.rooms) {
				if(room.no == chatNo) {
					jsonResult.put("message", room.title+" 방을 삭제했습니다.");
					roomManager.destroyRoom(room);
					break;
				}

			}
		
		send(jsonResult.toString());

		close();

	}
	public void chatCreate(JSONObject jsonObject) {

		String chatRoomName = jsonObject.getString("chatRoomName");
		JSONObject jsonResult = new JSONObject();
		
		jsonResult.put("statusCode", "0");
		System.out.println(room);
		if (room == null) {
            roomManager.createRoom(chatRoomName,this);
            System.out.println("[채팅서버] 채팅방 개설 " );
            System.out.println("[채팅서버] 현재 채팅방 갯수 " + roomManager.rooms.size());
        }
	
		
		jsonResult.put("message", chatRoomName	+ " 채팅방이 생성되었습니다.");


		send(jsonResult.toString());

		close();
	}
	public void chatEnter(JSONObject jsonObject) {

		int chatNo = Integer.parseInt(jsonObject.getString("chatNo"));
		this.chatName = jsonObject.getString("data");
		String chatTitle;
		JSONObject jsonResult = new JSONObject();

		jsonResult.put("statusCode", "-1");
		jsonResult.put("message", "해당번호 채팅방이 존재하지 않습니다.");
		//if(room == null) {
			for(Room room : roomManager.rooms) {
				if(room.no == chatNo) {
					jsonResult.put("statusCode", "0");
					jsonResult.put("message", chatNo+"번 방에 입장했습니다.");
					this.room=room;
					room.entryRoom(this);
					jsonResult.put("chatTitle", room.title);
					break;
				}

			}
		//}
		//else {
		//	jsonResult.put("message", "이미 입장한 방이 있습니다.");
		//}



		send(jsonResult.toString());

		close();
	}
	private void updateMember(JSONObject jsonObject) {
		Member member = new Member(jsonObject);

		JSONObject jsonResult = new JSONObject();
		
		jsonResult.put("statusCode", "-1");
		jsonResult.put("message", "로그인 아이디가 존재하지 않습니다");
		
		try {
			chatServer.memberRepository.updateMember(member);
			jsonResult.put("statusCode", "0");
			jsonResult.put("message", "회원정보수정이 정상으로 처리되었습니다");
		} catch (Member.NotExistUidPwd e) {
			e.printStackTrace();
		}

		send(jsonResult.toString());
		
		close();
		
	}

	private void login(JSONObject jsonObject) {
		String uid = jsonObject.getString("uid");
		String pwd = jsonObject.getString("pwd");
		JSONObject jsonResult = new JSONObject();
		
		jsonResult.put("statusCode", "-1");
		jsonResult.put("message", "로그인 아이디가 존재하지 않습니다");
		
		try {
			Member member = chatServer.findByUid(uid);
			if (null != member && pwd.equals(member.getPwd())) {
				jsonResult.put("statusCode", "0");
				jsonResult.put("message", "로그인 성공");
			}
		} catch (Member.NotExistUidPwd e) {
			e.printStackTrace();
		}

		send(jsonResult.toString());
		
		close();
	}
	
	private void passwdSearch(JSONObject jsonObject) {
		String uid = jsonObject.getString("uid");
		JSONObject jsonResult = new JSONObject();
		
		jsonResult.put("statusCode", "-1");
		jsonResult.put("message", "로그인 아이디가 존재하지 않습니다");
		
		try {
			Member member = chatServer.findByUid(uid);
			if (null != member) {
				jsonResult.put("statusCode", "0");
				jsonResult.put("message", "비밀번호 찾기 성공");
				jsonResult.put("pwd", member.getPwd());
			}
		} catch (Member.NotExistUidPwd e) {
			e.printStackTrace();
		}
				
		send(jsonResult.toString());
		
		close();
	}
	
	//메소드: JSON 보내기
	public void send(String json) {
		try {
			dos.writeUTF(json);
			dos.flush();
		} catch(IOException e) {
		}
	}	
	//메소드: 연결 종료
	public void close() {
		try { 
			socket.close();
		} catch(Exception e) {}
	}
}