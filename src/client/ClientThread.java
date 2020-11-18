package client;

import java.io.*;
import java.net.*;

public class ClientThread extends Thread {

    private Socket clientSocket;
    private BufferedReader socIn;
    private PrintStream socOut;

    public ClientThread(Socket s) {
        this.clientSocket = s;
    }

    public void run() {
        try {
            socIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            socOut = new PrintStream(clientSocket.getOutputStream());
            while (true) {
                String line = socIn.readLine();
                socOut.println(line);
            }
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }

}