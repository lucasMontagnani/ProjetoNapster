package udpTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class p2pTestServer {

	public static void main(String[] args) throws IOException {

		// Criar o mecanismo para escutar e atender conexôes por uma porta qualquer que esteja disponivel
		ServerSocket serverSocket = new ServerSocket(9000);
		System.out.println("listening on port: " + serverSocket.getLocalPort());

		//while(true) {

			System.out.println("Esperando conexão...");

			// Método bloqueante que cria um novo socket com o nó
			// Socket no terá uma porta designada pelo SO entre - 1024 e 65535
			Socket no = serverSocket.accept(); // BLOCKING,

			System.out.println("Conexão aceita!");
			
			// Capturando o arquivo pelo nome
			//File file = new File("C:\\Users\\lucas\\Downloads\\testFile\\textTest.txt"); // txt ok
			//File file = new File("C:\\Users\\lucas\\Downloads\\testFile\\musicaTeste_1.jpg"); //jpg ok
	        File file = new File("C:\\Users\\lucas\\Downloads\\testFile\\TesteMusica.mp3");
	        
	        // Criando um buffer do tamanho do arquivo selecionado
			byte bArray[] = new byte[(int)file.length()];
			
			FileInputStream fileImput = new FileInputStream(file);
			
			// Lendo o arquivo no Array de bytes por completo
			fileImput.read(bArray, 0, bArray.length);

			// Cria a cadeia de saida (escrita) de informações no socket; Basicamente o output de dados
			OutputStream oStream = no.getOutputStream();
			//DataOutputStream writer = new DataOutputStream(oStream);
			oStream.write(bArray, 0, bArray.length);
			
			// para indicar que o cliente terminou de enviar os dados de confirmação TCP, caso contrario gera a excessão java.net.SocketException: Connection reset
			no.shutdownOutput();
			
			// Fechando ServerSocket e FileInputStream
			serverSocket.close();
			fileImput.close();

		//}
	}

}
