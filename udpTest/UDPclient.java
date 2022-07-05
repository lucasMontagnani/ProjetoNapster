package udpTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;

public class UDPclient {
	public static void main(String[] args) throws Exception {
		
		// Endereço IP do host remoto (server)
		InetAddress iPAddress = InetAddress.getByName("127.0.0.1");
		
		// Canal de comunicação NÃO orientado à conexão
		// clientSocket terá uma porta designada pelo SO entre 1024 e 65535
		DatagramSocket clienSocket = new DatagramSocket();
		
		
		menuInterativo(iPAddress, clienSocket);
			
		
		// Fechamento da conexão
		clienSocket.close();
		
	}
	
	public static String lerDoTeclado() throws IOException {
		// Cria um Buffer que lê informações do teclado
		BufferedReader inputKeyBoard = new BufferedReader(new InputStreamReader(System.in));
						
		System.out.println("Insira o caminho do diretorio onde estão os arquivos a serem compartilhados:");
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
	
	public static void menuInterativo(InetAddress ip, DatagramSocket cSocket) throws IOException {
		
		System.out.println("Selecione uma das opções(número) abaixo:");
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
			join(ip, cSocket);
		} else if (opcao.equals("2")) {
			search(ip, cSocket);
		} else if (opcao.equals("3")) {
			
		} else if (opcao.equals("4")) {
			leave(ip, cSocket);
		} else {
			
		}
	}
	
	public static void search(InetAddress ip, DatagramSocket cSocket) throws IOException {
		// -- SEARCH : REQUEST ------------------------------------------------------------------------------------------------
		// Método que lê as informações do teclado
		String texto = lerDoTeclado();
		
		// Serializar objeto Mensagem para Json
		String jsonData = serializerMensagemGson("SEARCH", texto);	
		
		// Declaração e preenchimento do buffer de envio
		byte[] sendData = new byte[1024];
		sendData = jsonData.getBytes();
			
		// Criação do datagrama com endereço e porta do host remoto 9876
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, 9876);
				
		// Envio do Datagrama ao host remoto
		cSocket.send(sendPacket);		
		
		// -- SEARCH : REQUEST ------------------------------------------------------------------------------------------------
		// -- SEARCH : RESPONSE ------------------------------------------------------------------------------------------------
		
		// Declaração do buffer de recebimento (caso haja)
		byte[] recBuffer =  new byte[1024];
		
		// Ciração do datagrama a ser recebido
		DatagramPacket recPacket = new DatagramPacket(recBuffer, recBuffer.length);
		
		// Recebimento do datagrama do host remoto (método bloquante)
		cSocket.receive(recPacket); //BLOCKING
		
		// Obtenção da informação do datagrama 
		String informacao = new String(recPacket.getData(), recPacket.getOffset(), recPacket.getLength());		
		
		System.out.println(informacao);
		// -- SEARCH : RESPONSE ------------------------------------------------------------------------------------------------
	}
	
	public static void leave(InetAddress ip, DatagramSocket cSocket) throws IOException {
		// -- LEAVE : REQUEST ------------------------------------------------------------------------------------------------
		
		// Serializar objeto Mensagem para Json
		String jsonData = serializerMensagemGson("LEAVE", "");	
				
		// Declaração e preenchimento do buffer de envio
		byte[] sendData = new byte[1024];
		sendData = jsonData.getBytes();
			
		// Criação do datagrama com endereço e porta do host remoto 9876
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, 9876);
				
		// Envio do Datagrama ao host remoto
		cSocket.send(sendPacket);
		// -- LEAVE : REQUEST ------------------------------------------------------------------------------------------------
		// -- LEAVE : RESPONSE ------------------------------------------------------------------------------------------------
		// Declaração do buffer de recebimento (caso haja)
		byte[] recBuffer =  new byte[1024];
		
		// Ciração do datagrama a ser recebido
		DatagramPacket recPacket = new DatagramPacket(recBuffer, recBuffer.length);
		
		// Recebimento do datagrama do host remoto (método bloquante)
		cSocket.receive(recPacket); //BLOCKING
		
		// Obtenção da informação do datagrama 
		String informacao = new String(recPacket.getData(), recPacket.getOffset(), recPacket.getLength());
		
		System.out.println("Recebido do servidor: " + informacao);		
		
		/*
		if(informacao.equals("LEAVE_OK")) {
			cSocket.close();
		}
		*/
		// -- LEAVE : RESPONSE ------------------------------------------------------------------------------------------------
	}
	
	public static void join(InetAddress ip, DatagramSocket cSocket) throws IOException {
		// -- JOIN ------------------------------------------------------------------------------------------------
		// join (coletanto informações da pasta e musicas nela contidas)
		// Método que lê as informações do teclado
		String texto = lerDoTeclado();
			
		// Método que lê os arquivos do diretorio especificado
		String dadoString = lerArquivosPeloCaminho(texto);	
		
		// Serializar objeto Mensagem para Json
		String jsonData = serializerMensagemGson("JOIN", dadoString);	
		
		// join (enviando para o server)
		// Declaração e preenchimento do buffer de envio
		byte[] sendData = new byte[1024];
		sendData = jsonData.getBytes();
			
		// Criação do datagrama com endereço e porta do host remoto 9876
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, 9876);
				
		// Envio do Datagrama ao host remoto
		cSocket.send(sendPacket);
		// -- JOIN  ------------------------------------------------------------------------------------------------
			
		System.out.println("Mensagem enviada para o servidor");
				
		// -- JOIN_OK (recebendo do server) ------------------------------------------------------------------------------------------------
		// Declaração do buffer de recebimento (caso haja)
		byte[] recBuffer =  new byte[1024];
				
		// Ciração do datagrama a ser recebido
		DatagramPacket recPacket = new DatagramPacket(recBuffer, recBuffer.length);
		
		// Recebimento do datagrama do host remoto (método bloquante)
		cSocket.receive(recPacket); //BLOCKING
		
		// Obtenção da informação do datagrama 
		String informacao = new String(recPacket.getData(), recPacket.getOffset(), recPacket.getLength());
		
		System.out.println("Recebido do servidor: " + informacao);
		
		// JOIN_OK response
		if(informacao.equals("JOIN_OK")) {
			System.out.println("Sou peer " + InetAddress.getLoopbackAddress().getHostAddress() + ":" + cSocket.getLocalPort() +" com arquivos "+ dadoString);
			cSocket.getInetAddress();
			InetAddress.getLocalHost();
			
			/*
			System.out.println(clienSocket.getLocalAddress() + " " 
								+ clienSocket.getInetAddress() +  " "  
								+clienSocket.getLocalSocketAddress() + " "
								+clienSocket.getLocalAddress().getHostAddress() + " "
								+ InetAddress.getLocalHost() +  " "  
								+ InetAddress.getLoopbackAddress() +  " "  
								+ InetAddress.getLoopbackAddress().getHostAddress() +  " "  
								+ InetAddress.getLocalHost().getHostAddress() + " "
								+ clienSocket.getLocalAddress().getCanonicalHostName() + " " 
								+ clienSocket.getLocalAddress().getAddress().toString() + " " 
								+ clienSocket.getLocalAddress().getHostName() + " " 
								);
			 */
		}
		// -- JOIN_OK (recebendo do server) ------------------------------------------------------------------------------------------------
		
		menuInterativo(ip, cSocket);
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
	
	public static String[] listagemPeers(String peersString) {
        String[] strArr = peersString.split("\\s+");//Splitting using whitespace
        System.out.println("The String is: " + peersString);
        //System.out.print("The String Array after splitting is: " + Array.toString(strArr));
        return strArr;
	}
}
