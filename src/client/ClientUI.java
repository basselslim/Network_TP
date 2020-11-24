package client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import client.Client;

/**
 * User interface for the client
 * @author Bassel Slim & Clément Parret
 */
public class ClientUI extends JFrame {

    //Graphical Elements
    /**Top panel*/
    private JPanel topPanel;
    /**Central panel*/
    private JPanel centralPanel;
    /**Bottom panel*/
    private JPanel bottomPanel;
    /**Label for the pseudo*/
    private JLabel pseudoLabel;
    /**Text input for the pseudo*/
    private JTextField pseudoTextField;
    /**Label for the host*/
    private JLabel hostLabel;
    /**Text input for the host*/
    private JTextField hostTextField;
    /**Label for the port*/
    private JLabel portLabel;
    /**Text input for the port*/
    private JTextField portTextField;
    /**Button to connect to or disconnect from the server*/
    private JButton startButton;
    /**Text area to display messages*/
    private JTextArea chatMessage;
    /**Button to send a message*/
    private JButton sendButton;
    /**Text input in which to type messages*/
    private JTextField messageField;

    /**
     * Instantiates the user interface
     */
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
                        pseudoTextField.setEditable(false);
                        hostTextField.setEditable(false);
                        portTextField.setEditable(false);
                        writeMessage(pseudo + " joined the chat");
                    } catch(NumberFormatException ex) {
                        writeMessage("IP address or Port are incorrect");
                        hostTextField.setText("???");
                        portTextField.setText("???");
                    } catch (IOException ioException) {
                        writeMessage("Connection to " + hostTextField.getText() + " on port " + Integer.valueOf(portTextField.getText()) + " impossible");
                    }
                } else if(startButton.getText().equals("Leave Chat")) {
                    client.stop();
                    startButton.setText("Join Chat");
                    pseudoTextField.setEditable(true);
                    hostTextField.setEditable(true);
                    portTextField.setEditable(true);
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

    /**
     * Writes a message in the dedicated text area
     * @param message The message to display
     */
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

    /**
     * Manages the reception of a message
     * @param message The message that was received
     */
    public void onReceiveMessage(String message){
        writeMessage(message);
    }

    /**
     * Manages the disconnection from the server
     * @param message The message to display
     */
    public void onConnectionLost(String message){
        startButton.setText("Join Chat");
        pseudoTextField.setEditable(true);
        hostTextField.setEditable(true);
        portTextField.setEditable(true);
        writeMessage(message);
    }

    public static void main(String args[]) {
        ClientUI clientUI = new ClientUI();
        clientUI.setVisible(true);
    }
}
