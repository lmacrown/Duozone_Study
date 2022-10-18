package finalsocket;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MemberTestData {
	//메소드: 메인
	@SuppressWarnings("null")
	public static void main(String[] args) {	
		try {
			List<Member> memberList = new ArrayList<>();
			String MEMBER_FILE_NAME = "C:\\Users\\KOSA\\git\\repository\\javaproject\\member.txt";
			for(int i=0;i<10;i++) {
				memberList.add(Member.builder()
						.uid("userid" + i)
						.pwd("pwd" + i)
						.name("홍길동" + i)
						.build());
			}
			File file = new File(MEMBER_FILE_NAME);
			ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			out.writeObject(memberList);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}