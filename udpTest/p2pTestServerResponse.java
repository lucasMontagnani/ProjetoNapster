package udpTest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class p2pTestServerResponse {
	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket(9000);
		System.out.println("listening on port: " + serverSocket.getLocalPort());
		
		ThreadServer tServer = new ThreadServer(serverSocket);
		tServer.start();
		
		/*
		while (true) {
			System.out.println("Esperando...");

			// Método bloqueante que cria um novo socket com o nó
			// Socket no terá uma porta designada pelo SO entre - 1024 e 65535
			Socket no = serverSocket.accept(); // BLOCKING,

			// Cria uma cadeia de entrada (leitura) de informações no socket
			InputStreamReader is = new InputStreamReader(no.getInputStream());
			BufferedReader reader = new BufferedReader(is);

			// Leitura do socket (recebimento de inforações do host remoto)
			String response = reader.readLine(); //BLOCKING
			System.out.println("Response: " + response);

			//------------------------------------------------------------------------------------------------

			OutputStream os = no.getOutputStream();
			DataOutputStream writer = new DataOutputStream(os);

			String texto = "OK";

			// Escrita no socket (envio de informações ao host remoto)
			writer.writeBytes(texto + "\n");

		}	
		*/

	}

	public static class ThreadServer extends Thread{
		private ServerSocket serverSocket;



		public ThreadServer(ServerSocket serverSocket) {
			super();
			this.serverSocket = serverSocket;
		}



		@Override
		public void run() {
			try {
				while (true) {
					System.out.println("Esperando...");

					// Método bloqueante que cria um novo socket com o nó
					// Socket no terá uma porta designada pelo SO entre - 1024 e 65535
					Socket no;

					no = serverSocket.accept(); // BLOCKING,

					// Cria uma cadeia de entrada (leitura) de informações no socket
					InputStreamReader is = new InputStreamReader(no.getInputStream());
					BufferedReader reader = new BufferedReader(is);

					// Leitura do socket (recebimento de inforações do host remoto)
					String response = reader.readLine(); //BLOCKING
					System.out.println("Response: " + response);

					//------------------------------------------------------------------------------------------------

					OutputStream os = no.getOutputStream();
					DataOutputStream writer = new DataOutputStream(os);

					String texto = "OK";

					// Escrita no socket (envio de informações ao host remoto)
					writer.writeBytes(texto + "\n");

				}	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}