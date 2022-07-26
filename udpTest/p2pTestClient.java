package udpTest;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class p2pTestClient {

	public static void main(String[] args) throws UnknownHostException, IOException {

		byte bArray[] = new byte[1024];
		
		Socket socket = new Socket("127.0.0.1", 9000);
		
		// Armazenando no arquivo no array de bytes
		InputStream inputStream = socket.getInputStream();
		inputStream.read(bArray, 0, bArray.length);
		
		// Armazenando o arquivo
		FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\lucas\\Downloads\\tcpTest\\test.jpg");
		fileOutputStream.write(bArray, 0, bArray.length);
	}

}
