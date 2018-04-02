using RabbitMQ.Client;
using RabbitMQ.Client.Events;
using RabbitMQ.Client.MessagePatterns;
using System;
using System.Text;
using System.Net;
using System.Text.RegularExpressions;
using System.Collections.Generic;


class p2
{
    public static void Main()
    {
        var factory = new ConnectionFactory() { HostName = "localhost" };
        using (var connection = factory.CreateConnection())
        using (var channel = connection.CreateModel())
        {
            //channel.ExchangeDeclare("exchange1", "topic");
            channel.QueueDeclare(queue: "fileTexteP1", durable: true, exclusive: false, autoDelete: false, arguments: null);
            //channel.QueueBind("fileTexteP1", "exchange1", "texteP1.*", null);

            var consumer = new EventingBasicConsumer(channel);
            consumer.Received += (model, ea) =>
            {
                var body = ea.Body;
                var messageRecu = Encoding.UTF8.GetString(body);
                Console.WriteLine("\n" + " P2 a recu un message a traduire!!!");
                
                //Imbriquer les paragraphes a envoyer
                var factory2 = new ConnectionFactory() { HostName = "localhost" };
                using (var connection2 = factory2.CreateConnection())
                using (var channel2 = connection2.CreateModel())
                {
                    // ordre ExchangeDeclare --> exchange, type, durable, autodelete, null
                    channel2.ExchangeDeclare("exchange3", "topic", true, false, null);
                    channel2.QueueDeclare(queue: "fileTexteP2", durable: true, exclusive: false, autoDelete: false, arguments: null);
                    channel2.QueueBind("fileTexteP2", "exchange3", "texteP2.message", null);
                    
                    // appeller la methode pour traduire une phrase d'anglais a francais
                    // string messageTraduit = p2.Translate(messageRecu, "en", "fr");


			// ******* T E M P O R A I R E ********************
                    string messageTraduit = "google translate marche pu... alors j'ai mis ce message pour simuler";



                    // Envoyer les 2 messages en format "francais.anglais"

			String deuxMessages = messageTraduit+"."+messageRecu;
		
                    var messages_aEnvoyer = Encoding.UTF8.GetBytes(deuxMessages);
                    IBasicProperties props = channel2.CreateBasicProperties();
                    props.ContentType = "2Langues";
                    props.DeliveryMode = 2; //Persistent
                    channel2.BasicPublish(exchange: "exchange3", routingKey: "texteP2.message", basicProperties: props, body: messages_aEnvoyer);
                    Console.WriteLine(" les 2 messages sont envoyes!!!! : {0}", deuxMessages);

		

                } //end  using (var channel2 = connection2.CreateModel())
            }; //end consumer.Received += (model, ea) =>

            channel.BasicConsume(queue: "fileTexteP1", autoAck: true, consumer: consumer);

            Console.WriteLine(" Press [enter] to exit.");
            Console.ReadLine();
        }// end         using(var channel = connection.CreateModel()) 
    }// end Main


    public static string Translate(string text, string from, string to)
    {
        //REF : http://blog.isamert.net/c-translating-with-google-translate-without-using-api/

        string page = null;
        try
        {
            WebClient wc = new WebClient();
            wc.Headers.Add(HttpRequestHeader.UserAgent, "Mozilla/5.0");
            wc.Headers.Add(HttpRequestHeader.AcceptCharset, "UTF-8");
            wc.Encoding = Encoding.UTF8;
            string url = string.Format(@"http://translate.google.com.tr/m?hl=en&sl={0}&tl={1}&ie=UTF-8&prev=_m&q={2}",
                                        from, to, Uri.EscapeUriString(text));
            page = wc.DownloadString(url);
        }
        catch (Exception ex)
        {
            Console.WriteLine(ex.ToString());
            return null;
        }

        page = page.Remove(0, page.IndexOf("<div dir=\"ltr\" class=\"t0\">")).Replace("<div dir=\"ltr\" class=\"t0\">", "");
        int last = page.IndexOf("</div>");
        page = page.Remove(last, page.Length - last);

        return page;
    }// end public static string Translate
}//end class
