package client;

import java.io.*;
import java.net.*;

/**
 * Manages the connection to the chat server
 * @author Bassel Slim & Clément Parret
 */
public class Client {

    /**The chat user interface*/
    private ClientUI clientUI;
    /**TCP socket connected to the chat server*/
    private Socket clientSocket;
    /**Input stream*/
    private BufferedReader input;
    /**Output stream*/
    private PrintStream output;
    /**Thread managing the communication with the chat server*/
    private ClientThread client;

    /**
     * Initiates the client
     * @param listener The user interface
     */
    public Client(ClientUI listener){
        this.clientUI = listener;
    }

    /**
     * Connects the client to the chat server
     * @param pseudo The pseudo of the client
     * @param host The IP address of the chat server
     * @param port The port of the server that listens to new connections
     * @throws IOException
     */
    public synchronized void start(String pseudo, String host, int port) throws IOException{
        clientSocket = new Socket(host, port);
        input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        output = new PrintStream(clientSocket.getOutputStream());
        output.println(pseudo);
        client = new ClientThread(this, input);
        client.start();
    }

    /**
     * Ends the connection with the chat server
     */
    public synchronized void stop() {
        try {
            clientSocket.close();
        } catch(IOException e) {}
    }

    /**
     * Tells if the client is connected to a chat server
     * @return true if the client is connected, false if not
     */
    public synchronized boolean isConnectionStarted(){
        boolean state = false;
        if(client != null && client.isRunning()) {
            state = true;
        }
        return state;
    }

    /**
     * Sends a message to the chat server
     * @param message The message to send
     */
    public synchronized void writeMessage (String message) {
        if(clientSocket != null && isConnectionStarted()) {
            output.println(message);
        }
    }

    /**
     * Manages the reception of messages
     * @param message The message that was received
     */
    public void onReceiveMessage(String message) {
        clientUI.onReceiveMessage(message);
    }

    /**
     * Manages the disconnection from the chat server
     * @param message The message to display
     */
    public void onConnectionLost(String message) {
        clientUI.onConnectionLost(message);
    }
}
