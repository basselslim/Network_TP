package client;

import java.io.*;
import java.net.*;

public class Client {

    private int port;
    private String host;
    private ClientThread clientThread;
    private Socket socket;

    public Client(String host, int port){
        this.host = host;
        this.port = port;
    }

    public void start() {
        try {
            clientThread = new ClientThread(socket);
            clientThread.start();
        } catch (IOException e) {
            //handle error
        }
    }

    public static void main(String args[]) {

        Client client = new Client(args[0], new Integer(args[1]).intValue());
        client.start();
    }
}
