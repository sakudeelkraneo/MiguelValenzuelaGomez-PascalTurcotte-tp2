package TP02;


import java.util.concurrent.TimeoutException;
import com.rabbitmq.client.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.HashMap;
import java.util.Map;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.select.Elements;

//compiler
//javac -cp amqp-client-5.1.2.jar:jsoup-1.11.2.jar TP02/p1SoloTexto.java 

//lancer
//java -cp .:amqp-client-5.1.2.jar:slf4j-api-1.7.25.jar:slf4j-simple-1.7.25.jar:jsoup-1.11.2.jar TP02/p1SoloTexto

public class p1SoloTexto {

/*private static final String siteWeb = "https://developer.apple.com/library/content/documentation/Darwin/Conceptual/KernelProgramming/vm/vm.html#//apple_ref/doc/uid/TP30000905-CH210-BEHJDFCA";*/

private static final String siteWeb = "https://www.uqar.ca/a-propos/langues/english/university/choosing-uqar";

	public static void main(String[] argv) throws java.io.IOException {

		// String siteWeb = "http://www.hdwallpapers.in/";
		String EXCHANGE_NAME = "exchange1";
		String NOM_FILE_DATTENTE_P1P2 = "fileTexteP1";
		String cleDeLiaison_P1P2 = "texteP1.message"; // cle de liaison/routage du message
		String nomUtilisateur = "guest"; // par defaut
		String motDePasse = "guest"; // par defaut
		int numeroPort = 5672; // par defaut
		String virtualHostName = "/"; // par defaut
		String hostName = "localhost";
		// String hostName = "192.168.183.129";

		boolean durable = true;
		//boolean passive = false;
		boolean passive = false; 
		// deja
		boolean autoDelete = false; // ne pas supprimer l'echangeur lorsqu'aucun
		// client n'est connecte
		boolean exclusive = false;

		// se connecter au broker RabbitMQ
		ConnectionFactory factory2 = new ConnectionFactory();

		// indiquer les parametres de la connexion
		factory2.setUsername(nomUtilisateur);
		factory2.setPassword(motDePasse);
		factory2.setPort(numeroPort);
		factory2.setVirtualHost(virtualHostName);
		factory2.setHost(hostName);

		

		// creer une nouvelle connexion
		Connection connexion2;
		try {
			connexion2 = factory2.newConnection();

			// ouvrir un canal de communication avec le Broker pour l'envoi et la reception
			// de // messages
			Channel canalDeCommunication_P1P2 = connexion2.createChannel();

			// Ordre exchangeDeclare --> exchange,type,durable,autodelete,passive(i),null
			//exchange,topic, true, false, false, null
canalDeCommunication_P1P2.exchangeDeclare(EXCHANGE_NAME, "topic", durable,autoDelete, passive,  null);


			canalDeCommunication_P1P2.queueDeclare(NOM_FILE_DATTENTE_P1P2, durable, exclusive, autoDelete, null);
			canalDeCommunication_P1P2.queueBind(NOM_FILE_DATTENTE_P1P2, EXCHANGE_NAME, cleDeLiaison_P1P2);
			canalDeCommunication_P1P2.basicQos(1);

			// aniadir codigo aqui........................
///////////////////////////////////////////////////////////////////////////////////////////////


String html ="";
		//https://examples.javacodegeeks.com/core-java/net/url/read-text-from-url/
		try {

			            URL url = new URL(siteWeb);
			            // read text returned by server
			            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

			            String line;
			            while ((line = in.readLine()) != null) {
			            	html+= line;
			              //System.out.println(line);
			            }
			            in.close();
			             
			           // System.out.println(html);
			        }
			        catch (MalformedURLException e) {
			            System.out.println("Malformed URL: " + e.getMessage());
			        }
			        catch (IOException e) {
			            System.out.println("I/O Error: " + e.getMessage());
			        }
		
		
		//https://www.tutorialspoint.com/jsoup/jsoup_extract_text.htm
		Document document = Jsoup.parse(html);

	      //a with href
	      //Element link = document.select("p").first();         
	      Elements link = document.select("p");    

		String texteTraduit =   link.text().toString();   


	      //System.out.println("Text: " + link.text());

		String[] temp;
		String delimeter = "\\.";
		temp = texteTraduit.split(delimeter);
		
		System.out.println("L E N G T H ///> "+temp.length);

		for (int i = 0; i < temp.length; i++)
		 {
			String phraseAEnvoyer = temp[i];
			System.out.println("\n"+"Message # "+i+" :"+phraseAEnvoyer);
			//Envoyer chaque phrase a P2
			canalDeCommunication_P1P2.basicPublish("", NOM_FILE_DATTENTE_P1P2,
						null, phraseAEnvoyer.getBytes());

		} 













			
///////////////////////////////////////////////////////////////////////////				
				/*//Envoyer le text a p2 
				canalDeCommunication_P1P2.basicPublish("", NOM_FILE_DATTENTE_P1P2,
						null, texteTraduit.getBytes());
				System.out.println("P1 a envoye le texte a  P2!!");*/
				
				

			// fermer le canal
			canalDeCommunication_P1P2.close();

			// fermer la connexion
			connexion2.close();

		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}// end public static void main
}// end public class p1
