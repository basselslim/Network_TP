package client;

import java.io.*;

/**
 * Thread listening to messages coming from the chat server
 * @author Bassel Slim & Clément Parret
 */
public class ClientThread extends Thread {

    /**Client whom to pass the information*/
    private Client listener;
    /**Input stream*/
    private BufferedReader reader;
    /**Boolean indicating if the connection is established*/
    private boolean state;

    /**
     * Initiates the thread
     * @param listener Client to notify
     * @param reader Input stream
     */
    public ClientThread(Client listener, BufferedReader reader) {
        this.listener = listener;
        this.reader = reader;
    }

    /**
     * Indicates whether a connection is established
     * @return true if the client is connected, false if not
     */
    public synchronized boolean isRunning() {
        return state;
    }

    /**
     * Stops the connection
     */
    public synchronized void kill() {
        state = false;
    }

    /**
     * Main action of the thread, listens to the chat server for incoming messages
     */
    public void run() {
        state = true;
        try {
            String message;
            while((message = reader.readLine()) != null) {
                listener.onReceiveMessage(message);
            }
            listener.onConnectionLost("Connection lost...");
        } catch (IOException e) {
            listener.onConnectionLost("Client disconnected");
        }
        kill();
    }
}