package client;

import java.io.*;

public class ClientThread extends Thread {
    private ConnectionListener listener;
    private BufferedReader reader;
    private boolean state;

    public ClientThread(ConnectionListener listener, BufferedReader reader) {
        this.listener = listener;
        this.reader = reader;
    }

    public synchronized boolean isRunning() {
        return state;
    }

    public synchronized void kill() {
        state = false;
    }

    public void run() {
        state = true;
        try {
            String message;
            while((message = reader.readLine()) != null) {
                listener.onReceiveMessage(message);
            }
            listener.onConnectionLost("Connection lost...");
        } catch (IOException e) {
            listener.onConnectionLost("Client disconnected");
        }
        kill();
    }
}