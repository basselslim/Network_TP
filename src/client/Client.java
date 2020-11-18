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
            clientThread = new ClientThread(socket);
            clientThread.start();
    }

    public static void main(String args[]) {

        Client client = new Client(args[0], Integer.valueOf(args[1]));
        client.start();
    }
}
