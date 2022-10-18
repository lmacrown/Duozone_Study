package socketproject;

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
			System.out.println("q. 채팅방 나가기");
			System.out.print("메뉴 선택 => ");
		}
	}


	public static void main(String[] args) {
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
					//chatClient.memberClient.registerMember(scanner);
					break;
				case "3":
					memberClient.passwdSearch(scanner);
					break;
				case "4":
					memberClient.updateMember(scanner);
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
				case "1":

					break;

				case "2":

					chattingClient.sendMessage(scanner);
					break;


				case "3":

					break;
				case "4":

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
