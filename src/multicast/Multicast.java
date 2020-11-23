package multicast;

import client.ClientUI;
import server.Server;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Multicast extends JFrame {

    private MulticastSocket socket;
    private MulticastThread mainThread;
    private int port;
    private InetAddress address;

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

    public Multicast() {

        mainThread = null;

        setTitle("Multicast Chat");
        setSize(500, 400);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        //Top Panel
        JPanel topPanel = new JPanel();
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
        hostTextField.setText("224.0.0.1");
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
                if(startButton.getText().equals("Join Chat")) {
                    joinChat();
                } else if(startButton.getText().equals("Leave Chat")) {
                    leaveChat();
                }
            }
        });

        //Central Panel
        JPanel centralPanel = new JPanel();
        centralPanel.setLayout(new BoxLayout(centralPanel, BoxLayout.X_AXIS));

        //Message Area
        chatMessage = new JTextArea();
        chatMessage.setMargin(new Insets(10,10,10,10));
        chatMessage.setEditable(false);

        //Scroll
        JScrollPane scroll = new JScrollPane(chatMessage);
        scroll.setBorder(new EmptyBorder(0,0,0,0));
        centralPanel.add(scroll);

        //Bottom Panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.setMaximumSize(new Dimension(500, 160));
        bottomPanel.setBorder(new EmptyBorder(0, 10, 0, 10));

        // Message Text Field
        messageField = new JTextField();
        messageField.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if(mainThread != null && !messageField.getText().isEmpty()) {
                        sendMessage(pseudoTextField.getText() + " : " + messageField.getText());
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
                if(mainThread != null && !messageField.getText().isEmpty()) {
                    sendMessage(pseudoTextField.getText() + " : " + messageField.getText());
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

    public void joinChat() {
        try {
            String pseudo = pseudoTextField.getText();
            port = Integer.valueOf(portTextField.getText());
            address = InetAddress.getByName(hostTextField.getText());
            if(port < 1024 || port > 65535) {
                throw new NumberFormatException();
            }
            if (mainThread != null) {
                mainThread.end();
            }
            socket = new MulticastSocket(port);
            socket.joinGroup(address);
            mainThread = new MulticastThread(socket, this);
            mainThread.start();
            startButton.setText("Leave Chat");
            pseudoTextField.setEditable(false);
            hostTextField.setEditable(false);
            portTextField.setEditable(false);
            sendMessage(pseudo + " joined the chat");
        } catch(NumberFormatException ex) {
            writeMessage("IP address or Port are incorrect");
            hostTextField.setText("???");
            portTextField.setText("???");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void leaveChat() {
        startButton.setText("Join Chat");
        pseudoTextField.setEditable(true);
        hostTextField.setEditable(true);
        portTextField.setEditable(true);
        sendMessage(pseudoTextField.getText() + " left the chat.");
        mainThread.end();
        mainThread = null;
        chatMessage.setText("");
    }

    public void sendMessage(String message) {
        try {
            DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), address, port);
            socket.send(packet);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void onReceiveMessage(MulticastThread thread, String message) {
        writeMessage(message);
    }

    public static void main(String args[]) {
        Multicast multicast = new Multicast();
        multicast.setVisible(true);
    }

}
