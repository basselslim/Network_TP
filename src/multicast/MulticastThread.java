package multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

/**
 * Thread managing the reception of new messages
 * @author Bassel Slim & Clément Parret
 */
public class MulticastThread extends Thread {

    /**Socket on which to listen*/
    private MulticastSocket socket;
    /**Client connected to the chat*/
    private Multicast parent;

    /**
     * Instantiates the thread
     * @param socket Socket on which to listen
     * @param parent Client connected to the chat
     * @throws IOException
     */
    public MulticastThread (MulticastSocket socket, Multicast parent) throws IOException {
        this.socket = socket;
        this.parent = parent;
    }

    /**
     * Main action of the thread, it listens for new messages
     */
    public void run () {
        try {
            while (true) {
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String message = new String(packet.getData());
                parent.onReceiveMessage(message);
            }
        } catch (IOException e) {
            //handle error
        }
        end();
    }

    /**
     * Terminates the thread
     */
    public void end () {
        socket.close();
        this.stop();
    }

}
