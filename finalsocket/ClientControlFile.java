package finalsocket;

import java.awt.BorderLayout;
import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.json.JSONObject;

public class ClientControlFile extends ChatClient {
	// 채팅 로그 출력
	public void printChatLog() throws Exception {
		connect();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("command", "chatlog");
		// jsonObject.put(chatName, false);
		send(jsonObject.toString());
		ChatLogReceive();
		disconnect();
	}

	public void ChatLogReceive() throws Exception {
		String json = dis.readUTF();
		JSONObject root = new JSONObject(json);
		String message = root.getString("chatLogReceive");
		System.out.print("[채팅 로그]  \n" + message);
	}
	// 파일전송
	public void fileTrasfer(Scanner scan) throws Exception {
		// 파일이름
		System.out.println("파일 이름을 입력하시오");
		String transFileName = scan.next();
		File filename = new File("C:\\temp\\" + transFileName);

		if (!filename.exists()) {
			System.out.println("파일 없음");
			return;
		}

		byte[] arr = new byte[(int) filename.length()];
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

	// 파일 받기
	public void fileReceive(Scanner scan) throws Exception {
		System.out.println("파일 이름을 입력하시오");
		String transFileName = scan.next();
		File filename = new File("C:\\temp\\" + transFileName);

		if (!filename.exists()) {
			System.out.println("파일 없음");
			return;
		}

		byte[] arr = new byte[(int) filename.length()];
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filename));
		out.write(arr);
		out.close();

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("command", "fileRe");
		jsonObject.put("filename", filename.getName());
		jsonObject.put("filetrans", new String(Base64.getDecoder().decode(arr)));// 수정

		String json = jsonObject.toString();
		connect();
		send(json);
		//이미지 보기
		String fileNames = filename.getName();
		int pos = fileNames.lastIndexOf(".");
		String proper = fileNames.substring(pos + 1);
		System.out.println("확장자 : " + proper);
		if(proper.equals("jpg")) 
			imageRead(fileNames);
		
		System.out.println("파일 받기 완료");
	}

	// 파일리스트 출력
	public void fileListOutput() throws Exception {
		connect();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("command", "fileList");
		String json = jsonObject.toString();

		send(json);
		fileListOutputRe();

		disconnect();
	}
	public void fileListOutputRe() throws Exception {
		String json = dis.readUTF();
		JSONObject root = new JSONObject(json);
		String message = root.getString("fileListOutputReceive");
		System.out.print("[파일 리스트]  \n" + message);
	}

	// 파일 로그 출력
	public void messageOutput() throws IOException {
		connect();
		String json = dis.readUTF();
		JSONObject root = new JSONObject(json);
		String chatTitle = root.getString("chatTitle");

		String Data = "C:/Temp/" + chatTitle + ".db";
		File file = new File(Data);
		Scanner scan = new Scanner(file);
		while (scan.hasNextLine())
			System.out.println(scan.nextLine());
		disconnect();
	}

	// 이미지 파일 불러오기
	public void imageRead(String filename) throws Exception {
		Image image1 = null;
		Image image2 = null;
		// 파일로부터 이미지 읽기
		File sourceimage = new File("c:\\temp\\"+filename);
		image1 = ImageIO.read(sourceimage);

		// InputStream으로부터 이미지 읽기
		InputStream is = new BufferedInputStream(new FileInputStream("c:\\temp\\"+filename));
		image2 = ImageIO.read(is);

		JFrame frame = new JFrame();

		JLabel label1 = new JLabel(new ImageIcon(image1));
		JLabel label2 = new JLabel(new ImageIcon(image2));

		frame.getContentPane().add(label1, BorderLayout.CENTER);
		frame.getContentPane().add(label2, BorderLayout.NORTH);

		frame.pack();
		frame.setVisible(true);
	}
}
