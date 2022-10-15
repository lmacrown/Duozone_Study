package chatRoom;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import org.json.JSONObject;

import lombok.experimental.SuperBuilder;


public class ChatClient_member {
		ChatClient chatClient;

		//메소드 : 로그인
		void login(Scanner scanner) {
			try {
				String uid;
				String pwd;
				boolean result = false;
				System.out.println("\n1. 로그인 작업");
				System.out.print("아이디 : ");
				uid = scanner.nextLine();
				System.out.print("비밀번호 : ");
				pwd = scanner.nextLine();
//				//아디 비번 맞으면 연결
//				if(uid.equals(pwd) && pwd.equals(pwd)) {
//					connect();
//					clientUi(1);
//				}
				
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("command", "login");
				jsonObject.put("uid", uid);
				jsonObject.put("pwd", pwd);

				System.out.println("jsonObject = " + jsonObject.toString());
				chatClient.send(jsonObject.toString());


				chatClient.disconnect();
				loginResponse();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//메소드 : 로그인 확인
		void loginResponse() throws Exception {
			
			String json = chatClient.dis.readUTF();
			JSONObject root = new JSONObject(json);
			String statusCode = root.getString("statusCode");
			String message = root.getString("message");

			if (statusCode.equals("0")) {
				System.out.println("로그인 성공");
			} else {
				System.out.println(message);
			}
		}
		//메소드 : 회원 가입
		void registerMember(Scanner scanner) {

		}
		//메소드 : 비번 검색
		void passwdSearch(Scanner scanner) {
			try {
				String uid;

				System.out.println("\n3. 비밀번호 찾기");
				System.out.print("아이디 : ");
				uid = scanner.nextLine();

				chatClient.connect();

				JSONObject jsonObject = new JSONObject();
				jsonObject.put("command", "passwdSearch");
				jsonObject.put("uid", uid);
				String json = jsonObject.toString();
				chatClient.send(json);

				passwdSearchResponse();

				chatClient.disconnect();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//메소드 : 비번 검색 확인
		void passwdSearchResponse() throws Exception {
			String json = chatClient.dis.readUTF();
			JSONObject root = new JSONObject(json);
			String statusCode = root.getString("statusCode");
			String message = root.getString("message");

			if (statusCode.equals("0")) {
				System.out.println("비밀번호 : " + root.getString("pwd"));
			} else {
				System.out.println(message);
			}
		}
		//메소드 : 회원 정보 수정
		void updateMember(Scanner scanner) {
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

				chatClient.connect();

				JSONObject jsonObject = new JSONObject();
				jsonObject.put("command", "updateMember");
				jsonObject.put("uid", uid);
				jsonObject.put("pwd", pwd);
				jsonObject.put("name", name);
				jsonObject.put("sex", sex);
				jsonObject.put("address", address);
				jsonObject.put("phone", phone);
				String json = jsonObject.toString();
				chatClient.send(json);

				updateMemberResponse();

				chatClient.disconnect();

			} catch (Exception e) {
				e.printStackTrace();
			}		
		}
		//메소드 : 회원 정보 수정 확인
		void updateMemberResponse() throws Exception {
			String json = chatClient.dis.readUTF();
			JSONObject root = new JSONObject(json);
			String statusCode = root.getString("statusCode");
			String message = root.getString("message");

			if (statusCode.equals("0")) {
				System.out.println("정상적으로 수정되었습니다");
			} else {
				System.out.println(message);
			}
		}


}

