package server;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.LinkedList;

public class Server extends JFrame {

    private ServerThread mainThread;
    private LinkedList<ClientThread> clients;

    //Elements graphiques
    private JLabel portLabel;
    private JTextField portTextField;
    private JButton startButton;
    private JTextArea serverLog;

    public Server () {

        clients = new LinkedList<>();

        setTitle("TCP Chat - Server");
        setSize(500, 400);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JPanel pane1 = new JPanel();
        pane1.setLayout(new BoxLayout(pane1, BoxLayout.X_AXIS));
        pane1.setSize(500, 80);
        portLabel = new JLabel("Port: ");
        portTextField = new JTextField();
        startButton = new JButton("Start Server");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(startButton.getText().equals("Start Server")) {
                    try {
                        int port = Integer.valueOf(portTextField.getText());
                        if(port < 1024 || port > 65535) {
                            throw new NumberFormatException();
                        }
                        start(port);
                        startButton.setText("Stop Server");
                        //portTextField.setEditable(false);
                    } catch(NumberFormatException ex) {
                        //write("Error: enter a port number between 1024 and 65635");
                    }
                } else if(startButton.getText().equals("Stop Server")) {
                    //stop();
                    startButton.setText("Start Server");
                    //portTextField.setEditable(true);
                }
            }
        });
        pane1.add(portLabel);
        pane1.add(portTextField);
        pane1.add(startButton);

        serverLog = new JTextArea();

        add(pane1);
        add(serverLog);

    }

    public void start(int port) {
        try {
            mainThread = new ServerThread(port, this);
            mainThread.start();
        } catch (IOException e) {
            //handle error
        }
    }

    public void onAcceptClient(ClientThread client) {
        clients.add(client);
    }

    public static void main(String args[]) {
        Server server = new Server();
        server.setVisible(true);
    }

}
