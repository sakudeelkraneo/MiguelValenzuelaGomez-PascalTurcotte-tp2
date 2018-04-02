package TP02;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.Connection;
import java.io.IOException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import java.io.File;
import java.io.FileOutputStream;
import TP02.ConnecterMariaDB;

////////// P4   Images  +  Texte 

//compiler
//javac -cp amqp-client-5.1.2.jar:mariadb-java-client-2.2.3.jar TP02/p4.java TP02/ConnecterMariaDB.java

//lancer
//java -cp .:amqp-client-5.1.2.jar:slf4j-api-1.7.25.jar:slf4j-simple-1.7.25.jar:mariadb-java-client-2.2.3.jar TP02/p4

public class p4 {

	public static void main(String[] argv) throws Exception {

		String EXCHANGE_NAME_P3P4 = "exchange3";
		String NOM_FILE_DATTENTE_P3P4 = "fileImageP3";
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
		Channel canalDeCommunication_P3P4 = connexion.createChannel();

		// Ordre exchangeDeclare --> exchange,type,durable,autodelete,passive(i),null
		// exchange,topic, true, false, false, null
		canalDeCommunication_P3P4.exchangeDeclare(EXCHANGE_NAME_P3P4, "topic", durable, autoDelete, passive, null);

		canalDeCommunication_P3P4.queueDeclare(NOM_FILE_DATTENTE_P3P4, durable, exclusive, autoDelete, null);

		String cleDeLiaison_P3P4 = "imageP3P4.*";

		// lier la file d'attente a l'echangeur
		canalDeCommunication_P3P4.queueBind(NOM_FILE_DATTENTE_P3P4, EXCHANGE_NAME_P3P4, cleDeLiaison_P3P4);

		// Ne pas delivrer a un consommateur plus qu'un message a la fois: Fair dispatch
		canalDeCommunication_P3P4.basicQos(1);

		System.out.println(" -* En attente de messages ... pour arreter pressez CTRL+C");

		Consumer consumer = new DefaultConsumer(canalDeCommunication_P3P4) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {

				byte[] messageImageData = body;
				System.out.println("P4 a recu une image de P3!!");

				// Envoyer l'image!!!!!!!!!!!!
				ConnecterMariaDB.envoyerImage(messageImageData);

				canalDeCommunication_P3P4.basicAck(envelope.getDeliveryTag(), false);
			} // end public void handleDelivery
		}; // end Consumer consumer
		canalDeCommunication_P3P4.basicConsume(NOM_FILE_DATTENTE_P3P4, autoAck, consumer);

				
		//////////////////////    Section Texte  //////////////////////////
		
		String EXCHANGE_NAME_P2P4 = "exchange3";
		String NOM_FILE_DATTENTE_P2P4 = "fileTexteP2";
		// se connecter au broker RabbitMQ
		ConnectionFactory factory2 = new ConnectionFactory();

		// indiquer les parametres de la connexion
		factory2.setUsername(nomUtilisateur);
		factory2.setPassword(motDePasse);
		factory2.setPort(numeroPort);
		factory2.setVirtualHost(virtualHostName);
		factory2.setHost(hostName);

		// creer une nouvelle connexion
		Connection connexion2 = factory2.newConnection();

		// ouvrir un canal de communication avec le Broker pour l'envoi et la
		// reception de messages
		Channel canalDeCommunication_P2P4 = connexion2.createChannel();

		// Ordre exchangeDeclare --> exchange,type,durable,autodelete,passive(i),null
		// exchange,topic, true, false, false, null
		canalDeCommunication_P2P4.exchangeDeclare(EXCHANGE_NAME_P2P4, "topic", durable, autoDelete, passive, null);

		// String nomFileDAttente = canalDeCommunication.queueDeclare().getQueue();
		canalDeCommunication_P2P4.queueDeclare(NOM_FILE_DATTENTE_P2P4, durable, exclusive, autoDelete, null);

		String cleDeLiaison_P2P4 = "texteP2.*";

		// lier la file d'attente a l'echangeur
		canalDeCommunication_P2P4.queueBind(NOM_FILE_DATTENTE_P2P4, EXCHANGE_NAME_P2P4, cleDeLiaison_P2P4);

		// Ne pas delivrer a un consommateur plus qu'un message a la fois: Fair dispatch
		canalDeCommunication_P2P4.basicQos(1);

		System.out.println(" -* En attente de messages ... pour arreter pressez CTRL+C");

		Consumer consumer2 = new DefaultConsumer(canalDeCommunication_P2P4) {
			@Override
			public void handleDelivery(String consumerTag2, Envelope envelope2, AMQP.BasicProperties properties2,
					byte[] body2) throws IOException {

				String messageTexteRecu = new String(body2, "UTF-8");
				System.out.println("\n" + "P4 a recu une message de P2!!");

				String contentMessage = properties2.getContentType();

				System.out.println("contentMessage : " + contentMessage);


				String messFrancais = ConnecterMariaDB.extractFrench(messageTexteRecu);
				String messAnglais = ConnecterMariaDB.extractEnglish(messageTexteRecu);
				System.out.println("Message Francais : " + messFrancais);
				System.out.println("Message Anglais : " + messAnglais+"\n\n");


				// envoyer les paragraphes en francais et anglais!
				ConnecterMariaDB.envoyerTexte(messAnglais,messFrancais);

				canalDeCommunication_P2P4.basicAck(envelope2.getDeliveryTag(), false);
			} // end public void handleDelivery
		}; // end Consumer consumer
		canalDeCommunication_P2P4.basicConsume(NOM_FILE_DATTENTE_P2P4, autoAck, consumer2);

	}// public static void main

}// end class p4
