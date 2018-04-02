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



//compiler
//javac -cp amqp-client-5.1.2.jar:mariadb-java-client-2.2.3.jar TP02/p4Image.java TP02/ConnecterMariaDB.java

//lancer
//java -cp .:amqp-client-5.1.2.jar:slf4j-api-1.7.25.jar:slf4j-simple-1.7.25.jar:mariadb-java-client-2.2.3.jar TP02/p4Image

public class p4Image {

	public static void main(String[] argv) throws Exception {

		String EXCHANGE_NAME_P3P4 = "exchange3";
		String NOM_FILE_DATTENTE_P3P4 = "fileImageP3";
		String nomUtilisateur = "guest"; // par defaut
		String motDePasse = "guest"; // par defaut
		int numeroPort = 5672; // par defaut
		String virtualHostName = "/"; // par defaut
		String hostName = "localhost";
		// String hostName = "192.168.183.129";
		boolean autoAck = false;
		boolean durable = true;
		boolean passive = false; 
		boolean autoDelete = false; // ne pas supprimer l'echangeur lorsqu'aucun client n'est connecte
		boolean exclusive = false;

		// se connecter au broker RabbitMQ
		ConnectionFactory factory = new ConnectionFactory();

		// indiquer les parametres de la connexion
		factory.setUsername(nomUtilisateur);
		factory.setPassword(motDePasse);
		factory.setPort(numeroPort);
		factory.setVirtualHost(virtualHostName);
		factory.setHost(hostName);

		// autre alternative pour specifier les parametres de la connexion
		// factory.setUri("amqp://nomUtilisateur:motDePasse@hostName:numeroPort/virtualHostName");

		// creer une nouvelle connexion
		Connection connexion = factory.newConnection();

		// ouvrir un canal de communication avec le Broker pour l'envoi et la
		// reception de messages
		Channel canalDeCommunication_P3P4 = connexion.createChannel();

		// Ordre exchangeDeclare --> exchange,type,durable,autodelete,passive(i),null
		//exchange,topic, true, false, false, null
		canalDeCommunication_P3P4.exchangeDeclare(EXCHANGE_NAME_P3P4, "topic",  durable, autoDelete, passive, null);

		// recuperer le nom d file d'attente associee a
		// String nomFileDAttente = canalDeCommunication.queueDeclare().getQueue();
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



				/*
				 * /////////////////////////// File outputFile = new File("resultado.png"); try
				 * ( FileOutputStream outputStream = new FileOutputStream(outputFile); ) {
				 * 
				 * outputStream.write(messageImageData); //write the bytes and your done.
				 * outputStream.close();
				 * 
				 * } catch (Exception e) { e.printStackTrace(); }
				 * 
				 * 
				 * 
				 * ///////////////////////////
				 */

				canalDeCommunication_P3P4.basicAck(envelope.getDeliveryTag(), false);
			} // end public void handleDelivery
		}; // end Consumer consumer
		canalDeCommunication_P3P4.basicConsume(NOM_FILE_DATTENTE_P3P4, autoAck, consumer);

	}//public static void main







}//end class p4


















