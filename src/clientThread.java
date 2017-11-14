import javax.swing.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Timer;

/*
 * The chat client thread. This client thread opens the input and the output
 * streams for a particular client, ask the client's name, informs all the
 * clients connected to the server about the fact that a new client has joined
 * the chat room, and as long as it receive data, echos that data back to all
 * other clients. The thread broadcast the incoming messages to all clients and
 * routes the private message to the particular client. When a client leaves the
 * chat room this thread informs also all the clients about that and terminates.
 */
class clientThread extends Thread {

    private String clientName = null;
    private DataInputStream is = null;
    private PrintStream os = null;
    private Socket clientSocket = null;
    private final clientThread[] threads;
    private int maxClientsCount;

    private static int  minbid;
    private  static  int minutes;
    private static  long counter;
    private static int itemCounter;
    private static  String time;
    private static String Winner ="noWinner";

    List<String> Items = new ArrayList<>(Arrays.asList("T-shirt","Nike Shoes","Car", "Motorbike"));

    public clientThread(Socket clientSocket, clientThread[] threads) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;

    }
    public void run() {
        int maxClientsCount = this.maxClientsCount;
        clientThread[] threads = this.threads;
        try {


      /*
       * Create input and output streams for this client.
       */
            // is = new DataInputStream(clientSocket.getInputStream());


//            Timer timer = new Timer();
//            TimerTask task = new TimerTask() {
//                @Override
//                public void run() {
//
//                    try {
//                        minutes++;
//                        System.out.println("time is " + minutes);
//                        sleep(3000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            };
//
//            timer.scheduleAtFixedRate(task,1000,1000);
//
        os = new PrintStream(clientSocket.getOutputStream());

            Scanner in = new Scanner(clientSocket.getInputStream());
            String name;
            String startAuction;
            while (true) {

                //ask user to enter name
                os.println("Enter your name.");
                name = in.nextLine();
               // check if minbid is 0 or not if not send minbid to other threads
                if(minbid == 0)
                {
                    os.println("First Item is " +Items.get(itemCounter) + "min bid "+"50");
                    minbid = 50;
                }
                else
                {
                    os.println("First Item is " +Items.get(itemCounter) + "min bid "+minbid);
                }

                if (name.indexOf('@') == -1) {
                    break;
                } else {
                    os.println("The name should not contain '@' character.");
                }
            }
            // set time and broadcast to every client when 60 seconds has passed
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    // Task to be executed every second
                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {

                            @Override
                            public void run() {
                                DateFormat timeFormat = new SimpleDateFormat("ss");
                                Calendar cali = Calendar.getInstance();
                                cali.getTime();
                                long startTime = System.currentTimeMillis();
                                long elapsedTime = System.currentTimeMillis() - startTime;
                                long elapsedSeconds = elapsedTime / 1000;
                                long secondsDisplay = elapsedSeconds % 60;
                                long elapsedMinutes = elapsedSeconds / 60;
                                time = timeFormat.format(cali.getTimeInMillis());


                                //System.out.println("this is "+" "+Winner +" and Counter "+counter);

                                counter++;

                                if(counter == 5)
                                {
                                    if(Winner.equals("noWinner"))
                                    {

                                        synchronized (this) {
                                            for (int i = 0; i < maxClientsCount; i++) {
                                                if (threads[i] != null && threads[i].clientName != null) {
                                                    threads[i].os.println(Winner +" takes the " +" "+  Items.get(itemCounter));
                                                }
                                            }
                                        }

                                        itemCounter++;
                                        System.out.println(Items.size());
                                    }
                                    if(!Winner.equals("noWinner"))
                                    {
                                        System.out.println(Winner);
                                        synchronized (this){
                                            for (int i = 0; i < maxClientsCount; i++) {
                                                if (threads[i] != null && threads[i].clientName != null) {
                                                    threads[i].os.println("Highest bid is "+ minbid+"$"+ " "+ Winner+ "takes the " +" "+  Items.get(itemCounter));
                                                }
                                            }
                                        }
                                        Items.remove(itemCounter);
                                        itemCounter++;
                                        System.out.println("items being removed " +Items.size());
                                    }


                                    if (Items.size() == 0)
                                    {
                                        System.out.println("aqui");
                                        Items.add("Auction closed");
                                    }


                                    if(itemCounter > Items.size()-1)
                                    {
                                        itemCounter = 0;
                                    }



                                    synchronized (this) {
                                        for (int i = 0; i < maxClientsCount; i++) {
                                            if (threads[i] != null && threads[i].clientName != null) {
                                                minbid = 50;
                                                if(!Items.get(0).contains("none"))
                                                {
                                                    os.println("Next item is  " +Items.get(itemCounter) + "Minimum  bid " + " is "+ minbid +"$");
                                                }else
                                                {
                                                    os.println(Items.get(itemCounter) );
                                                }

                                            }
                                        }
                                    }

                                    Winner ="noWinner";
                                    counter =0;


                                }
                            }
                        });
                    } catch (InvocationTargetException | InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            };

// This will invoke the timer every second
            timer.scheduleAtFixedRate(task, 1000, 1000);
      /* Welcome the new the client. */
//            os.println("Welcome " + name
//                    + " to our Auction \nTo leave enter /quit in a new line.");
            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null && threads[i] == this) {
                        clientName = "@" + name;
                        break;
                    }
                }
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null && threads[i] != this) {
                        threads[i].os.println("*** A new user " + name
                                + " entered the auction !!! ***");
                    }
                }
            }
      /* Start the conversation. */



            while (true) {

                String line =  in.nextLine();


                    System.out.println(Items.size());
                if (line.startsWith("quit")) {
                    break;
                }


        /* If the message is private sent it to the given client. */
                if (line.startsWith("@")) {
                    String[] words = line.split("\\s", 2);
                    if (words.length > 1 && words[1] != null) {
                        words[1] = words[1].trim();


                        if (!words[1].isEmpty()) {
                            synchronized (this) {
                                for (int i = 0; i < maxClientsCount; i++) {
                                    if (threads[i] != null && threads[i] != this
                                            && threads[i].clientName != null
                                            && threads[i].clientName.equals(words[0])) {
                                        threads[i].os.println("<" + name + "> " + words[1]);
                    /*
                     * Echo this message to let the client know the private
                     * message was sent.
                     */
                                        this.os.println(">" + name + "> " + words[1]);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }else if(line.startsWith("bid"))
                {
                    String str = line.replaceAll("\\D+", "");
                    minbid = Integer.parseInt(str);

                    synchronized (this) {
                        for (int i = 0; i < maxClientsCount; i++) {
                            if (threads[i] != null && threads[i].clientName != null) {


                                threads[i].os.println(name + "  bid   "+minbid + "");
                                Winner = name;

                            }
                        }
                    }

                }else if(line.startsWith("time"))
                {
                    synchronized (this) {
                        for (int i = 0; i < maxClientsCount; i++) {
                            if (threads[i] != null && threads[i].clientName != null) {


                                threads[i].os.println("you have "+time +"left");
                            }
                        }
                    }

                }
                else {
          /* The message is public, broadcast it to all other clients. */


//                    synchronized (this) {
//                        for (int i = 0; i < maxClientsCount; i++) {
//                            if (threads[i] != null && threads[i].clientName != null) {
//
//
//                                threads[i].os.println(name + "  bid   "+time);
//                            }
//                        }
//                    }
               }
            }
            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null && threads[i] != this
                            && threads[i].clientName != null) {
                        threads[i].os.println("*** The user " + name
                                + " is leaving the chat room !!! ***");
                    }
                }
            }
            os.println("*** Bye " + name + " ***");

      /*
       * Clean up. Set the current thread variable to null so that a new client
       * could be accepted by the server.
       */
            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == this) {
                        threads[i] = null;
                    }
                }
            }


      /*
       * Close the output stream, close the input stream, close the socket.
       */
            is.close();
            os.close();
            clientSocket.close();
        } catch (IOException e) {

            Thread.currentThread().interrupt();
            return;



        }
    }

}
