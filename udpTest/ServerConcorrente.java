package udpTest;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

public class ServerConcorrente {
	
	public static void main(String[] args) throws IOException {
		// Criar o mecanismo para escutar e atender conex�es pela porta 9876
		DatagramSocket serverSocket = new DatagramSocket(9876);
		
		// Tabela HASH com os nomes das musicas que cada host possui
		Map<String, List<Integer>> lista_MusicaPorta = new HashMap<String, List<Integer>>();
		
		while(true) {
			
			// Declara��o e preenchimento do buffer de recebimento
			byte[] recBuffer = new byte[1024];

			// Cira��o do datagrama a ser recebido
			DatagramPacket recPacket = new DatagramPacket(recBuffer, recBuffer.length);
			
			System.out.println("Esperando alguma mensagem...");
			
			// Recebimento do datagrama do host remoto (m�todo bloquante)
			serverSocket.receive(recPacket); //BLOCKING ----------------------------------------------------------------
			
			ThreadAtendimento thread = new ThreadAtendimento(serverSocket, recPacket, lista_MusicaPorta);
			thread.start();
		}
				
	}
		
	public static class ThreadAtendimento extends Thread{
		
		private DatagramSocket serverSocket;
		private DatagramPacket recPacket;
		public Map<String, List<Integer>> lista_MusicaPorta = new HashMap<String, List<Integer>>();
		
		public ThreadAtendimento(DatagramSocket serverSocket, DatagramPacket recPacket,
				Map<String, List<Integer>> lista_MusicaPorta) {
			this.serverSocket = serverSocket;
			this.recPacket = recPacket;
			this.lista_MusicaPorta = lista_MusicaPorta;
		}


		@Override
		public void run() {
			// Obten��o da informa��o vinda no datagrama
			String informacao = new String(recPacket.getData(), recPacket.getOffset(), recPacket.getLength());

			// Desserializar Json para objeto Mensagem 
			Mensagem mensagemInfo = DesserializerMensagemGson(informacao);			

			// Validar informacao
			if (mensagemInfo.getMetodo().equals("JOIN")){

				// Pegando os dados vindo da string e adicionando numa lista
				String[] musicasLiStrings = listagemMusicas(mensagemInfo.getRequestResponsePayload());
				System.out.println("Numero de musicas: " + musicasLiStrings.length);		

				// Ender�o IP e porta do Cliente (s� usando para devolver algo)
				InetAddress iPAddress = recPacket.getAddress();
				int port = recPacket.getPort();

				// Adicionando as musicas do host na hasktable <MUSICA, PORTAS>
				addMusicasToTable(musicasLiStrings, lista_MusicaPorta, port);
				System.out.println("Hashtable:" + lista_MusicaPorta);


				// Declara��o e preenchimento do buffer de envio
				byte[] sendBuffer = new byte[1024];
				sendBuffer = "JOIN_OK".getBytes();

				// Cria��o do datagrama a ser enviado (como resposta ao cliente)
				DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, iPAddress, port);

				// Envio do datagrama ao cliente
				try {
					serverSocket.send(sendPacket);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				System.out.println("Mensagem enviada pelo server");

			} else if (mensagemInfo.getMetodo().equals("LEAVE")){

				System.out.println("saindo...");
				// Ender�o IP e porta do Cliente (s� usando para devolver algo)
				InetAddress iPAddress = recPacket.getAddress();
				int port = recPacket.getPort();


				System.out.println(mensagemInfo.getRequestResponsePayload());
				String[] musicasStringList = listagemMusicas(mensagemInfo.getRequestResponsePayload());

				leaveServer(lista_MusicaPorta, musicasStringList, port);
				System.out.println("Hashtable:" + lista_MusicaPorta);

				// Declara��o e preenchimento do buffer de envio
				byte[] sendBuffer = new byte[1024];
				sendBuffer = "LEAVE_OK".getBytes();

				// Cria��o do datagrama a ser enviado (como resposta ao cliente)
				DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, iPAddress, port);

				// Envio do datagrama ao cliente
				try {
					serverSocket.send(sendPacket);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				System.out.println("Mensagem enviada pelo server");

			} else if (mensagemInfo.getMetodo().equals("SEARCH")){

				// Procura e adiciona a uma lista todos os peers que possuem a musica
				List<Integer> listaPeers = searchMusic(lista_MusicaPorta, mensagemInfo.getRequestResponsePayload());
				System.out.println(listaPeers);
				String peerLiString = intListToString(listaPeers);

				// Ender�o IP e porta do Cliente (s� usando para devolver algo)
				InetAddress iPAddress = recPacket.getAddress();
				int port = recPacket.getPort();

				// Declara��o e preenchimento do buffer de envio
				byte[] sendBuffer = new byte[1024];
				sendBuffer = peerLiString.getBytes();

				// Cria��o do datagrama a ser enviado (como resposta ao cliente)
				DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, iPAddress, port);

				// Envio do datagrama ao cliente
				try {
					serverSocket.send(sendPacket);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (mensagemInfo.getMetodo().equals("UPDATE")){

				// Pegando os dados vindo da string e adicionando numa lista
				String[] musicasLiStrings = listagemMusicas(mensagemInfo.getRequestResponsePayload());
				System.out.println("Numero de musicas: " + musicasLiStrings.length);	

				// Ender�o IP e porta do Cliente (s� usando para devolver algo)
				InetAddress iPAddress = recPacket.getAddress();
				int port = recPacket.getPort();

				// Adicionando as musicas do host na hasktable <MUSICA, PORTAS>
				addMusicasToTable(musicasLiStrings, lista_MusicaPorta, port);
				System.out.println("Hashtable:" + lista_MusicaPorta);

				// Declara��o e preenchimento do buffer de envio
				byte[] sendBuffer = new byte[1024];
				sendBuffer = "UPDATE_OK".getBytes();

				// Cria��o do datagrama a ser enviado (como resposta ao cliente)
				DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, iPAddress, port);

				// Envio do datagrama ao cliente
				try {
					serverSocket.send(sendPacket);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}
	
	
	public static String[] listagemMusicas(String musicasString) {
        String[] strArr = musicasString.split("\\s+");//Splitting using whitespace
        System.out.println("The String is: " + musicasString);
        //System.out.print("The String Array after splitting is: " + Array.toString(strArr));
        return strArr;
	}
	
	public static void addMusicasToTable(String[] musicasList, Map<String, List<Integer>> ht, int port) {
		// Itera sobre a lista de musicas
		for(String musica : musicasList) {
			//System.out.println("musica:"+musica);
			// Verifica se a Musisca (KEY) j� existe na hashtable
			if(verifyMusicAlredyExists(ht, musica)) {
				addMusicaAlredyExists(ht, musica, port);
			} else {
				List<Integer> musicaUnica = new ArrayList<>();
				musicaUnica.add(port);
				ht.put(musica, musicaUnica);
			}		
		}		
	}
	
	public static boolean verifyMusicAlredyExists(Map<String, List<Integer>> ht, String musica) {
		if(!ht.isEmpty()) {
			if (ht.containsKey(musica)) {
				return true;
			}
		}
		return false;
	}
	
	public static void addMusicaAlredyExists(Map<String, List<Integer>> ht, String musica, int port) {
		List<Integer> pList = ht.get(musica);
		pList.add(port);
		ht.put(musica, pList);
		
	}
	
	public static void leaveServer(Map<String, List<Integer>> ht, String[] musicasSalvas, int port) {
		//System.out.println(ht.entrySet());
		for(String musica : musicasSalvas) {
			if (ht.get(musica).size() == 1) {
				ht.remove(musica);
			} else {
				List<Integer> pList = ht.get(musica);
				//System.out.println(pList);
				int index = pList.indexOf(port);
				pList.remove(index);
			}

		}
	}
	
	public static List<Integer> searchMusic(Map<String, List<Integer>> ht, String musica) {
		List<Integer> hostList = new ArrayList<Integer>();
		if (verifyMusicAlredyExists(ht, musica)) {
			hostList =  ht.get(musica);
			return hostList;
		} else {
			return hostList;
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
	
	public static String intListToString(List<Integer> peers) {
		String stringPeers = "";
		for(int peer : peers) {
			if (stringPeers.trim().isEmpty()) {
				stringPeers = String.valueOf(peer);
			} else {
				stringPeers = stringPeers + " " + String.valueOf(peer);
			}
		}
		return stringPeers;
	}
	
}
