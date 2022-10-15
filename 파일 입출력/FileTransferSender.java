package ch18.sec02.exam01;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class FileTransferSender {

	public static final int DEFAULT_BUFFER_SIZE = 10000;
//https://twinw.tistory.com/153
	public static void main(String[] args) {
		String serverIP = args[0];
		int port = Integer.parseInt(args[1]);
		String FileName = args[2];
		
		File file = new File(FileName);
		if(!file.exists()) {
			System.out.println("파일 없음");
			System.exit(0);
		}
		long fileSize=file.length();
		long totalReadBytes =0; 
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		int readBytes;
		double startTime =0;
		try {
			FileInputStream fis = new FileInputStream(file);
			Socket socket = new Socket(serverIP, port);//"localhost", 50001
			if(!socket.isConnected()) {
				System.out.println("소켓 연결 오류");
				System.exit(0);
			}
			startTime = System.currentTimeMillis();
			OutputStream os = socket.getOutputStream();
			while((readBytes=fis.read(buffer))>0) {
				if(/*취소입력값*/) {
					break;
				}
				os.write(buffer, 0, readBytes);
				totalReadBytes += readBytes;
				System.out.println("In progress : "+totalReadBytes+"/"
						+fileSize +" Byte(s) ("+(totalReadBytes*100/fileSize)+"%)");
			}
			System.out.println("파일 변환 완료");
			fis.close();
			os.close();
			socket.close();
		}catch(UnknownHostException e) {
			e.printStackTrace();
			
		}catch(IOException e){
			e.printStackTrace();
		}
		double endTime = System.currentTimeMillis();
		double diffTime =(endTime - startTime)/1000;
		double transferSpeed =(fileSize/1000)/diffTime;
		
		System.out.println("time : "+diffTime+"second(s)");
		System.out.println("Average transfer speed : "+transferSpeed+"KB/s");
	}
}