package server;

import java.io.*;
import java.net.*;

/**
 * Thread managing the connection to the chat server for a specific client
 * @author Bassel Slim & Clément Parret
 */
public class ClientThread extends Thread {

    /**The TCP socket specific to this thread's client*/
    private Socket clientSocket;
    /**Output stream*/
    private PrintStream socOut;
    /**Input stream*/
    private BufferedReader socIn;
    /**The server hosting the chat*/
    private Server server;
    /**The pseudo of the client*/
    private String pseudo;

    /**
     * Instantiates the thread
     * @param s The socket that was created for this client
     * @param serv The server hosting the chat
     */
    ClientThread(Socket s, Server serv) {
        this.clientSocket = s;
        server = serv;
    }

    /**
     * Main action of the thread, gets the thread started and then listens for incoming messages from the client
     */
    public void run() {
        try {
            socIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            socOut = new PrintStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Error while accessing socket");
        }
        try {
            pseudo = socIn.readLine();
        } catch (IOException e) {
            pseudo = "Unknown";
        }
        server.onAcceptClient(this);
        try {
            String message;
            while((message = socIn.readLine()) != null) {
                server.onReceiveMessage(this, message);
            }
        } catch(IOException e) {
            //handle exception
        }
        server.onDisconnectClient(this);
        end();
    }

    /**
     * Terminates the thread
     */
    public void end () {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.stop();
    }

    /**
     * Sends a message to the client
     * @param message The message to send
     */
    public void sendMessage (String message) {
        socOut.println(message);
    }

    /**
     * Getter for the socket attribute
     * @return The socket attribute
     */
    public Socket getClientSocket() {
        return clientSocket;
    }

    /**
     * Getter for the pseudo attribute
     * @return The pseudo attribute
     */
    public String getPseudo() {
        return pseudo;
    }

}
