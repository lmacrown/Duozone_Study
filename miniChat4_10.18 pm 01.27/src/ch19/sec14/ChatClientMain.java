package ch19.sec14;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class ChatClientMain {


	public void logout() {

	}


	public void exit() {

	}
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
			System.out.println("3. 파일 전송");
			System.out.println("4. 파일목록 조회");
			System.out.println("5. 파일 다운");
			System.out.println("q. 채팅방 나가기");
			System.out.print("메뉴 선택 => ");
		}
	}


	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		ChatClient chatClient = new ChatClient();
		boolean stop = false;
		boolean isMember = false;
		boolean isEnter = false;
		int layer= 0;
		Scanner scanner = new Scanner(System.in);
		while(!stop) {
			while(!stop && !isMember) {

				clientUi(layer);

				String menuNum = scanner.nextLine();
				switch(menuNum) {
				case "1":
					isMember = chatClient.login(scanner);
					if(isMember)
						layer++;
					break;

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
			while(isMember && !isEnter) {
				clientUi(layer);
				String menuNum = scanner.nextLine();
				switch(menuNum) {
				case "1"://채팅방 목록
					chatClient.chatList();
					break;
				case "2"://채팅방 생성
					chatClient.chatCreate(scanner,chatClient);
					break;
				case "3"://채팅방 입장
					isEnter = chatClient.chatEnter(scanner);
					if(isEnter)
						layer++;
					break;
				case "4"://채팅방 삭제
					chatClient.removeRoom(scanner);
					break;
				case "Q", "q":
					layer--;
				System.out.println("로그아웃");
				isMember= false;


				break;

				}
			}

			//함주호
			while(isEnter) {
				clientUi(layer);
				String menuNum = scanner.nextLine();
				
				switch(menuNum) {
				case "1"://채팅 로그 출력"
					chatClient.printChatLog();
					break;
				case "2"://메세지 입력"
					chatClient.sendMessage(scanner);
					break;
				case "3"://파일 전송
					chatClient.fileTrasfer(scanner);/* 포트번호 50001 */
					break;
				case "4"://파일 목록 조회
					chatClient.fileListOutput();
					break;
				case "5"://파일 받기
					chatClient.fileReceive(scanner);
					break;
//				case "5"://파일 받기
//					System.out.println("파일 다운로드 : ");
//					fileName = "C:\\temp\\" + scanner.next();
//					FileName = new File(fileName);
//
//					if(FileName.exists())
//						chatClient.fileReceive("localhost", 50001, FileName);
//					else
//						System.out.println("파일이 존재하지 않습니다");
//					
//					int pos = fileName.lastIndexOf(".");
//					if(fileName.substring(pos+1).equals("jpg"))
//						chatClient.imageRead();
//					break;
					
				case "Q", "q":
					isEnter= false;
				layer--;
				System.out.println("채팅방에서 나갔습니다.");
				break;

				}
			}
		}

	} 

}
