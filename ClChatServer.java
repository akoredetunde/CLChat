/*
 * Packages importation
 */
import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * This program is a simple comand line interface program
 * The program behave as a server that wait for a connetion from a client.
 * The program only support one connection. As soon as a connection is establish the server stop
 * listening for new connection.
 * The port that the server is listening on or running can be specify as a command line argument
 * and it none is specify a defualt port is used which is port 5000.
 * The two end of the connected both send a unique string to each other to verify the program
 * at each end is a CLChat program.
 * Then after both end has confirm that the program is the right program, then the connected
 * programs alternate send messages to each other.
 * Both end of the connected can close the connection by entering the string 'quit'.
 * @author akoredetunde
 */
public class ClChatServer{
    /*
     * The default port to listen on if none is specified.
     */
    static int port = 5000;

    /*
     * The unique string. Both end of connection send this string to each other 
     * to verify both program is a CLChat program
     */
    final static String HandShake = "CLCHAT";

    /*
     * This charcter is appended to every message that is send over the connection
     * to idicate that its message and not a command. The charcter is removed
     *  before displaying the message at the destination.
     */
    final static char MESSAGE = '0';

    /*
     * This charcter is send to the connected program whenever the other user quit
     */
    final static char CLOSE = '1'; 

    public static void main(String[] args) {
        ServerSocket listener;  //listening for a connection
        Socket connection;  //accepting a connection request and establishing the connection
        BufferedReader incoming;    //Stream for receiving data from the client
        PrintWriter outgoing;   //Stream for sending data to the client
        String messageOut;  //A message that is to be sent to the client
        String messageIn;   // A message that is recieved from the client 
        Scanner userInput;  // Reading input from the user

        /*
         * Checking if the port to be use if specify in the command line
         * and also verifing if the port specify is in the valid range
         */
        if(args.length > 0){ 
            try {  
                if(Integer.parseInt(args[0]) > 1075 && Integer.parseInt(args[0]) < 65535){
                    port = Integer.parseInt(args[0]);
                }else{
                    throw new NumberFormatException("Illegal Port Number " + args[0]);
                }
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
                return;
            }
            
        }

        /*
         * Waiting for a connection request, when it arrive
         * close the lsitener, so that it wont listen for more connection.
         * also creating of streams for communication and also exchanging 
         * the unique string (HandShake) to verify that the connection program is a
         * CLChat program
         */
        try {
            listener = new ServerSocket(port);
            connection = listener.accept();
            listener.close();
            incoming = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            outgoing = new PrintWriter(new OutputStreamWriter(connection.getOutputStream()));
            outgoing.println(HandShake);
            outgoing.flush();
            messageIn = incoming.readLine();
            if(!messageIn.equals(HandShake)){
                throw new Exception("Connected program is not a CLChat!");
            }
            System.out.println("Connected.  Waiting for the first message");

        } catch (Exception e) {
            System.out.println("An error occured while opening connection");
            System.out.println(e.toString());
            return;
        }


        /*
         * Exchanging messages with the other end of the connection until one
         * side or the other of the connection closes the connection.
         * The server wait for the first message. i.e The client send the first message
         */
        try {
            userInput = new Scanner(System.in);
            System.out.println("Note Enter 'quit' to end the program \n");
            while(true){
                System.out.println("WAITING...");
                messageIn = incoming.readLine();

                /*
                 * Checking if the message recieve from the client is a close command
                 * and if it is, the connection is closed down. else the message
                 * is display.
                 */
                if(messageIn.length() > 0){
                    if(messageIn.charAt(0) == CLOSE){
                        System.out.println("Connection closed at other end.");
                        connection.close();
                        incoming.close();
                        outgoing.close();
                        userInput.close();
                        break;
                    }
                    messageIn = messageIn.substring(1);
                }
                System.out.println("RECIEVED: " + messageIn);
                System.out.print("SEND:   ");
                messageOut = userInput.nextLine();

                /*
                 * Checking if the input from the user is a quit command
                 * if this is true a CLOSE command it send to the user at the other end
                 * to inform it that the user close the connection.
                 * Then the connection is close. Else the input is send to the other end
                 * as a message.
                 */
                if(messageOut.equalsIgnoreCase("quit")){
                    outgoing.println(CLOSE);
                    outgoing.flush();
                    connection.close();
                    userInput.close();
                    System.out.println("Connection Closed");
                    break;
                }
                outgoing.println("MESSAGE: " + messageOut);
                outgoing.flush();

                /*
                 * Checking if error occur while transmitting the message.
                 */
                if(outgoing.checkError()){
                    userInput.close();
                    outgoing.close();
                    incoming.close();
                    throw new IOException("Error occur while transmitting message");
                }
            }
        } catch (Exception e) {
            System.out.println("Sorry an error occured. Connection lost.");
            System.out.print("Error " + e);
            System.exit(1);
        }

    }
}