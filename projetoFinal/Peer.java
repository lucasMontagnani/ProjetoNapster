package projetoFinal;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class Peer {
	//public String MusicasListString;
	public static boolean varTest = true;
	public static List<Integer> peerList = new ArrayList<Integer>();
	public static List<Integer> tcpList = new ArrayList<Integer>();
	public static String filePath;
	public static String fileName;
	
	public static void main(String[] args) throws Exception {
		// Endere�o IP do host remoto (server)
		InetAddress iPAddress = InetAddress.getByName("127.0.0.1");
		
		// Canal de comunica��o N�O orientado � conex�o
		// clientSocket ter� uma porta designada pelo SO entre 1024 e 65535
		DatagramSocket clienSocket = new DatagramSocket();
		
		// Canal de comunica��o orientado � conex�o em uma porta disponivel aleatoria
		ServerSocket serverSocket = new ServerSocket(0);
		
		ThreadServerTCP tServer = new ThreadServerTCP(serverSocket);
		tServer.start();
		
		String MusicasListString = "";
		
		new ThreadMenu(iPAddress, clienSocket, MusicasListString).start();
		
		while(varTest) {
			// -- RESPONSE ------------------------------------------------------------------------------------------------

			// Declara��o do buffer de recebimento (caso haja)
			byte[] recBuffer =  new byte[1024];

			// Cira��o do datagrama a ser recebido
			DatagramPacket recPacket = new DatagramPacket(recBuffer, recBuffer.length);

			// Recebimento do datagrama do host remoto (m�todo bloquante)
			clienSocket.receive(recPacket); //BLOCKING

			// Obten��o da informa��o do datagrama 
			String informacao = new String(recPacket.getData(), recPacket.getOffset(), recPacket.getLength());		

			//System.out.println(informacao);
			
			// Desserializar Json para objeto Mensagem 
			Mensagem mensagemInfo = DesserializerMensagemGson(informacao);			


			if(mensagemInfo.getMetodo().equals("JOIN_OK")) {
				System.out.println("Sou peer " + InetAddress.getLoopbackAddress().getHostAddress() + ":" + clienSocket.getLocalPort() +" com arquivos " + mensagemInfo.getRequestResponsePayload());
				
			} else if(mensagemInfo.getMetodo().equals("LEAVE_OK")) {
				//System.out.println("LEAVE_OK recebido"); // testing
				varTest = false;
				clienSocket.close();
				
			} else if(mensagemInfo.getMetodo().equals("UPDATE_OK")) {
				//System.out.println("UPDATE_OK recebido"); // testing
				
			} else if(mensagemInfo.getMetodo().equals("ALIVE")) {
				//System.out.println("ALIVE_OK recebido"); // testing
				try {
					alive(iPAddress, clienSocket);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			} else if(mensagemInfo.getMetodo().equals("SEARCH_OK")) {
				//System.out.println("SEARCH_OK recebido"); // testing
				String listIp = mensagemInfo.getRequestResponsePayload();
				//System.out.println("Info: " + listIp); // testing
				System.out.println("peers com arquivo solicitado: [127.0.0.1: " + mensagemInfo.getRequestResponsePayload() + "]");
				
				peerList = listagemPeers(listIp);
				
				
			} else if(mensagemInfo.getMetodo().equals("TCP_PORT")) {
				// Procurar e devolver porta tcp
				int tcpPort =  serverSocket.getLocalPort();
				int udpPort = recPacket.getPort();
				//System.out.println("tcpPort: " + tcpPort + " - " + "udpPort : " + udpPort); // testing
				tcpResponse(iPAddress, clienSocket, tcpPort, udpPort);
				
			} else if(mensagemInfo.getMetodo().equals("TCP_PORT_OK")) {
				// Lista de portas TCP enviadas pelos peers ativos
				tcpList.add(Integer.parseInt(mensagemInfo.getRequestResponsePayload()));
				//System.out.println("Bateeeeuuuu!"); // testing
			}
			

			// --RESPONSE ------------------------------------------------------------------------------------------------
		}
		
	}
		
	public static class ThreadServerTCP extends Thread{
		private ServerSocket serverSocket;

		public ThreadServerTCP(ServerSocket serverSocket) {
			super();
			this.serverSocket = serverSocket;
		}
		
		@Override
		public void run() {
			try {
				while (true) {
					//System.out.println("Esperando..."); // testing
					//- RECEBENDO MENSAGEM DE REQUISI��O -----------------------------------------------------------------------------------------------
					// M�todo bloqueante que cria um novo socket com o n�
					// Socket no ter� uma porta designada pelo SO entre - 1024 e 65535
					Socket no;

					no = serverSocket.accept(); // BLOCKING,

					// Cria uma cadeia de entrada (leitura) de informa��es no socket
					InputStreamReader is = new InputStreamReader(no.getInputStream());
					BufferedReader reader = new BufferedReader(is);

					// Leitura do socket (recebimento de infora��es do host remoto)
					String response = reader.readLine(); //BLOCKING
					//System.out.println("Response: " + response); // testing
					
					// Verificanso se possui o arquivo requisitado
					Boolean fileCheck = fileExists(response);
					
					//Aceitando ou negando a requisi��o de forma aleat�ria
					Boolean resposta = Math.random() < 0.5;
					//System.out.println(resposta); // testing
					
					if(fileCheck && resposta) {
						//- ENVIANDO MENSAGEM DE RESPOSTA-----------------------------------------------------------------------------------------------

						OutputStream os = no.getOutputStream();
						DataOutputStream writer = new DataOutputStream(os);

						String texto = "DOWNLOAD_ACEITO";

						// Escrita no socket (envio de informa��es ao host remoto)
						writer.writeBytes(texto + "\n");
						
						//- ENVIANDO ARQUIVO -----------------------------------------------------------------------------------------------
						// Capturando o arquivo pelo nome
						//File file = new File("C:\\Users\\lucas\\Downloads\\testFile\\textTest.txt"); // txt ok
						//File file = new File("C:\\Users\\lucas\\Downloads\\testFile\\musicaTeste_1.jpg"); //jpg ok
				        File file = new File(filePath + "\\" + response);
				        
				        // Criando um buffer do tamanho do arquivo selecionado
						byte bArray[] = new byte[(int)file.length()];
						
						FileInputStream fileImput = new FileInputStream(file);
						
						// Lendo o arquivo no Array de bytes por completo
						fileImput.read(bArray, 0, bArray.length);

						// Cria a cadeia de saida (escrita) de informa��es no socket; Basicamente o output de dados
						OutputStream oStream = no.getOutputStream();
						//DataOutputStream writer = new DataOutputStream(oStream);
						oStream.write(bArray, 0, bArray.length);
						
						// para indicar que o cliente terminou de enviar os dados de confirma��o TCP, caso contrario gera a excess�o java.net.SocketException: Connection reset
						no.shutdownOutput();
						
						// Fechando ServerSocket e FileInputStream
						fileImput.close();
					} else {
						//- ENVIANDO MENSAGEM DE RESPOSTA-----------------------------------------------------------------------------------------------

						OutputStream os = no.getOutputStream();
						DataOutputStream writer = new DataOutputStream(os);

						String texto = "DOWNLOAD_NEGADO";

						// Escrita no socket (envio de informa��es ao host remoto)
						writer.writeBytes(texto + "\n");
					}		

				}	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
	}
	
	
	public static class ThreadMenu extends Thread{
		private InetAddress ip;
		private DatagramSocket cSocket;
		private String MusicasListString;
				
		public ThreadMenu(InetAddress ip, DatagramSocket cSocket, String musicasListString) {
			super();
			this.ip = ip;
			this.cSocket = cSocket;
			MusicasListString = musicasListString;
		}

		@Override
		public void run() {
			try {
				menuInterativo(ip, cSocket, MusicasListString);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void menuInterativo(InetAddress ip, DatagramSocket cSocket, String MusicasListString) throws IOException {
		
		System.out.println("Selecione uma das op��es(n�mero) abaixo:");
		System.out.println("1 - JOIN");
		System.out.println("2 - SEARCH");
		System.out.println("3 - DOWNLOAD");
		System.out.println("4 - LEAVE");
		
		//Scanner ler = new Scanner(System.in);
		//String opcao = ler.next();
		//System.out.println(opcao.equals("1"));
		
		BufferedReader inputKeyboard = new BufferedReader(new InputStreamReader(System.in));
		String opcao = inputKeyboard.readLine(); // BLOCKING
		//System.out.println(opcao.equals("1"));
		
		if (opcao.equals("1")) {
			join(ip, cSocket, MusicasListString);
		} else if (opcao.equals("2")) {
			search(ip, cSocket, MusicasListString);
		} else if (opcao.equals("3")) {
			// Fazer tratamentos
			if (peerList.isEmpty()) {
				//System.out.println("N�o h� peers na lista de downloads. Fa�a uma busca pelo SEARCH para poder reqalizar o DOWNLOAD."); // testing
			} else {
				portRequest(ip, cSocket); // Portas tcp salvas em tcpList
				if (!tcpList.isEmpty()) {
					downloadTCP(ip, cSocket, MusicasListString);
					//menuInterativo(ip, cSocket);
				}
			}
		} else if (opcao.equals("4")) {
			leave(ip, cSocket, MusicasListString);
		} 
		/*
		else if (opcao.equals("5")) {
			update(ip, cSocket, "musicaTeste_3.jpg", MusicasListString);
		}else {
			
		}
		*/
	}
	
	public static void search(InetAddress ip, DatagramSocket cSocket, String MusicasListString) throws IOException {
		// -- SEARCH : REQUEST ------------------------------------------------------------------------------------------------
		// M�todo que l� as informa��es do teclado
		String texto = lerDoTeclado();
		
		// Salvando o nome do arquivo requisitado em uma v�riavel global
		fileName = texto;
		
		// Serializar objeto Mensagem para Json
		String jsonData = serializerMensagemGson("SEARCH", texto);	
		
		// Declara��o e preenchimento do buffer de envio
		byte[] sendData = new byte[1024];
		sendData = jsonData.getBytes();
			
		// Cria��o do datagrama com endere�o e porta do host remoto 10098
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, 10098);
				
		// Envio do Datagrama ao host remoto
		cSocket.send(sendPacket);		
		
		// -- SEARCH : REQUEST ------------------------------------------------------------------------------------------------
		
		/*
		// -- SEARCH : RESPONSE ------------------------------------------------------------------------------------------------
		
		// Declara��o do buffer de recebimento (caso haja)
		byte[] recBuffer =  new byte[1024];
		
		// Cira��o do datagrama a ser recebido
		DatagramPacket recPacket = new DatagramPacket(recBuffer, recBuffer.length);
		
		// Recebimento do datagrama do host remoto (m�todo bloquante)
		cSocket.receive(recPacket); //BLOCKING
		
		// Obten��o da informa��o do datagrama 
		String informacao = new String(recPacket.getData(), recPacket.getOffset(), recPacket.getLength());		
		
		System.out.println(informacao);
		// -- SEARCH : RESPONSE ------------------------------------------------------------------------------------------------
		//Chama menu de op��es
		 */
		
		//menuInterativo(ip, cSocket);
		new ThreadMenu(ip, cSocket, MusicasListString).start();
	}
	
	public static void leave(InetAddress ip, DatagramSocket cSocket, String MusicasListString) throws IOException {
		// -- LEAVE : REQUEST ------------------------------------------------------------------------------------------------
		
		// Serializar objeto Mensagem para Json
		String jsonData = serializerMensagemGson("LEAVE", MusicasListString);	
				
		// Declara��o e preenchimento do buffer de envio
		byte[] sendData = new byte[1024];
		sendData = jsonData.getBytes();
			
		// Cria��o do datagrama com endere�o e porta do host remoto 10098
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, 10098);
				
		// Envio do Datagrama ao host remoto
		cSocket.send(sendPacket);
		// -- LEAVE : REQUEST ------------------------------------------------------------------------------------------------
		
		/*
		// -- LEAVE : RESPONSE ------------------------------------------------------------------------------------------------
		// Declara��o do buffer de recebimento (caso haja)
		byte[] recBuffer =  new byte[1024];
		
		// Cira��o do datagrama a ser recebido
		DatagramPacket recPacket = new DatagramPacket(recBuffer, recBuffer.length);
		
		// Recebimento do datagrama do host remoto (m�todo bloquante)
		cSocket.receive(recPacket); //BLOCKING
		
		// Obten��o da informa��o do datagrama 
		String informacao = new String(recPacket.getData(), recPacket.getOffset(), recPacket.getLength());
		
		System.out.println("Recebido do servidor: " + informacao);		
		*/
		// -- LEAVE : RESPONSE ------------------------------------------------------------------------------------------------
		/*
		if(informacao.equals("LEAVE_OK")) {
			cSocket.close();
		}
		*/
	}
	
	public static void join(InetAddress ip, DatagramSocket cSocket, String MusicasListString) throws IOException {
		// -- JOIN ------------------------------------------------------------------------------------------------
		// join (coletanto informa��es da pasta e musicas nela contidas)
		// M�todo que l� as informa��es do teclado
		String texto = lerDoTeclado();
		
		// Salvando o caminho em uma v�riavel global
		filePath = texto;
			
		// M�todo que l� os arquivos do diretorio especificado
		//String dadoString = lerArquivosPeloCaminho(texto);
		MusicasListString = lerArquivosPeloCaminho(texto);
		
		// Serializar objeto Mensagem para Json
		String jsonData = serializerMensagemGson("JOIN", MusicasListString);	
		
		// join (enviando para o server)
		// Declara��o e preenchimento do buffer de envio
		byte[] sendData = new byte[1024];
		sendData = jsonData.getBytes();
			
		// Cria��o do datagrama com endere�o e porta do host remoto 10098
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, 10098);
				
		// Envio do Datagrama ao host remoto
		cSocket.send(sendPacket);
		// -- JOIN  ------------------------------------------------------------------------------------------------
		/*	
		System.out.println("Mensagem enviada para o servidor");
				
		// -- JOIN_OK (recebendo do server) ------------------------------------------------------------------------------------------------
		// Declara��o do buffer de recebimento (caso haja)
		byte[] recBuffer =  new byte[1024];
				
		// Cira��o do datagrama a ser recebido
		DatagramPacket recPacket = new DatagramPacket(recBuffer, recBuffer.length);
		
		// Recebimento do datagrama do host remoto (m�todo bloquante)
		cSocket.receive(recPacket); //BLOCKING
		
		// Obten��o da informa��o do datagrama 
		String informacao = new String(recPacket.getData(), recPacket.getOffset(), recPacket.getLength());
		
		System.out.println("Recebido do servidor: " + informacao);
		
		// JOIN_OK response
		if(informacao.equals("JOIN_OK")) {
			System.out.println("Sou peer " + InetAddress.getLoopbackAddress().getHostAddress() + ":" + cSocket.getLocalPort() +" com arquivos "+ MusicasListString);
			cSocket.getInetAddress();
			InetAddress.getLocalHost();
			
			
		}
		// -- JOIN_OK (recebendo do server) ------------------------------------------------------------------------------------------------
		 */
		
		//Chama menu de op��es
		//menuInterativo(ip, cSocket);
		
		new ThreadMenu(ip, cSocket, MusicasListString).start();
	}
	
	public static void update(InetAddress ip, DatagramSocket cSocket, String musicaBaixada, String MusicasListString) throws IOException {
		// -- UPDATE - REQUEST ------------------------------------------------------------------------------------------------		
		//Atualizar atributo MusicaListString com a nova musica
		// System.out.println(MusicasListString); // testing
		MusicasListString = MusicasListString + " "+musicaBaixada;
		//System.out.println(MusicasListString); // testing
		
		// Serializar objeto Mensagem para Json
		String jsonData = serializerMensagemGson("UPDATE", musicaBaixada);	
		
		// join (enviando para o server)
		// Declara��o e preenchimento do buffer de envio
		byte[] sendData = new byte[1024];
		sendData = jsonData.getBytes();
			
		// Cria��o do datagrama com endere�o e porta do host remoto 10098
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, 10098);
				
		// Envio do Datagrama ao host remoto
		cSocket.send(sendPacket);
		
		// -- UPDATE - REQUEST ------------------------------------------------------------------------------------------------	
		/*
		// -- UPDATE : RESPONSE ------------------------------------------------------------------------------------------------
		// Declara��o do buffer de recebimento (caso haja)
		byte[] recBuffer =  new byte[1024];
		
		// Cira��o do datagrama a ser recebido
		DatagramPacket recPacket = new DatagramPacket(recBuffer, recBuffer.length);
		
		// Recebimento do datagrama do host remoto (m�todo bloquante)
		cSocket.receive(recPacket); //BLOCKING
		
		// Obten��o da informa��o do datagrama 
		String informacao = new String(recPacket.getData(), recPacket.getOffset(), recPacket.getLength());		
		
		System.out.println(informacao);		
		// -- UPDATE : RESPONSE ------------------------------------------------------------------------------------------------
		 */
		
		//Chama menu de op��es
		//menuInterativo(ip, cSocket);
	}
	
	public static void alive(InetAddress ip, DatagramSocket cSocket) throws IOException {
		// -- ALIVE : RESPONSE ------------------------------------------------------------------------------------------------
		
		// Serializar objeto Mensagem para Json
		String jsonData = serializerMensagemGson("ALIVE_OK", "ALIVE_OK");	
		
		// Declara��o e preenchimento do buffer de envio
		byte[] sendData = new byte[1024];
		sendData = jsonData.getBytes();
			
		// Cria��o do datagrama com endere�o e porta do host remoto 10098
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, 10098);
				
		// Envio do Datagrama ao host remoto
		cSocket.send(sendPacket);		
		
		// -- ALIVE : RESPONSE ------------------------------------------------------------------------------------------------
		
	}
	
	public static void tcpRequest(InetAddress ip, DatagramSocket cSocket, int porta) throws IOException {
		// -- TCPREQUEST : REQUEST ------------------------------------------------------------------------------------------------
		
		// Serializar objeto Mensagem para Json
		String jsonData = serializerMensagemGson("TCP_PORT", "TCP_PORT");	
		
		// Declara��o e preenchimento do buffer de envio
		byte[] sendData = new byte[1024];
		sendData = jsonData.getBytes();
			
		// Cria��o do datagrama com endere�o e porta do host remoto 10098
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, porta);
				
		// Envio do Datagrama ao host remoto
		cSocket.send(sendPacket);		
		
		// -- TCPREQUEST : REQUEST ------------------------------------------------------------------------------------------------	
	}
	
	public static void tcpResponse(InetAddress ip, DatagramSocket cSocket, int portaTcp, int portaUdp) throws IOException {
		// -- TCPREQUEST : REQUEST ------------------------------------------------------------------------------------------------
		
		// Serializar objeto Mensagem para Json
		String jsonData = serializerMensagemGson("TCP_PORT_OK", String.valueOf(portaTcp));	
		
		// Declara��o e preenchimento do buffer de envio
		byte[] sendData = new byte[1024];
		sendData = jsonData.getBytes();
			
		// Cria��o do datagrama com endere�o e porta do host remoto 10098
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, portaUdp);
				
		// Envio do Datagrama ao host remoto
		cSocket.send(sendPacket);		
		
		// -- TCPREQUEST : REQUEST ------------------------------------------------------------------------------------------------	
	}
	
	public static void portRequest (InetAddress ip, DatagramSocket cSocket) {
		// Pedindo para todos os peers suas respectivas portas TCP atrav�s de uma requisi��o UDP
		for (Integer peer : peerList) {
			try {
				tcpRequest(ip, cSocket, peer);					
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// Esperar para que as tranza��es sejam finalizadas
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void downloadTCP(InetAddress ip, DatagramSocket cSocket, String MusicasListString) throws UnknownHostException, IOException {
		
		int iterator = 1;
		for(int i = 0; i<tcpList.size(); i++) {
		//for (Integer port : tcpList) {
			Socket socket = new Socket("127.0.0.1", tcpList.get(i));
			
			// Cria uma cadeia de saida (escrita) de informa��es no socket
			OutputStream os = socket.getOutputStream();
			DataOutputStream writer = new DataOutputStream(os);
			
			//texto a ser enviado
			//String texto = "musicaTeste_2.jpg";
			
			// Escrita no socket (envio de informa��es ao host remoto)
			writer.writeBytes(fileName + "\n");
			
			// RECEBENDO ------------------------------------------------------------------
			
			// Cria uma cadeia de entrada (leitura) de informa��es no socket
			InputStreamReader is = new InputStreamReader(socket.getInputStream());
			BufferedReader reader = new BufferedReader(is);
			
			// Leitura do socket (recebimento de infora��es do host remoto)
			String response = reader.readLine(); //BLOCKING
			//System.out.println("Response: " + response); // testing
			
			if(response.equals("DOWNLOAD_ACEITO")) {
				// - BAIXANDO ARQUIVO ------------------------------------------------------------------------------------
				//System.out.println("Bora baixar!"); // testing
				
				byte[] bytes = new byte[1024];
				
				// Armazenando no arquivo no array de bytes
				InputStream inputStream = socket.getInputStream();

				FileOutputStream fileOutputStream = new FileOutputStream(filePath + "\\" + fileName);
				
				// Gravando o arquivo gigante por inteiro
				int count;
				while ((count = inputStream.read(bytes)) > 0) {
					fileOutputStream.write(bytes, 0, count);
				}
				
				//System.out.println("Download finalzado!");
				
				// Fechando Socket e fileOutputStream
				fileOutputStream.close();
				
				System.out.println("Arquivo " + fileName + " baixado com sucesso na pasta " + filePath);
				
				// - ENVIANDO METODO UPDATE AO SERVIDOR ------------------------------------------------------------------
				update(ip, cSocket, fileName, MusicasListString);
				
				// Fechando socket
				socket.close();
				
				// Chamando Menu interativo
				new ThreadMenu(ip, cSocket, MusicasListString).start();
				
				// Saindo do loop pois o arquivo j� foi baidado
				break;
				
			} else if (response.equals("DOWNLOAD_NEGADO")) {
				// Fechando socket
				socket.close();
				
				
				
				// Verifico se tem mais algum peer na lista e se est� na primeira itera��o
				if (tcpList.size() == 1 && iterator == 1) {
					System.out.println("peer 127.0.0.1:" + tcpList.get(i) + " negou o download, pedindo agora para o peer 127.0.0.1:"+ tcpList.get(i));
					i=-1;
					iterator = 2;
				} else if (tcpList.size() == 1 && iterator == 2) {
					//System.out.println("N�o foi possivel fazer o download do arquivo solicitado nos peers disponiveis. Espere um pouco e procure novamente pelo arquivo no servidor.");
					System.out.println("peer 127.0.0.1:" + tcpList.get(i) + " negou o download");
					// Chamando Menu interativo
					new ThreadMenu(ip, cSocket, MusicasListString).start();
				} else if(tcpList.size() > 1 && i != tcpList.size()-1) {
					System.out.println("peer 127.0.0.1:" + tcpList.get(i) + " negou o download, pedindo agora para o peer 127.0.0.1:"+ tcpList.get(i+1));
				} else if(tcpList.size() > 1 && i == tcpList.size()-1) {
					System.out.println("peer 127.0.0.1:" + tcpList.get(i) + " negou o download");
					// Chamando Menu interativo
					new ThreadMenu(ip, cSocket, MusicasListString).start();
				}
				
				
				// Esperar 10 segundos at� perguntar novamente ao mesmo peer
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	
	public static String serializerMensagemGson(String action, String info) {
		Mensagem payloadMensagem = new Mensagem(action, info);
		
		Gson gson = new Gson();
		String jsonString = gson.toJson(payloadMensagem);
		return jsonString;
	}
	
	public static Mensagem DesserializerMensagemGson(String jsonString) {
		Gson gson = new Gson();
		Mensagem mensagemObject = gson.fromJson(jsonString, Mensagem.class);
		return mensagemObject;
	}
	
	public static String lerDoTeclado() throws IOException {
		// Cria um Buffer que l� informa��es do teclado
		BufferedReader inputKeyBoard = new BufferedReader(new InputStreamReader(System.in));
						
		//System.out.println("Insira o caminho do diretorio onde est�o os arquivos a serem compartilhados:"); // testing
		// Leitura do teclado
		String texto = inputKeyBoard.readLine(); // BLOCKING
		
		return texto;
	}
	
	public static String lerArquivosPeloCaminho(String path) {
		File file = new File(path);
		String[] listFiles = file.list();
		String stringFiles = "";
		for(String arquivo : listFiles) {
			//System.out.println(arquivo);

			if (stringFiles.trim().isEmpty()) {
				stringFiles = arquivo;
			} else {
				stringFiles = stringFiles + " " + arquivo;
			}
		}
		//System.out.println(stringFiles);
		return stringFiles;
	}
	
	public static List<Integer> listagemPeers(String peersString) {
        String[] strArr = peersString.split("\\s+");//Splitting using whitespace
        //System.out.println("The String is: " + peersString); // testing
        
        List<Integer> peerList = new ArrayList<>();
        
        // Convertendo de lista de strings para lista de integers
        for(int i=0; i<strArr.length; i++) {
        	peerList.add(Integer.parseInt(strArr[i]));
         }
        return peerList;
	}
	
	public static boolean fileExists(String fileName) {
		File file = new File(filePath);
		String[] listFiles = file.list();
		for (String item : listFiles) {
			if (fileName.equals(item)) {
				return true;
			}
		}
		return false;
	}
}
