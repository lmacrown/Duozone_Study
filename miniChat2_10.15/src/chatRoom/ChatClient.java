package chatRoom;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.json.JSONObject;

public class ChatClient extends ChatClient_member  {
	//필드
	static Socket socket;
	DataInputStream dis;
	DataOutputStream dos;
	String chatName;

	//채팅방 UI
	public static void clientUi(int layer) {
			if(layer == 0) {
				System.out.println();
				System.out.println("1. 로그인");
				System.out.println("2. 회원가입");
				System.out.println("3. 비밀번호검색");
				System.out.println("4. 회원정보수정");
				System.out.println("q. 프로그램 종료");
				System.out.print("메뉴 선택 => ");
			}
			else if(layer == 1) {
				System.out.println();
				System.out.println("1. 채팅방 목록");
				System.out.println("2. 채팅방 생성");
				System.out.println("3. 채팅방 입장");
				System.out.println("4. 채팅방 삭제");
				System.out.println("q. 로그 아웃");
				System.out.print("메뉴 선택 => ");
			}
			else if(layer == 2) {
				System.out.println();
				System.out.println("1. 채팅 로그 출력");
				System.out.println("2. 메세지 입력");
				System.out.println("3. 파일전송");
				System.out.println("4. 파일목록 조회");
				System.out.println("q. 채팅방 나가기");
				System.out.print("메뉴 선택 => ");
			}
		}
	
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
					String clientIp = root.getString("clientIp");
					String chatName = root.getString("chatName");
					String message = root.getString("message");
					System.out.println("<" + chatName + "@" + clientIp + "> " + message);
				}
			} catch(Exception e1) {
				System.out.println("[클라이언트] 서버 연결 끊김");
				System.exit(0);
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
	
	//메소드 : 채팅방 생성
	public void chatCreate(Scanner scanner) {
		try {
			String chatRoomName;
			System.out.println("생성할 채팅방 이름: ");
			chatRoomName = scanner.nextLine();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("command", "chatCreate");
			jsonObject.put("chatRoomName", chatRoomName);
			String json = jsonObject.toString();
			send(json);
			
			chatCreateResponse();
			disconnect();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//메소드 : 채팅방 생성 확인
	public void chatCreateResponse() throws Exception {

		String json = dis.readUTF();
		JSONObject root = new JSONObject(json);
		String statusCode = root.getString("statusCode");
		String message = root.getString("message");
		if(statusCode.equals("0")) {
			connect();
		}

		System.out.println(message);
	}
	//메소드 : 채팅방 입장
	public boolean chatEnter(Scanner scanner) {
		try {
			String select;
			System.out.println("입장할 채팅방 번호: ");
			select = scanner.nextLine();
			boolean isEnter;
			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("command", "chatEnter");
			jsonObject.put("chatNo", select);
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
	//메소드 : 채팅방 입장 확인
	public boolean chatEnterResponse() throws Exception {
		String json = dis.readUTF();
		JSONObject root = new JSONObject(json);
		
		String statusCode = root.getString("statusCode");
		String message = root.getString("message");

		if (statusCode.equals("0")) {
			System.out.println(message);
			return true;
	
		} else {
			System.out.println(message);
			return false;
			}
	}
	

	//메소드: 메인
	public static void main(String[] args) {		
		try {			
			ChatClient chatClient = new ChatClient();
			boolean stop = false;
			boolean isMember = false;
			boolean isEnter = false;
			int layer= 0;
			Scanner scanner = new Scanner(System.in);
			while(false == stop && isMember == false) {

				clientUi(layer);

				String menuNum = scanner.nextLine();
				switch(menuNum) {
				case "1":
				{
					chatClient.login(scanner);
					break;
				}
				case "2":
					chatClient.registerMember(scanner);
					break;
				case "3":
					chatClient.passwdSearch(scanner);
					break;
				case "4":
					chatClient.updateMember(scanner);
					break;
				case "Q", "q":
					scanner.close();
				stop = true;
				System.out.println("프로그램 종료됨");
				break;
				}
			}
			layer++;
			
			while(!isEnter) {
				clientUi(layer);
				chatClient.connect();//채팅방 연결
				String menuNum = scanner.nextLine();
				switch(menuNum) {
				case "1":{//채팅방 목록
					
					break;
				}
				
				
				chatClient.chatName = scanner.nextLine();
				
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("command", "incoming");
				jsonObject.put("data", chatClient.chatName);
				String json = jsonObject.toString();
				chatClient.send(json);
				
				case "2":{//채팅방 생성
					chatClient.chatCreate(scanner);
					break;
				}
				case "3":{//채팅방 입장
					isEnter = chatClient.chatEnter(scanner);
					break;
				}
				case "4":{//채팅방 삭제

					break;
				}
				case "Q", "q":
				{
					scanner.close();
					isMember= false;
					layer--;
					System.out.println("로그아웃");
					break;
				}
				}
			}
			
			layer++;
			
			while(isEnter) {
				clientUi(layer);
				String menuNum = scanner.nextLine();
				switch(menuNum) {
				case "1":{//채팅 로그 출력
					Map<String, Integer> map = new HashMap< >();
					
					chatClient.messageOutPut(map.get(/*채팅방 이름 파일 value값*/));
					break;
				}
				case "2":{//메시지 입력
					chatClient.chat();
					break;
				}
				case "3":{//파일전송
					InetSocketAddress isa = (InetSocketAddress) socket.getRemoteSocketAddress();//ip주소 받을 객체 생성
					String serverIP= isa.getHostName();//ip주소 받음
					
					System.out.println("보낼 파일 이름 입력");
					String DATA = "C:\\temp\\"+scanner.next();
					File FileName = new File(DATA);
					
					chatClient.fileTrasfer(serverIP, 50001 ,FileName);/*포트번호 50001*/
					break;
					//getHostName
				}
				case "4":{//파일목록 조회
					chatClient.FileListOutput();
					break;
				}
				case "Q", "q":
				{
					scanner.close();
					isEnter= false;
					layer--;
					System.out.println("채팅방에서 나갔습니다.");
					break;
				}
				}
			}
			//			ChatClient chatClient = new ChatClient();
			//			chatClient.connect();
			//			System.out.println("대화명 입력: ");
			//			chatClient.chatName = scanner.nextLine();
			//			
			//			JSONObject jsonObject = new JSONObject();
			//			jsonObject.put("command", "incoming");
			//			jsonObject.put("data", chatClient.chatName);
			//			String json = jsonObject.toString();
			//			chatClient.send(json);
			//			
			//			chatClient.receive();			
			//			
			//			System.out.println("--------------------------------------------------");
			//			System.out.println("보낼 메시지를 입력하고 Enter");
			//			System.out.println("채팅를 종료하려면 q를 입력하고 Enter");
			//			System.out.println("--------------------------------------------------");
			//			while(true) {
			//				String message = scanner.nextLine();
			//				if(message.toLowerCase().equals("q")) {
			//					break;
			//				} else {
			////					jsonObject = new JSONObject();
			//					jsonObject.put("command", "message");
			//					jsonObject.put("data", message);
			//					chatClient.send(jsonObject.toString());
			//				}
			//			}
			//			scanner.close();
			//			chatClient.unconnect();
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("[클라이언트] 서버 연결 안됨");
		}
	}
	

	//메시지 출력
	public void chat() {		
		try {			
			ChatClient chatClient = new ChatClient();
			
			Scanner scanner = new Scanner(System.in);
			JSONObject jsonObject = new JSONObject();
			String json = jsonObject.toString();

			String chatMessage = null;
			FileOutputStream fos = new FileOutputStream("C:/Temp/");/*채팅방이름 추가*/
			DataOutputStream dos = new DataOutputStream(fos);
			
			while(true) {
				String message = scanner.nextLine();
				if(message.toLowerCase().equals("q")) {
					break;
				} else {					
					jsonObject.put("command", "message");
					jsonObject.put("data", message);
					json = jsonObject.toString();
					chatClient.send(json);
					//메시지 파일에 저장
					jsonObject.put(chatMessage, message);
					dos.writeUTF(chatMessage);
				}
			}
			scanner.close();
		} catch(IOException e) {
			System.out.println("채팅 서버 연결 안됨");
		}
	}
	//파일전송
	private void fileTrasfer(String serverIP, int port, File fileName) {
		String files=fileName.getName();
		File file = new File(files);
		if(!file.exists()) {
			System.out.println("파일 없음");
			System.exit(0);
		}
		long fileSize=file.length();
		long totalReadBytes =0; 
		byte[] buffer = new byte[10000];
		int readBytes;
		double startTime =0;
		try {
			FileInputStream fis = new FileInputStream(file);
			Socket socket = new Socket(serverIP, port);//"localhost", 50001
			if(!socket.isConnected()) {
				System.out.println("소켓 연결 오류");
				System.exit(0);
			}
			startTime = System.currentTimeMillis();
			OutputStream os = socket.getOutputStream();
			while((readBytes=fis.read(buffer))>0) {
				os.write(buffer, 0, readBytes);
				totalReadBytes += readBytes;
				System.out.println("In progress : "+totalReadBytes+"/"
						+fileSize +" Byte(s) ("+(totalReadBytes*100/fileSize)+"%)");
			}
			System.out.println("파일 변환 완료");
			fis.close();
			os.close();
			socket.close();
		}catch(UnknownHostException e) {
			e.printStackTrace();
			
		}catch(IOException e){
			e.printStackTrace();
		}
		double endTime = System.currentTimeMillis();
		double diffTime =(endTime - startTime)/1000;
		double transferSpeed =(fileSize/1000)/diffTime;
		
		System.out.println("time : "+diffTime+"second(s)");
		System.out.println("Average transfer speed : "+transferSpeed+"KB/s");
	}
	//파일리스트 출력
	private void FileListOutput() {
		String DATA = "C:\\temp";
		File dir = new File(DATA);
		// contains() : 파일 이름에 "%"값이 붙은것만 필터링
		String[] fileNames = dir.list();
		for (String filename : fileNames) {
			System.out.println("filename : " + filename);
		}
		
	}
	//파일 로그 출력
	private void messageOutPut(File fileName) throws FileNotFoundException {
		String files=fileName.getName();
		File file = new File(files);
		Scanner scan = new Scanner(file);
		while(scan.hasNextLine())
			System.out.println(scan.nextLine());
	}
}


	