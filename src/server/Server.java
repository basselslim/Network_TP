package server;

import java.io.*;
import java.net.*;
import java.util.LinkedList;

public class Server {

    private int port;
    private ServerThread mainThread;
    private LinkedList<ClientThread> clients;

    public Server (int port) {
        this.port = port;
        clients = new LinkedList<>();
    }

    public void start() {
        try {
            mainThread = new ServerThread(port, this);
            mainThread.start();
        } catch (IOException e) {
            //handle error
        }
    }

    public void onAcceptClient(ClientThread client) {
        clients.add(client);
    }

    public static void main(String args[]) {

        Server server = new Server(Integer.valueOf(args[0]));
        server.start();
    }

}
