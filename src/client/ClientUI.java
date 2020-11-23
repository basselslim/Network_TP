package client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import client.ConnectionListener;
import client.Client;

public class ClientUI extends JFrame implements ConnectionListener{
    //Graphical Elements
    private JPanel topPanel;
    private JPanel centralPanel;
    private JPanel bottomPanel;
    private JLabel pseudoLabel;
    private JTextField pseudoTextField;
    private JLabel hostLabel;
    private JTextField hostTextField;
    private JLabel portLabel;
    private JTextField portTextField;
    private JButton startButton;
    private JTextArea chatMessage;
    private JButton sendButton;
    private JTextField messageField;

    public ClientUI(){

        Client client = new Client(this);

        setTitle("TCP Chat - Client");
        setSize(500, 400);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        //Top Panel
        topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.setMaximumSize(new Dimension(500, 80));
        topPanel.setBorder(new EmptyBorder(0, 10, 0, 10));

        //Pseudo
        pseudoLabel = new JLabel("Pseudo: ");
        pseudoTextField = new JTextField();
        pseudoTextField.setText("bass");
        topPanel.add(pseudoLabel);
        topPanel.add(pseudoTextField);

        //Host IP
        hostLabel = new JLabel("Host: ");
        hostTextField = new JTextField();
        hostTextField.setText("127.0.0.1");
        topPanel.add(hostLabel);
        topPanel.add(hostTextField);

        //Server Port
        portLabel = new JLabel("Port: ");
        portTextField = new JTextField();
        portTextField.setText("1234");
        topPanel.add(portLabel);
        topPanel.add(portTextField);

        //Connection Button
        startButton = new JButton("Join Chat");
        topPanel.add(startButton);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(startButton.getText().equals("Join Chat") && !client.isConnectionStarted()) {
                    try {
                        String pseudo = pseudoTextField.getText();
                        String host = hostTextField.getText();
                        int port = Integer.valueOf(portTextField.getText());
                        if(port < 1024 || port > 65535) {
                            throw new NumberFormatException();
                        }
                        client.start(pseudo, host, port);
                        startButton.setText("Leave Chat");
                        //writeMessage(pseudo + " joined the chat");
                    } catch(NumberFormatException ex) {
                        writeMessage("IP address or Port are incorrect");
                        hostTextField.setText("???");
                        portTextField.setText("???");
                    } catch (IOException ioException) {
                        writeMessage("Connection to " + hostTextField.getText() + " on port " + Integer.valueOf(portTextField.getText()) + " impossible");
                    }
                } else if(startButton.getText().equals("Leave Chat")) {
                    onConnectionLost(pseudoTextField.getText() + " left the chat.");
                }
            }
        });

        //Central Panel
        centralPanel = new JPanel();
        centralPanel.setLayout(new BoxLayout(centralPanel, BoxLayout.X_AXIS));
        //bottomPanel.setMaximumSize(new Dimension(500, 80));
        //centralPanel.setBorder(new EmptyBorder(0, 10, 0, 10));

        //Message Area
        chatMessage = new JTextArea();
        chatMessage.setMargin(new Insets(10,10,10,10));
        chatMessage.setEditable(false);

        //Scroll
        JScrollPane scroll = new JScrollPane(chatMessage);
        scroll.setBorder(new EmptyBorder(0,0,0,0));
        centralPanel.add(scroll);

        //Bottom Panel
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.setMaximumSize(new Dimension(500, 160));
        bottomPanel.setBorder(new EmptyBorder(0, 10, 0, 10));

        // Message Text Field
        messageField = new JTextField();
        messageField.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if(client.isConnectionStarted() && !messageField.getText().isEmpty()) {
                        client.writeMessage(messageField.getText());
                        messageField.setText("");
                    }
                }
            }
            public void keyTyped(KeyEvent e) {}
            public void keyReleased(KeyEvent e) {}
        });
        bottomPanel.add(messageField, BorderLayout.CENTER);

        // Send Button
        sendButton = new JButton("Send");
        sendButton.setPreferredSize(new Dimension(80, 24));
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(client.isConnectionStarted() && !messageField.getText().isEmpty()) {
                    client.writeMessage(messageField.getText());
                    messageField.setText("");
                }
            }
        });
        bottomPanel.add(sendButton, BorderLayout.EAST);

        this.add(topPanel);
        this.add(centralPanel);
        this.add(bottomPanel);
    }

    public synchronized void writeMessage(String message) {
        synchronized(chatMessage) {
            while(message.endsWith("\n")) {
                message = message.substring(0, message.length()-1);
            }
            if(!message.isEmpty()) {
                chatMessage.append(message + "\n");
                chatMessage.setCaretPosition(chatMessage.getDocument().getLength());
            }
        }
    }

    @Override
    public void onReceiveMessage(String message){
        writeMessage(message);
    }

    @Override
    public void onConnectionLost(String message){
        startButton.setText("Join the chat");
        pseudoTextField.setEditable(true);
        hostTextField.setEditable(true);
        portTextField.setEditable(true);
        writeMessage(message);
    }

    public synchronized void clear() {
        synchronized(chatMessage) {
            chatMessage.setText("");
        }
    }

    public static void main(String args[]) {
        ClientUI clientUI = new ClientUI();
        clientUI.setVisible(true);
    }
}
