package socketproject;

import java.util.Scanner;

import org.json.JSONObject;

public class ClientControlMember extends ChatClient {

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
	
	
}
