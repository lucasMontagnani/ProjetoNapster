package udpTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Scanner;

public class TesteClass {
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
	
}
