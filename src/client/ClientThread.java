package client;

import java.io.*;
import java.net.*;

public class ClientThread extends Thread {

    private Socket clientSocket;
    private BufferedReader socIn;
    private PrintStream socOut;
    private BufferedReader stdIn;

    public ClientThread(String host, int port) {
        try {
            this.clientSocket = new Socket(host, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            socIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            socOut= new PrintStream(clientSocket.getOutputStream());
            stdIn = new BufferedReader(new InputStreamReader(System.in));

            String line;
            while (true) {
                line=stdIn.readLine();
                if (line.equals(".")) break;
                socOut.println(line);
                System.out.println("echo: " + socIn.readLine());
            }

            socOut.close();
            socIn.close();
            stdIn.close();
            clientSocket.close();

        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }


    }

}