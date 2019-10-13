package friends;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
//import java.util.regex.Pattern;


public class FriendsDriver {

	public static void main(String[] args) throws FileNotFoundException {
		
		Scanner inputScan = new Scanner(System.in);
		System.out.print("Input friends graph: ");
		String doc = inputScan.next();
		
		File document = new File(doc);
		Scanner docScan = new Scanner(document);
		
		
		Graph g = new Graph(docScan);
		
		System.out.println("connectors: " + Friends.connectors(g));
		System.out.println();
		
		
		String answer = "yes";
		String school;
		String p1;
		String p2;
		
		
		while (answer.equals("yes")) {
			
			//cliques
			System.out.print("school: ");
			school = inputScan.next();
			System.out.println("cliques: " + Friends.cliques(g, school));
			
			//shortest chain
			
			System.out.print("shortest chain first person: ");
			p1 = inputScan.next();
			System.out.print("shortest chain second person: ");
			p2 = inputScan.next();
			System.out.println("shortest chain: " + Friends.shortestChain(g, p1, p2));
			
			
			System.out.println("--------------");
			System.out.print("Again? ");
			answer = inputScan.next();
			
		}
		
		
	
		
	
		inputScan.close();
	}
	
	

}
