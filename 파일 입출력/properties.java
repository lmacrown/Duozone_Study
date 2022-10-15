package ch18.sec02.exam01;

import java.io.File;
import java.io.IOException;

public class properties {

	public static void main(String[] args) throws IOException {
		File f = new File("C:\\temp\\test1.txt");

		String fileName = f.getName();
		int pos = fileName.lastIndexOf(".");

		System.out.println("경로를 제외한 파일 이름 : " + f.getName());
		System.out.println("확장자를 제외한 파일 이름 : " + fileName.substring(0, pos));
		System.out.println("확장자 : " + fileName.substring(pos + 1));

	}
}