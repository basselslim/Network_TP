package server;

import java.io.*;
import java.net.*;

public class ServerThread extends Thread{

    private ServerSocket socket;
    private Server server;

    public ServerThread (int port, Server serv) throws IOException {
        socket = new ServerSocket(port);
        server = serv;
    }

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

    public void end () {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.stop();
    }

}
