package udpTest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class p2pTestClientRequest {
	public static void main(String[] args) throws UnknownHostException, IOException {

		Socket socket = new Socket("127.0.0.1", 9000);
		
		// Cria uma cadeia de saida (escrita) de informações no socket
		OutputStream os = socket.getOutputStream();
		DataOutputStream writer = new DataOutputStream(os);
		
		// Cria um buffer que lê informações do teclado
		//BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		// Leitura do teclado 
		//String texto = inFromUser.readLine();
		
		String texto = "musicaTeste_2.jpg";
		
		// Escrita no socket (envio de informações ao host remoto)
		writer.writeBytes(texto + "\n");
		
		//------------------------------------------------------------------------------------------------
		
		// Cria uma cadeia de entrada (leitura) de informações no socket
		InputStreamReader is = new InputStreamReader(socket.getInputStream());
		BufferedReader reader = new BufferedReader(is);
		
		// Leitura do socket (recebimento de inforações do host remoto)
		String response = reader.readLine(); //BLOCKING
		System.out.println("Response: " + response);
		
		socket.close();
	}
	
}
