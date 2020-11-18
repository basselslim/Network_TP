package client;

import server.ServerThread;

public class Client {

    private int port;
    private String host;
    private ClientThread clientThread;

    public Client(String host, int port){
        this.host = host;
        this.port = port;
    }

    public void start() {
        try {
            mainThread = new ClientThread(socket);
            mainThread.start();
        } catch (IOException e) {
            //handle error
        }
    }
}
