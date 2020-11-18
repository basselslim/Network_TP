package server;

import java.io.*;
import java.net.*;
import java.util.LinkedList;

public class ClientThread extends Thread {

    private Socket clientSocket;
    private PrintStream socOut;
    private BufferedReader socIn;
    private Server server;

    ClientThread(Socket s, Server serv) {
        this.clientSocket = s;
        server = serv;
    }

    public void run() {
        try {
            socIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            socOut = new PrintStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Error while accessing socket");
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
        stop();
    }

    public void sendMessage (String message) {
        socOut.println(message);
    }

}
