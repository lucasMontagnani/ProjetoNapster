package udpTest;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

public class UDPserver {
	public static void main(String[] args) throws Exception {
		// Criar o mecanismo para escutar e atender conexôes pela porta 9876
		DatagramSocket serverSocket = new DatagramSocket(9876);
		
		// Tabela HASH com os nomes das musicas que cada host possui
		//Hashtable<Integer, String> listaPorta_Musica = new Hashtable<Integer, String>();
		//Map<String, Integer> lista_Musica_Porta = new HashMap<String, Integer>();
		Map<String, List<Integer>> lista_MusicaPorta = new HashMap<String, List<Integer>>();
		String[] musicasLiStrings = new String[50];
		
		while(true) {
			
			// Declaração e preenchimento do buffer de recebimento
			byte[] recBuffer = new byte[1024];

			// Ciração do datagrama a ser recebido
			DatagramPacket recPacket = new DatagramPacket(recBuffer, recBuffer.length);
			
			System.out.println("Esperando alguma mensagem...");
			
			// Recebimento do datagrama do host remoto (método bloquante)
			serverSocket.receive(recPacket); //BLOCKING ----------------------------------------------------------------
			
			// Obtenção da informação vinda no datagrama
			String informacao = new String(recPacket.getData(), recPacket.getOffset(), recPacket.getLength());
			
			// Desserializar Json para objeto Mensagem 
			Mensagem mensagemInfo = DesserializerMensagemGson(informacao);			
			
			// Validar informacao
			if (mensagemInfo.getMetodo().equals("JOIN")){
				
				// Pegando os dados vindo da string e adicionando numa lista
				musicasLiStrings = listagemMusicas(mensagemInfo.getRequestResponsePayload());
				System.out.println("Numero de musicas: " + musicasLiStrings.length);		
				
				// Enderço IP e porta do Cliente (só usando para devolver algo)
				InetAddress iPAddress = recPacket.getAddress();
				int port = recPacket.getPort();
				
				// Adicionando as musicas do host na hasktable <MUSICA, PORTAS>
				addMusicasToTable(musicasLiStrings, lista_MusicaPorta, port);
				System.out.println("Hashtable:" + lista_MusicaPorta);
				
				
				// Declaração e preenchimento do buffer de envio
				byte[] sendBuffer = new byte[1024];
				sendBuffer = "JOIN_OK".getBytes();
				
				// Criação do datagrama a ser enviado (como resposta ao cliente)
				DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, iPAddress, port);
				
				// Envio do datagrama ao cliente
				serverSocket.send(sendPacket);
				
				System.out.println("Mensagem enviada pelo server");
				
			} else if (mensagemInfo.getMetodo().equals("LEAVE")){
				
				System.out.println("saindo...");
				// Enderço IP e porta do Cliente (só usando para devolver algo)
				InetAddress iPAddress = recPacket.getAddress();
				int port = recPacket.getPort();
				
				leaveServer(lista_MusicaPorta, musicasLiStrings, port);
				System.out.println("Hashtable:" + lista_MusicaPorta);
				
				// Declaração e preenchimento do buffer de envio
				byte[] sendBuffer = new byte[1024];
				sendBuffer = "LEAVE_OK".getBytes();
				
				// Criação do datagrama a ser enviado (como resposta ao cliente)
				DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, iPAddress, port);
				
				// Envio do datagrama ao cliente
				serverSocket.send(sendPacket);
				
				System.out.println("Mensagem enviada pelo server");
				
			} else if (mensagemInfo.getMetodo().equals("SEARCH")){
				
				// Procura e adiciona a uma lista todos os peers que possuem a musica
				List<Integer> listaPeers = searchMusic(lista_MusicaPorta, mensagemInfo.getRequestResponsePayload());
				System.out.println(listaPeers);
				String peerLiString = intListToString(listaPeers);
				
				// Enderço IP e porta do Cliente (só usando para devolver algo)
				InetAddress iPAddress = recPacket.getAddress();
				int port = recPacket.getPort();
				
				// Declaração e preenchimento do buffer de envio
				byte[] sendBuffer = new byte[1024];
				sendBuffer = peerLiString.getBytes();
				
				// Criação do datagrama a ser enviado (como resposta ao cliente)
				DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, iPAddress, port);
				
				// Envio do datagrama ao cliente
				serverSocket.send(sendPacket);
			}
		}
		
		//serverSocket.close();
		
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
			// Verifica se a Musisca (KEY) já existe na hashtable
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
				int index = pList.get(port);
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
