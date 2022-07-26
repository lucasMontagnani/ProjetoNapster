package udpTest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class p2pTestClient2 {
	public static void main(String[] args) throws UnknownHostException, IOException {

		byte[] bytes = new byte[1024];

		Socket socket = new Socket("127.0.0.1", 9000);

		// Armazenando no arquivo no array de bytes
		InputStream inputStream = socket.getInputStream();

		FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\lucas\\Downloads\\tcpTest\\test.mp3");
		
		// Gravando o arquivo por inteiro
		int count;
		while ((count = inputStream.read(bytes)) > 0) {
			fileOutputStream.write(bytes, 0, count);
		}
		
		System.out.println("Download finalzado!");
		
		// Fechando Socket e fileOutputStream
		fileOutputStream.close();
		socket.close();

	}
}
