package multichat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Base64;
import java.util.Scanner;

import org.json.JSONObject;

public class SocketClient {
	// 필드
	ChatServer chatServer;
	Socket socket;
	DataInputStream dis;
	DataOutputStream dos;
	String clientIp;
	String chatName;
	Room room;
	RoomManager roomManager;
	static String chatTitle;

	// 생성자
	public SocketClient(ChatServer chatServer, Socket socket, RoomManager roomManager) throws Exception {
		try {
			this.chatServer = chatServer;
			this.socket = socket;
			this.dis = new DataInputStream(socket.getInputStream());
			this.dos = new DataOutputStream(socket.getOutputStream());
			InetSocketAddress isa = (InetSocketAddress) socket.getRemoteSocketAddress();
			this.clientIp = isa.getHostName();
			this.roomManager = roomManager;

			receive();
		} catch (IOException e) {
		}
	}

	// 메소드: JSON 받기
	public void receive() throws Exception {
		chatServer.threadPool.execute(() -> {
			try {
				boolean stop = false;

				while (true != stop) {
					String receiveJson = dis.readUTF();

					JSONObject jsonObject = new JSONObject(receiveJson);
					String command = jsonObject.getString("command");

					switch (command) {
					case "login":
						login(jsonObject);
						stop = true;
						break;
					case "registerMember":
						registerMember(jsonObject);
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
					case "memberDelete":
						memberDelete(jsonObject);
						stop = true;
						break;
					case "memberInfo":
						memberInfo(jsonObject);
						stop = true;
						break;
					case "message":
						String message = jsonObject.getString("data");
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
					case "chatrm":
						removeRoom(jsonObject);
						stop = true;
						break;
					case "endchat":
						this.sendWithOutMe("님이 나갔습니다.");
						stop = true;
						break;
					case "chatstart":
						startChat(jsonObject);
						break;
					case "chatlog":
						printChatLog(jsonObject);
						break;
					case "fileTran":
						fileTran(jsonObject);
						stop = true;
						break;
					case "fileList":
						fileListOutput(jsonObject);
						stop = true;
						break;
					case "fileRe":
						fileReceive(jsonObject);
						stop = true;
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	// 회원정보목록
	private void memberInfo(JSONObject jsonObject) {
		Member member = new Member(jsonObject);
		JSONObject jsonResult = new JSONObject();

		jsonResult.put("statusCode", "-1");
		jsonResult.put("message", "로그인 아이디가 존재하지 않습니다");

		try {
			chatServer.findByUid(member.getUid());
			chatServer.selectMember(member);
			jsonResult.put("statusCode", "0");
			jsonResult.put("message", "회원가입이 완료되었습니다.");
		} catch (Exception e) {
			e.printStackTrace();
		}

		send(jsonResult.toString());

		close();

	}

	private void memberDelete(JSONObject jsonObject) {
		Member member = new Member(jsonObject);

		JSONObject jsonResult = new JSONObject();
		jsonResult.put("statusCode", "-1");
		jsonResult.put("message", "로그인 아이디가 존재하지 않습니다");

		try {
			// chatServer.memberRepository.memberDelete(member);
			chatServer.findByUid(member.getUid());
			chatServer.deleteMemberInfo(member);
			jsonResult.put("statusCode", "0");
			jsonResult.put("message", "회원탈퇴가 정상적으로 이루어졌습니다.");

			send(jsonResult.toString());

			close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void registerMember(JSONObject jsonObject) {
		Member member = new Member(jsonObject);

		JSONObject jsonResult = new JSONObject();

		jsonResult.put("statusCode", "-1");
		jsonResult.put("message", "로그인 아이디가 존재하지 않습니다");

		try {

			chatServer.registerMember(member);
			jsonResult.put("statusCode", "0");
			jsonResult.put("message", "회원가입이 완료되었습니다.");
		} catch (Exception e) {
			e.printStackTrace();
		}

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
				jsonResult.put("uid", uid);
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

	public void chatList() {

		String roomStatus;

		roomStatus = "[room status]\n";
		if (roomManager.rooms.size() > 0) {
			for (Room room : roomManager.rooms) {
				roomStatus += String.format("{no : %s, title : %s}\n", room.no, room.title);
			}
			roomStatus = roomStatus.substring(0, roomStatus.length() - 1);
		}

		JSONObject jsonResult = new JSONObject();

		jsonResult.put("message", roomStatus);

		send(jsonResult.toString());

		close();

	}

	public void startChat(JSONObject jsonObject) {

		this.chatName = jsonObject.getString("chatName");
		this.room = roomManager.loadRoom(this.chatName);
		this.sendWithOutMe("님이 들어오셨습니다.");
		room.clients.add(this);

	}

	public void sendWithOutMe(String message) {
		JSONObject root = new JSONObject();
		root.put("chatName", this.chatName);
		for (SocketClient c : this.room.clients) {
			if (!c.equals(this)) {
				root.put("message", message);
				String json = root.toString();
				c.send(json);
			}
		}
	}

	public void removeRoom(JSONObject jsonObject) {

		int chatNo = Integer.parseInt(jsonObject.getString("chatNo"));
		JSONObject jsonResult = new JSONObject();

		jsonResult.put("message", "해당번호 채팅방이 존재하지 않습니다.");

		for (Room room : roomManager.rooms) {
			if (room.no == chatNo) {
				jsonResult.put("message", room.title + " 방을 삭제했습니다.");
				roomManager.destroyRoom(room);
				break;
//			}else if(room.no>chatNo) {
//				room.no-=1;
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
			roomManager.createRoom(chatRoomName, this);
			System.out.println("[채팅서버] 채팅방 개설 ");
			System.out.println("[채팅서버] 현재 채팅방 갯수 " + roomManager.rooms.size());
		}

		jsonResult.put("message", chatRoomName + " 채팅방이 생성되었습니다.");

		send(jsonResult.toString());

		close();
	}

	public void chatEnter(JSONObject jsonObject) {

		int chatNo = Integer.parseInt(jsonObject.getString("chatNo"));
		this.chatName = jsonObject.getString("data");

		JSONObject jsonResult = new JSONObject();

		jsonResult.put("statusCode", "-1");
		jsonResult.put("message", "해당번호 채팅방이 존재하지 않습니다.");

		for (Room room : roomManager.rooms) {
			if (room.no == chatNo) {
				jsonResult.put("statusCode", "0");
				jsonResult.put("message", chatNo + "번 방에 입장했습니다.");
				this.room = room;
				room.entryRoom(this);
				chatTitle = room.title;
				break;
			}

		}

		send(jsonResult.toString());

		close();
	}

	// 파일 전송
	public void fileTran(JSONObject jsonObject) throws Exception {
		JSONObject json = new JSONObject();
		String fileName = jsonObject.getString("filename");
		byte[] data = Base64.getDecoder().decode(jsonObject.getString("filetrans").getBytes());

		File filePath = new File("C:\\temp\\server");
		if (!filePath.exists()) {
			filePath.mkdir();
		}
		BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream("C:\\temp\\server\\" + fileName));
		fos.write(data);
		fos.close();
		send(json.toString());
		close();
	}

	// 파일 받기
	public void fileReceive(JSONObject jsonObject) throws Exception {
		JSONObject json = new JSONObject();
		String fileName = jsonObject.getString("filename");
		// byte[] data =
		// Base64.getDecoder().decode(jsonObject.getString("filename").getBytes());

		File filePath = new File("C:\\temp\\server\\" + fileName);
		if (!filePath.exists()) {
			System.out.println("파일 없음");
		} else {
			BufferedInputStream ios = new BufferedInputStream(new FileInputStream(filePath));
			// 받아온 파일 json에 저장 해야됨
			byte[] data = new byte[(int) filePath.length()];
			ios.read(data);
			ios.close();
			json.put("decodeFile", new String(Base64.getEncoder().encode(data)));
			send(json.toString());
		}
		close();
	}

	// 채팅 로그 출력
	public void printChatLog(JSONObject jsonObject) throws Exception {
		JSONObject json = new JSONObject();
		String chatRoom = chatTitle;
		json.put("chatTitle", chatRoom);
		System.out.println(chatRoom + " 채팅 기록");

		FileInputStream file = new FileInputStream("C:/Temp/" + chatRoom + ".db");
		Scanner scan = new Scanner(file);

		String chatms = "";
		while (scan.hasNextLine()) {
			chatms += scan.nextLine() + "\n";
		}
		json.put("chatLogReceive", chatms);
		send(json.toString());
		scan.close();
	}

	// 파일 리스트 출력
	public void fileListOutput(JSONObject jsonObject) throws Exception {
		JSONObject json = new JSONObject();
		File file = new File("C:/Temp");
		String[] fileNames = file.list();
		String fileLi = "";
		for (String filename : fileNames) {
			System.out.println("filename : " + filename);
			fileLi += filename + "\n";
		}

		json.put("fileListOutputReceive", fileLi);
		send(json.toString());
	}

	// 메소드: JSON 보내기
	public void send(String json) {
		try {
			dos.writeUTF(json);
			dos.flush();
		} catch (IOException e) {
		}
	}

	// 메소드: 연결 종료
	public void close() {
		try {
			socket.close();
		} catch (Exception e) {
		}
	}
}