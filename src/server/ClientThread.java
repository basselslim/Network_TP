package server;

import java.io.*;
import java.net.*;

public class ClientThread extends Thread {

    private Socket clientSocket;
    private PrintStream socOut;
    private BufferedReader socIn;
    private Server server;
    private String pseudo;

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
        try {
            pseudo = socIn.readLine();
        } catch (IOException e) {
            //handle exception
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

    public void end () {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.stop();
    }

    public void sendMessage (String message) {
        socOut.println(message);
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public String getPseudo() {
        return pseudo;
    }

}
