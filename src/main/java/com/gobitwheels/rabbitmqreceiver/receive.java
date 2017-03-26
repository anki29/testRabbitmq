
package com.gobitwheels.rabbitmqreceiver;

/**
 *
 * @author ankit
 */
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;
import com.rabbitmq.client.ShutdownSignalException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;


class MyRunnable implements Runnable {        //Class for running the consumer in the different Thread
   private static final String EXCHANGE_NAME = "Exchange1";
    @Override
    public void run() {
        ConnectionFactory factory= new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        
         System.out.println("THis thread is working");
        
       
        try {
            Connection connection = factory.newConnection(); // Create connection
            Channel channel = connection.createChannel(); // Create Channel
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");
            String queueName="TestQueue"; //Consume the message from the QUEUE "TestQueue1(We have two queue, one for consuming and another for sending)
            channel.queueBind(queueName, EXCHANGE_NAME, "TestQueue");
            System.out.println("THis thread is working");
           boolean autoAck = false;

        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(queueName, autoAck, consumer);

        while (true) {
          QueueingConsumer.Delivery delivery = consumer.nextDelivery();
          String message = new String(delivery.getBody());
          
          System.out.println(" [x] Consumer : received '" + message + "'");
          doWork(message);

          channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
}
        
            
        } catch (IOException ex) {
            Logger.getLogger(MyRunnable.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TimeoutException ex) {
           Logger.getLogger(MyRunnable.class.getName()).log(Level.SEVERE, null, ex);
       } catch (InterruptedException ex) {
           Logger.getLogger(MyRunnable.class.getName()).log(Level.SEVERE, null, ex);
       } catch (ShutdownSignalException ex) {
           Logger.getLogger(MyRunnable.class.getName()).log(Level.SEVERE, null, ex);
       } catch (ConsumerCancelledException ex) {
           Logger.getLogger(MyRunnable.class.getName()).log(Level.SEVERE, null, ex);
       }
                          
        
    }
       
       
   private static void doWork(String task) throws InterruptedException {
        for (char ch: task.toCharArray()) {
          if (ch == '\n') Thread.sleep(5000);
        }
}
       
      
        
         
         
     }
    




public class receive {
    private static final String EXCHANGE_NAME = "Exchange";
     public static void main(String[] args) throws IOException, TimeoutException {
        MyRunnable runnable= new MyRunnable();
        Thread t= new Thread(runnable);
        t.start();
        ConnectionFactory factory= new ConnectionFactory(); //New connection for Sending the message on QUEUE "TestQueue1"
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        
        Connection connection= factory.newConnection();
        Channel channel= connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");        
        
        String queueName="TestQueue1";          //Queue for sending the message
        do{
        Scanner scanner= new Scanner(System.in);    
               
        String message = scanner.nextLine();    
        channel.basicPublish(EXCHANGE_NAME, queueName, null, message.getBytes()); //publish the message on the queue "TestQueue1"
      //  System.out.println(queueName +":" + message + "'");          
        

               
    
        }while(true);
    }
   


    
}