
import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class MultiThreadChatClient implements Runnable {

    // The client socket
    private static Socket clientSocket = null;
    // The output stream
    private static PrintStream os = null;
    // The input stream
    private static DataInputStream is = null;

    private static BufferedReader inputLine = null;
    private static boolean closed = false;
    private  static int minvalue;

    private  static String responseLine;
    private static String name;
    private static int bidvalue;
    public static void main(String[] args) {

        // The default port.
        int portNumber = 8567;
        bidvalue = 1;
        // The default host.
        String host = "localhost";

        if (args.length < 2) {
            System.out
                    .println("Usage: java MultiThreadChatClient <host> <portNumber>\n"
                            + "Now using host=" + host + ", portNumber=" + portNumber);
        } else {
            host = args[0];
            portNumber = Integer.valueOf(args[1]).intValue();
        }

    /*
     * Open a socket on a given host and port. Open input and output streams.
     */
        try {

            clientSocket = new Socket(host, portNumber);
            inputLine = new BufferedReader(new InputStreamReader(System.in));
            os = new PrintStream(clientSocket.getOutputStream());
            is = new DataInputStream(clientSocket.getInputStream());

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to the host "
                    + host);
        }

        new Thread(new MultiThreadChatClient()).start();
        try {
            while ((responseLine = is.readLine()) != null) {



                System.out.println(responseLine);
                if (responseLine.contains("bid")) {
//                    String delims = "[ ]+";
//                    String[] tokens = responseLine.split(delims);
                    //get the minimum value from server
                    String str = responseLine.replaceAll("\\D+", "");
                    minvalue = Integer.parseInt(str);


//                    System.out.println("split" +tokens[tokens.length-1]);
//
//                    for(int i = 0; i < tokens.length ;i ++)
//                    {
//                        System.out.println( tokens[i]);
//                    }
//                    System.out.println("Integer " + minvalue);
//                    System.out.println("this is replace all  " + str);


                    System.out.println(responseLine);
                } else if (responseLine.contains("name")) {
                    System.out.println(responseLine);

                    name = responseLine;
                }
                else if(responseLine.contains("left"))
                {
                    System.out.println(responseLine);
                }

                if (responseLine.indexOf("*** Bye") != -1)
                    break;
            }
            closed = true;
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        }
    /*

     * If everything has been initialized then we want to write some data to the
     * socket we have opened a connection to on the port portNumber.
     */


    }

    /*
     * Create a thread to read from the server. (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    public void run() {
    /*
     * Keep on reading from the socket till we receive "Bye" from the
     * server. Once we received that then we want to break.
     */


        if (clientSocket != null && os != null && is != null) {
            try {

        /* Create a thread to read from the server. */

                while (!closed) {
                    System.out.println("Enter something");
                    String input ;
                    input = inputLine.readLine().trim();

                   // System.out.println("bidbid"+bidvalue);

                    if (input.contains("bid")) {
                        String str = input.replaceAll("\\D+", "");
                        bidvalue = Integer.parseInt(str);
                       //System.out.println("inside biiiidd"+bidvalue);
                    }

                    if (bidvalue == 1) {
                        os.println(input);
                    } else if(input.equals("time"))
                    {
                        os.println("time");
                    }
                    else if (bidvalue <= minvalue) {
                        System.out.println("You have to bid more than " + minvalue);

                    } else {
                        os.println("bid" + bidvalue);
                    }
                }
        /*
         * Close the output stream, close the input stream, close the socket.
         */
                os.close();
                is.close();
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }


        }
    }}