package ch18.sec02.exam01;

import java.awt.BorderLayout;
import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class WriteExample {
	public static void main(String[] args) {

		WriteExample ex = new WriteExample();
		ex.FileListOutput();
		ex.porperties();
		ex.imageRead();
	}
	//파일리스트 출력
	public void FileListOutput() {
		String DATA = "C:\\temp";
		File dir = new File(DATA);
		// contains() : 파일 이름에 "%"값이 붙은것만 필터링
		String[] fileNames = dir.list();
		for (String filename : fileNames) {
			System.out.println("filename : " + filename);
		}
		
	}
	//속성값 분류
	public void porperties() {
		File f = new File("C:\\temp\\test1.txt");

		String fileName = f.getName();
		int pos = fileName.lastIndexOf(".");

		System.out.println("경로를 제외한 파일 이름 : " + f.getName());
		System.out.println("확장자를 제외한 파일 이름 : " + fileName.substring(0, pos));
		System.out.println("확장자 : " + fileName.substring(pos + 1));
	}
	//이미지 파일 불러오기
	public void imageRead() {
		Image image1 = null;
		Image image2 = null;
		
		try {
	        // 파일로부터 이미지 읽기
	        File sourceimage = new File("c:\\temp\\targetFile1.jpg");
	        image1 = ImageIO.read(sourceimage);
	    
	        // InputStream으로부터 이미지 읽기
	        InputStream is = new BufferedInputStream(
	            new FileInputStream("c:\\\\temp\\\\targetFile1.jpg"));
	        image2 = ImageIO.read(is);
	    
//	        // URL로 부터 이미지 읽기
//	        URL url = new URL("http://www.ojc.asia/images/new_logo.gif");
//	        image3 = ImageIO.read(url);
	    } catch (IOException e) {
	    } 
	    // Use a label to display the image
	    JFrame frame = new JFrame();
	    
	    JLabel label1 = new JLabel(new ImageIcon(image1));
	    JLabel label2 = new JLabel(new ImageIcon(image2));
	    
	    frame.getContentPane().add(label1, BorderLayout.CENTER);
	    frame.getContentPane().add(label2, BorderLayout.NORTH);
	    
	    frame.pack();
	    frame.setVisible(true);
	}
}