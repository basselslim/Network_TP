package client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Client extends JFrame{

    private int port;
    private String host;
    private ClientThread clientThread;

    //Elements graphiques
    private JLabel portLabel;
    private JTextField portTextField;
    private JButton startButton;
    private JTextArea serverLog;

    public Client(String host, int port){
        this.host = host;
        this.port = port;
    }

    public void start() {
        clientThread = new ClientThread(this.host, this.port);
        clientThread.run();
    }

    public static void main(String args[]) {
        Client client = new Client(args[0], new Integer(args[1]).intValue());
        client.start();
        client.setVisible(true);
    }
}
