package finalsocket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



public class MemberRepository {
	List<Member> memberList = null;
	Map<String, Member> memberMap = Collections.synchronizedMap(new HashMap<>());
	private static final String MEMBER_FILE_NAME = "C:\\Users\\KOSA\\git\\repository\\javaproject\\member.txt";

	public void loadMember() {
		try {
			File file = new File(MEMBER_FILE_NAME);
			if (file.exists() && file.length() != 0) {
				ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
				memberList = (List<Member>) in.readObject();
				memberMap = memberList.stream().collect(Collectors.toMap(m -> m.getUid(), m -> m));
				in.close();
			} else {
				memberList = new ArrayList<>();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveMember() {
		try {
			File file = new File(MEMBER_FILE_NAME);
			ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			out.writeObject(memberList);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void insertMember(Member member) throws Exception {
//		if (true == memberList.stream().anyMatch(m -> member.getUid().equals(m.getUid()))) {
//			throw new Member.ExistMember("[" + member.getUid() + "] 아이디가 존재합니다");
//		}
//		memberList.add(member);
//		memberMap.put(member.getUid(), member);
//		saveMember();
		if (this.memberCheck(member)) {
			this.memberList.add(member);
			this.memberMap.put(member.getUid(), member);
			saveMember();
		} else {
			throw new Exception("이미 존재하는 회원입니다.");
		}
	}

	// 중복회원 검사 메소드
	private boolean memberCheck(Member m) {
		boolean result = true;

		// 기존회원의 정보와 전달받은 member객체 비교
		for (Member member : this.memberList) {
			if (member.getUid().equals(m.getUid()) && member.getName().equals(m.getName())
					&& member.getPwd().equals(m.getPwd())) {
				result = false;
			}
		}
		return result;
	}

	public synchronized Member findByUid(String uid) throws Member.NotExistUidPwd {

		Member findMember = memberMap.get(uid);
		if (null == findMember) {
			throw new Member.NotExistUidPwd("[" + uid + "] 아이디가 존재하지 않습니다");
		}
		return findMember;
	}

	public synchronized void updateMember(Member member) throws Member.NotExistUidPwd {
		// 배열에서 위치를 찾는다
		int index = memberList.indexOf(member);
		if (-1 == index) {
			throw new Member.NotExistUidPwd("[" + member.getUid() + "] 아이디가 존재하지 않습니다");
		}
		// 배열의 내용을 수정 한다
		memberList.set(index, member);
		memberMap.put(member.getUid(), member);

		saveMember();
	}

	public void memberDelete(Member m) throws Exception {
		try {
			this.memberList.remove(m);
			String key = m.getUid();
			this.memberMap.remove(key, m);
			saveMember();
//			List<Member> member = this.memberList;
//			if (((Member) member).getUid().equals(m.getUid()) && ((Member) member).getPwd().equals(m.getPwd())) {
//				this.memberList.remove(m);
//				this.memberMap.remove(m.getUid(), m);
//				saveMember();
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
//회원관리
	public void memberInfo() {
		this.memberPrint(memberList);
	}

//회원관리
	public void memberPrint(List<Member> member) {

		System.out.printf("총:%s건%n", member.size());
		System.out.println("[명단 출력]");
		for (Member m : member) {
			System.out.printf("%s%n", "ID:" + m.getUid() + " " + "NAME:" + m.getName() + " " + "PW:" + m.getPwd());
		}

		System.out.println();
	}

}
