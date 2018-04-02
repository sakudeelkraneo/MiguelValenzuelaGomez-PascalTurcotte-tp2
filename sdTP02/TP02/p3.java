package TP02;

import TP02.ImagesWeb;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.Connection;
import java.util.concurrent.TimeoutException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

//compiler
//javac -cp amqp-client-5.1.2.jar TP02/p3.java TP02/ImagesWeb.java

//lancer
//java -cp .:amqp-client-5.1.2.jar:slf4j-api-1.7.25.jar:slf4j-simple-1.7.25.jar TP02/p3

public class p3 {
	// pour sauvegarder sur disque les images envoyes par P1
	private static final String folderPath = "/home/tpascal/Documents/unihiver2018/sysDis/sdTP02/TP02/imagesTEMP/";
	
	public static void main(String[] argv) throws Exception {
		
		String EXCHANGE_NAME_P1P3 = "exchange1";
		String NOM_FILE_DATTENTE_P1P3 = "fileImageP1";
		//String nomUtilisateur = "guest"; 
		//String motDePasse = "guest"; 

		//J'ai change les nicknames et mot de passe pour avoir acces remote
		String nomUtilisateur = "test"; 
		String motDePasse = "test";

		int numeroPort = 5672; 
		String virtualHostName = "/"; 

		String hostName = "localhost";
		//String hostName = "192.168.32.130"; //centos 7 syst dist 2

		boolean autoAck = false;
		boolean durable = true;
		boolean passive = false; 
		boolean autoDelete = false; 
		boolean exclusive = false;

		// se connecter au broker RabbitMQ
		ConnectionFactory factory = new ConnectionFactory();

		// indiquer les parametres de la connexion
		factory.setUsername(nomUtilisateur);
		factory.setPassword(motDePasse);
		factory.setPort(numeroPort);
		factory.setVirtualHost(virtualHostName);
		factory.setHost(hostName);

		// creer une nouvelle connexion
		Connection connexion = factory.newConnection();

		// ouvrir un canal de communication avec le Broker pour l'envoi et la
		// reception de messages
		Channel canalDeCommunication_P1P3 = connexion.createChannel();

		// Ordre exchangeDeclare --> exchange,type,durable,autodelete,passive(i),null
		//exchange,topic, true, false, false, null
		canalDeCommunication_P1P3.exchangeDeclare(EXCHANGE_NAME_P1P3, "topic", durable, autoDelete, passive,  null);

		canalDeCommunication_P1P3.queueDeclare(NOM_FILE_DATTENTE_P1P3, durable, exclusive, autoDelete, null);

		String cleDeLiaison_P1P3 = "imageP1.*";

		// lier la file d'attente a l'echangeur
		canalDeCommunication_P1P3.queueBind(NOM_FILE_DATTENTE_P1P3, EXCHANGE_NAME_P1P3, cleDeLiaison_P1P3);

		// Ne pas delivrer a un consommateur plus qu'un message a la fois: Fair dispatch
		canalDeCommunication_P1P3.basicQos(1);

		System.out.println(" -* En attente de messages ... pour arreter pressez CTRL+C");

		Consumer consumer = new DefaultConsumer(canalDeCommunication_P1P3) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				
				// Recuperer l'image envoye par P1
				byte[] imageRecuDuP1 = body;
				System.out.println(" P3 a bien recu l'image envoyee par P1!!");

				// Recuperer les properties (Nom et type du fichier recu)
				Map<String, Object> headers = new HashMap<String, Object>();
				headers = properties.getHeaders();

				String nomDuFichier = headers.get("nom").toString();
				String typeDuFichier = headers.get("type").toString();
				
				System.out.println("\n"+"****NOM***** :" + nomDuFichier);
				System.out.println("*** TYPE **** :" + typeDuFichier);

				//Se connecter a une canal de communication (p3 avec p4)
				String EXCHANGE_NAME_P3P4 = "exchange3";
				String NOM_FILE_DATTENTE_P3P4 = "fileImageP3";
				String cleDeLiaison_P3P4 = "imageP3P4.message"; // cle de liaison/routage du message

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

					Channel canalDeCommunication_P3P4 = connexion2.createChannel();

				// Ordre exchangeDeclare --> exchange,type,durable,autodelete,passive(i),null
				//exchange,topic, true, false, false, null
					canalDeCommunication_P3P4.exchangeDeclare(EXCHANGE_NAME_P3P4, "topic",  durable, autoDelete, passive,
							null);

					canalDeCommunication_P3P4.queueDeclare(NOM_FILE_DATTENTE_P3P4, durable, exclusive, autoDelete,
							null);

					canalDeCommunication_P3P4.queueBind(NOM_FILE_DATTENTE_P3P4, EXCHANGE_NAME_P3P4, cleDeLiaison_P3P4);
					canalDeCommunication_P3P4.basicQos(1);

					// sauvegarder les images sur disque
					ImagesWeb.createFile(imageRecuDuP1, nomDuFichier, typeDuFichier, folderPath);

					// creer des images 500 x 500 et obtenir leur path en string
					String image500Path = ImagesWeb.resize500(nomDuFichier, typeDuFichier, folderPath);
					System.out.println("I M A G E    5 0 0 --> " + image500Path);

					// Transformer en byte[] l'image 500x500
					Path path500 = Paths.get(image500Path);
					byte[] image500x500 = Files.readAllBytes(path500);

					// creer des images 1000 x 1000 et obtenir leur path en string
					String image1000Path = ImagesWeb.resize1000(nomDuFichier, typeDuFichier, folderPath);
					System.out.println("I M A G E    1 0 0 0 --> " + image1000Path);

					// Transformer en byte[] l'image 1000x1000
					Path path1000 = Paths.get(image1000Path);
					byte[] image1000x1000 = Files.readAllBytes(path1000);

					
					// Envoyer l'image 500 x 500 en format byte[]
					canalDeCommunication_P3P4.basicPublish("", NOM_FILE_DATTENTE_P3P4, null, image500x500);
					//WHY IT WORKS WITH NULL?????????
					//canalDeCommunication_P3P4.basicPublish(EXCHANGE_NAME_P3P4, NOM_FILE_DATTENTE_P3P4, null, image500x500);  
					System.out.println(" - P3 a envoye une image 500 x 500 a P4!");
					
					
					// Envoyer l'image 1000 x 1000 en format byte[]
					canalDeCommunication_P3P4.basicPublish("", NOM_FILE_DATTENTE_P3P4, null, image1000x1000); 
					//WHY IT WORKS WITH NULL?????????
					//canalDeCommunication_P3P4.basicPublish(EXCHANGE_NAME_P3P4, NOM_FILE_DATTENTE_P3P4, null, image1000x1000);
					System.out.println(" - P3 a envoye une image 1000 x 1000 a P4!");

					// fermer le canal P3P4
					canalDeCommunication_P3P4.close();
					// fermer la connexion 2
					connexion2.close();
				} // end try
				catch (TimeoutException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				canalDeCommunication_P1P3.basicAck(envelope.getDeliveryTag(), false);
			} // end public void handleDelivery
		}; // end Consumer consumer
		canalDeCommunication_P1P3.basicConsume(NOM_FILE_DATTENTE_P1P3, autoAck, consumer);

	}
}
