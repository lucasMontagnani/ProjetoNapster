package udpTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class TesteClass {
	public static int valueTest = 0;
	public static void main(String[] args) throws IOException {
		System.out.println("testando...");
		//lerArquivos();

		//BufferedReader inputKeyBoard = new BufferedReader(new InputStreamReader(System.in));
		//System.out.println("Insira o caminho do diretorio onde estão os arquivos a serem compartilhados:");
		// Leitura do teclado
		//String texto = inputKeyBoard.readLine(); // BLOCKING

		//lerArquivosPeloCaminho(texto); //ok

		//listagemMusicas2("musicaTeste_1.jpg musicaTeste_2.jpg");

		//teste();
		//Map<String, List<Integer>> lista_MusicaPorta = new HashMap<String, List<Integer>>();
		//lista_MusicaPorta.put("musicaTeste_3.jpg", Arrays.asList(1, 2, 3));
		//lista_MusicaPorta.put("musicaTeste_4.jpg", Arrays.asList(4, 3, 5));

		//System.out.println(getKeysByValues(lista_MusicaPorta, 3));
				
		//System.out.println(Arrays.asList(4, 3, 5).contains(4));
		//aliveTest();
		
		BufferedReader inputKeyboard = new BufferedReader(new InputStreamReader(System.in));
		String opcao = inputKeyboard.readLine(); // BLOCKING
		
		if (opcao.equals("2")) {
			valueTest = 2;
			System.out.println(valueTest);
		} else if (opcao.equals("3")) {
			valueTest = 3;
			System.out.println(valueTest);
		}
		else if (opcao.equals("1")) {
			System.out.println(valueTest);
		}
	}

	public static List<String> getKeysByValues(Map<String, List<Integer>> map, Integer value) {
		List<String> musicasList = new ArrayList<String>();
		for (Map.Entry<String, List<Integer>> entry : map.entrySet()) {
			if (entry.getValue().contains(value)) {
				musicasList.add(entry.getKey());
				//System.out.println(entry.getKey());
			}
		}
		
		return musicasList;
	}

	//C:\\Users\\lucas\\Downloads\\MusicasTesteTeste
	public static void lerArquivos() {
		File file = new File("C:\\Users\\lucas\\Downloads\\MusicasTeste");
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
		System.out.println(stringFiles);
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
		System.out.println(stringFiles);
		return stringFiles;
	}

	public static String[] listagemMusicas(String musicasString) {
		String[] strArr = musicasString.split("\\s+");//Splitting using whitespace
		System.out.println("The String is: " + musicasString);
		//System.out.print("The String Array after splitting is: " + Array.toString(strArr));
		return strArr;
	}

	public static ArrayList<String> listagemMusicas2(String musicasString) {
		String[] strArr = musicasString.split("\\s+");//Splitting using whitespace
		ArrayList<String> list = new ArrayList<String>(Arrays.asList(strArr));
		System.out.println("The String is: " + musicasString);
		System.out.print("The ArrayList is: " + list);
		return list;
	}

	public static void addMusicasToTable(String[] musicasList, Hashtable<Integer, String> ht, int port) {
		for(String musica : musicasList) {
			ht.put(port, musica);
		}		
	}

	public static void teste() {
		Scanner ler = new Scanner(System.in);
		String opcao = ler.next();	
		System.out.println(opcao);
	}

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

	public static void aliveTest() {
		long segundos = (1000 * 5);

		Timer timer = new Timer();

		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("Testeee");
			}
		};

		timer.schedule(task, 0, segundos);
	}

}
