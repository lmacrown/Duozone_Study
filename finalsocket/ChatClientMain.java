package finalsocket;

import java.util.Scanner;

public class ChatClientMain {
	
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
			System.out.println("5. 파일 다운");
			System.out.println("q. 채팅방 나가기");
			System.out.print("메뉴 선택 => ");
		}
	}


	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		ClientControlMember memberClient = new ClientControlMember();
		ClientControlChat chattingClient = new ClientControlChat();
		ClientControlFile fileClient = new ClientControlFile();
		
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
					isMember = memberClient.login(scanner);
					if(isMember)
						layer++;
					break;

				case "2":
					memberClient.registerMember(scanner); //회원가입
					break;
				case "3":
					memberClient.passwdSearch(scanner); //비밀번호찾기
					break;
				case "4":
					memberClient.updateMember(scanner); //회원정보수정
					break;
				case "5":
					memberClient.memberDelete(scanner); //회원탈퇴
					break;
				case "6":
					memberClient.memberInfo(scanner); //전체회원목록
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
				case "1":
					chattingClient.chatList();

					break;

				case "2":
					chattingClient.chatCreate(scanner);
					break;
				case "3":
					isEnter = chattingClient.chatEnter(scanner);
					if(isEnter)
						layer++;
					break;
				case "4":
					chattingClient.removeRoom(scanner);
					break;
				case "Q", "q":
					layer--;
				System.out.println("로그아웃");
				isMember= false;
				break;

				}
			}

			

			while(isEnter) {
				clientUi(layer);
				String menuNum = scanner.nextLine();
				switch(menuNum) {
				case "1"://채팅 로그 출력"
					fileClient.printChatLog();
					break;
				case "2"://메세지 입력"
					chattingClient.sendMessage(scanner);
					break;
				case "3"://파일 전송
					fileClient.fileTrasfer(scanner);/* 포트번호 50001 */
					break;
				case "4"://파일 목록 조회
					fileClient.fileListOutput();
					break;
				case "5"://파일 받기
					fileClient.fileReceive(scanner);
					break;
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
