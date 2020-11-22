package client;

import java.io.*;
import java.net.*;

public class Client implements ConnectionListener{

    private ConnectionListener clientUI;

    private Socket clientSocket;

    private BufferedReader input;

    private PrintStream output;

    private ClientThread client;

    public Client(ConnectionListener listener){
        this.clientUI = listener;
    }

    public synchronized void start(String pseudo, String host, int port) throws IOException{
        clientSocket = new Socket(host, port);
        input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        output = new PrintStream(clientSocket.getOutputStream());
        output.println(pseudo);
        client = new ClientThread(this, input);
        client.start();
    }

    public synchronized void stop() {
        try {
            clientSocket.close();
        } catch(IOException e) {}
    }

    public synchronized boolean isConnectionStarted(){
        boolean state = false;
        if(client != null && client.isRunning()) {
            state = true;
        }
        return state;
    }

    public synchronized void writeMessage (String message) {
        if(clientSocket != null && isConnectionStarted()) {
            output.println(message);
        }
    }

    @Override
    public void onReceiveMessage(String message) {
        clientUI.onReceiveMessage(message);
    }

    @Override
    public void onConnectionLost(String message) {
        clientUI.onConnectionLost(message);
    }
}
