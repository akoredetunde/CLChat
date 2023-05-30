/*
 * Packages importation
 */
import java.net.*;
import java.util.Scanner;
import java.io.*;

/**
 * This is a simple command line interface program
 * This program behave as a client that send a connection request to a server.
 * The port number the server is hosted on is pass as a command line argument
 * if the port is not specified in the command line argument then a default port is use
 * which is the port 5000.
 * When a connection is established with the server, the server send a unique string to this program
 * if the unique string send by the server is not the rigth string the connection is close else the
 * program also send back the unique string to the server. This is done to verify that both the program
 * is connected to a CLChat server.
 * After the server has been comfirmed then both the server and the program can start exchanges messages
 * with each other. Any of the program can close the connection by sending the string 'quit'
 * @author akoredetunde
 */
public class ClChatClient{
    /* The unique string. This string is exchange between the program and the server to
     * verify that the server connected to is a CLChat server.
     */
    final static String HandShake = "CLCHAT";

    /*
     * This is the Domain or IP where the server is hosted. Here I'm using a localhost IP
     * because I'm running both the server and the client program on the same computer.
     */
    final static String hostname = "127.0.0.1";

    /*
     * A Command that is sent to and fro of the connection to indicate that a program has
     * close the connection
     */
    final static char CLOSE = '1';

    /*
     * This is appended to any message sent to the other end to indicate that its not a command
     * but a pure messages. The character is removed before displaying it at the destination.
     */
    final static char MESSAGE = '0';

    /*
     * The default port to use if the port is not specified in the command line argument.
     */
    static int port = 5000;
    
    public static void main(String[] args) {
        Socket connection; //   Sending a connection request to the server
        BufferedReader incoming; //    Stream for reading incoming data from the server.
        PrintWriter outgoing; //    Stream for sending data to the server.
        String messageIn; //    Message recieved from the server
        String messageOut; //   Message to be sent to the server
        Scanner userInput; //   Reading input from the user

        /*
         * Checking if a port number is specify in the command line argument
         * and also verifying that the port is in the valid range.
         */
        if(args.length > 0){
            try{
                if(Integer.parseInt(args[0]) > 1075 && Integer.parseInt(args[0]) < 65535){
                    port = Integer.parseInt(args[0]);
                }else{
                    throw new NumberFormatException("Illegal Port Number " + args[0]);
                }
            }catch(NumberFormatException e){
                System.out.println(e.toString());
                return;
            }
        }

        /*
         * Creating a connection request to the server,
         * Creating streams for recieving and sending data to and fro the connection,
         * and also exchanging the unique string to verify that the server is a CLChat server.
         */
        try {
            connection = new Socket(hostname, port);
            incoming = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            outgoing = new PrintWriter(new OutputStreamWriter(connection.getOutputStream()));
            messageIn = incoming.readLine();
            if(!messageIn.equals(HandShake)){
                incoming.close();
                outgoing.close();
                connection.close();
                throw new Exception("Connected Server is not a CLChat Server!.");
            }
            outgoing.println(messageIn);
            outgoing.flush();
            outgoing.checkError();
            System.out.println("Connected.  You can send your first message");
        } catch (Exception e) {
            System.out.println("Error occur while opening connection");
            System.out.println(e.toString());
            return;
        }

        /*
         * Exchanging messages with the server until any of the connected progarams
         * close the connection. This program send the first message.
         */
        try {
            userInput = new Scanner(System.in);
            System.out.println("Note Enter 'quit' to end the program \n");
            System.out.println("Start the conversation");
            while(true){
                System.out.print("SEND:    ");
                messageOut = userInput.nextLine();

                /*
                 * Checking if the user enter the quit command and if this is true
                 * the CLOSE character is send to the server to inform the server 
                 * that the client close the connection.
                 * Then the connection is closed. Else the message is send to the server
                 */
                if(messageOut.equalsIgnoreCase("quit")){
                    outgoing.println(CLOSE);
                    outgoing.flush();
                    connection.close();
                    System.out.println("Connection closed.");
                    incoming.close();
                    outgoing.close();
                    userInput.close();
                    break;
                }
                outgoing.println(MESSAGE + messageOut);
                outgoing.flush();

                /*
                 * Checking if an error occur while transmitting the message
                 */
                if(outgoing.checkError()){
                    incoming.close();
                    outgoing.close();
                    userInput.close();
                    connection.close();
                    throw new IOException("Error occur while transmitting message");
                }
                System.out.println("Waiting...");
                messageIn = incoming.readLine();

                /*
                 * Checking if the message recieved from the server is the CLOSE Command
                 * if this is true, then this means the server has close the connection, the 
                 * the program also close the connection. Else the is display to the user.
                 */
                if(messageIn.charAt(0) == CLOSE){
                    System.out.println("Connected closed at other end");
                    connection.close();
                    incoming.close();
                    outgoing.close();
                    userInput.close();
                    break;
                }
                System.out.println(messageIn);
            }
        } catch (Exception e) {
            System.out.println("Sorry an error occured. Connection lost.");
            System.out.print("Error " + e);
            System.exit(1);
        }
    }  
}