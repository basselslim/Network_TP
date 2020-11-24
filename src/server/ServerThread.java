package server;

import java.io.*;
import java.net.*;

/**
 * Thread managing new connections to the chat server
 * @author Bassel Slim & Clément Parret
 */
public class ServerThread extends Thread{

    /**The default socket of the server*/
    private ServerSocket socket;
    /**The server hosting the chat*/
    private Server server;

    /**
     * Instantiates the thread
     * @param port The port on which the socket listens
     * @param serv The server hosting the chat
     * @throws IOException
     */
    public ServerThread (int port, Server serv) throws IOException {
        socket = new ServerSocket(port);
        server = serv;
    }

    /**
     * Main action of the thread, it accepts new connections to the server and starts a clientThread each time
     */
    public void run () {
        try {
            while (true) {
                Socket client = socket.accept();
                new ClientThread(client, server).start();
            }
        } catch (IOException e) {
            //handle error
        }
    }

    /**
     * Terminates the thread
     */
    public void end () {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.stop();
    }

}
