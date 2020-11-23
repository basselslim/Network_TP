package multicast;

import server.ClientThread;
import server.Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

public class MulticastThread extends Thread {
    private MulticastSocket socket;
    private Multicast parent;

    public MulticastThread (MulticastSocket socket, Multicast parent) throws IOException {
        this.socket = socket;
        this.parent = parent;
    }

    public void run () {
        try {
            while (true) {
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String message = new String(packet.getData());
                parent.onReceiveMessage(this, message);
            }
        } catch (IOException e) {
            //handle error
        }
        end();
    }

    public void end () {
        socket.close();
        this.stop();
    }

}
