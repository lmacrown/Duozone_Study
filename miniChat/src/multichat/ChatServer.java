package multichat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;

public class ChatServer {
	// 필드
	ServerSocket serverSocket;
	ExecutorService threadPool = Executors.newFixedThreadPool(100);
	MemberRepository memberRepository = new MemberRepository();

	RoomManager roomManager = new RoomManager();

	// 메소드: 서버 시작
	public void start() throws IOException {

		// 멤버 파일 생성
		String MEMBER_FILE_NAME = "C:\\temp\\mamber.db";
		File filepaths = new File("C:\\temp");
		if (!filepaths.exists()) {
			filepaths.mkdir();
		}
		File file = new File(MEMBER_FILE_NAME);
		ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
		memberRepository.loadMember();

		serverSocket = new ServerSocket(50001);
		System.out.println("[서버] 시작됨");
//
//		try {
//			ObjectInputStream in = new ObjectInputStream(
//					new BufferedInputStream(new FileInputStream(MEMBER_FILE_NAME)));
//			in.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		

		Thread thread = new Thread(() -> {
			try {
				while (true) {
					Socket socket = serverSocket.accept();
					SocketClient sc = new SocketClient(this, socket, roomManager);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		thread.start();
	}

	// 메시지 보내기
	public void sendMessage(SocketClient sender, String message) throws Exception {
		JSONObject root = new JSONObject();
		root.put("chatName", sender.chatName);
		// 출력 파일 생성
		String chatTitle = roomManager.loadRoom(sender.chatName).title;
		FileWriter filewriter = new FileWriter("C:/Temp/" + chatTitle + ".db", true);
		filewriter.write(message);
		filewriter.flush();
		filewriter.write("\n");
		filewriter.close();

		if (message.indexOf("@") == 0) {
			int pos = message.indexOf(" ");
			String key = message.substring(1, pos);
			for (SocketClient c : sender.room.clients) {
				if (key.equals(c.chatName)) {
					message = "(귀속말)  " + message.substring(pos + 1);
					root.put("message", message);
					String json = root.toString();
					c.send(json);
				}
			}

		} else {
			sender.sendWithOutMe(message);
		}
	}

	// 메소드: 서버 종료
	public void stop() {
		try {
			serverSocket.close();
			threadPool.shutdownNow();
			System.out.println("[서버] 종료됨 ");
		} catch (IOException e1) {
		}
	}

	public synchronized void registerMember(Member member) throws Exception {
		try {
			memberRepository.insertMember(member);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void deleteMemberInfo(Member member) throws Exception {
		try {
			memberRepository.memberDelete(member);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized Member findByUid(String uid) throws Member.NotExistUidPwd {
		return memberRepository.findByUid(uid);
	}

	public synchronized void selectMember(Member member) throws Exception {
		try {
			memberRepository.memberInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 메소드: 메인
	public static void main(String[] args) {
		try {
			ChatServer chatServer = new ChatServer();
			chatServer.start();

			System.out.println("----------------------------------------------------");
			System.out.println("[종료커맨드 : 'q' or 'Q']");
			System.out.println("----------------------------------------------------");

			Scanner scanner = new Scanner(System.in);
			while (true) {
				String key = scanner.nextLine();
				if (key.equals("q"))
					break;
			}
			scanner.close();
			chatServer.stop();
		} catch (IOException e) {
			System.out.println("[서버] " + e.getMessage());
		}
	}
}