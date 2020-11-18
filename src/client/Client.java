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
    private JLabel pseudoLabel;
    private JTextField pseudoTextField;
    private JLabel hostLabel;
    private JTextField hostTextField;
    private JLabel portLabel;
    private JTextField portTextField;
    private JButton startButton;
    private JTextArea serverLog;

    public Client(){

        setTitle("TCP Chat - Client");
        setSize(500, 400);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JPanel pane1 = new JPanel();
        pane1.setLayout(new BoxLayout(pane1, BoxLayout.X_AXIS));
        pane1.setMaximumSize(new Dimension(500, 80));
        pane1.setBorder(new EmptyBorder(0, 10, 0, 10));
        pseudoLabel = new JLabel("Pseudo: ");
        pseudoTextField = new JTextField();
        hostLabel = new JLabel("Host: ");
        hostTextField = new JTextField();
        portLabel = new JLabel("Port: ");
        portTextField = new JTextField();
        startButton = new JButton("Join Chat");
        //getRootPane().setDefaultButton(startButton);
        //startButton.requestFocus();
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(startButton.getText().equals("Join Chat")) {
                    try {
                        String pseudo = pseudoTextField.getText();
                        String host = hostTextField.getText();
                        int port = Integer.valueOf(portTextField.getText());
                        if(port < 1024 || port > 65535) {
                            throw new NumberFormatException();
                        }
                        start(pseudo, host, port);
                    } catch(NumberFormatException ex) {
                        hostTextField.setText("???");
                        portTextField.setText("???");
                    }
                } else if(startButton.getText().equals("Leave Chat")) {
                    stop();
                }
            }
        });
        pane1.add(pseudoLabel);
        pane1.add(pseudoTextField);
        pane1.add(hostLabel);
        pane1.add(hostTextField);
        pane1.add(portLabel);
        pane1.add(portTextField);
        pane1.add(startButton);

        JPanel pane2 = new JPanel();
        pane2.setLayout(new BoxLayout(pane2, BoxLayout.X_AXIS));
        serverLog = new JTextArea();
        serverLog.setMargin(new Insets(10,10,10,10));
        pane2.add(serverLog);

        add(pane1);
        add(pane2);
    }

    public void start(String pseudo, String host, int port) {
        startButton.setText("Leave Chat");
        hostTextField.setEditable(false);
        portTextField.setEditable(false);

        clientThread = new ClientThread(host, port);
        clientThread.start();
        writeLog(pseudo + " joined the Chat");
    }

    public void stop() {
        startButton.setText("Join Chat");
        serverLog.setText("");
        hostTextField.setEditable(true);
        portTextField.setEditable(true);
    }

    public void writeLog (String s) {
        serverLog.append(s);
        serverLog.append("\n");
    }

    public static void main(String args[]) {
        Client client = new Client();
        client.setVisible(true);
    }
}
