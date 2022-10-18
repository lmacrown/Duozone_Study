package ch19.sec14;

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
			String MEMBER_FILE_NAME = "c:\\temp\\member.db";
			memberList.add(Member.builder()
					.uid("0")
					.pwd("0")
					.name("홍길동")
					.sex("M")
					.phone("010-1234-1234")
					.address("혜화동")
					.build());
			for(int i=1;i<10;i++) {
				memberList.add(Member.builder()
						.uid("userid" + i)
						.pwd("pwd" + i)
						.name("홍길동" + i)
						.sex(i % 2 == 0 ? "F" : "M")
						.phone("010-1234-123" + i)
						.address("혜화동" + i)
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