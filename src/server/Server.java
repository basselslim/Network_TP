package server;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Scanner;

public class Server extends JFrame {

    private ServerThread mainThread;
    private LinkedList<ClientThread> clients;
    private final String pathToHistory = "chat_history.txt";

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
        pane1.setMaximumSize(new Dimension(500, 80));
        pane1.setBorder(new EmptyBorder(0, 10, 0, 10));
        portLabel = new JLabel("Port: ");
        portTextField = new JTextField();
        startButton = new JButton("Start Server");
        //getRootPane().setDefaultButton(startButton);
        //startButton.requestFocus();
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
                    } catch(NumberFormatException ex) {
                        portTextField.setText("???");
                    }
                } else if(startButton.getText().equals("Stop Server")) {
                    stop();
                }
            }
        });
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

    public void start (int port) {
        serverLog.setText("");
        startButton.setText("Stop Server");
        portTextField.setEditable(false);
        try {
            mainThread = new ServerThread(port, this);
            mainThread.start();
            writeLog("Server started");
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeLog(getHistory());
    }

    public void stop() {
        startButton.setText("Start Server");
        portTextField.setEditable(true);
        mainThread.end();
        for(ClientThread client : clients) {
            client.end();
        }
        clients.clear();
        writeLog("Server stopped");
    }

    public void onAcceptClient (ClientThread client) {
        clients.add(client);
        String pseudo = "" + client.getClientSocket().getPort();
        String message = pseudo + " joined the chat";
        writeLog(message);
        addToHistory(message);
        for (ClientThread c : clients) {
            c.sendMessage(message);
        }
    }

    public void onDisconnectClient (ClientThread client) {
        clients.remove(client);
        String pseudo = "" + client.getClientSocket().getPort();
        String message = pseudo + " left the chat";
        writeLog(message);
        addToHistory(message);
        for (ClientThread c : clients) {
            c.sendMessage(message);
        }
    }

    public void onReceiveMessage (ClientThread client, String s) {
        String pseudo = "" + client.getClientSocket().getPort();
        String message = pseudo + " : " + s;
        writeLog(message);
        addToHistory(message);
        for (ClientThread c : clients) {
            c.sendMessage(message);
        }
    }

    public void writeLog (String s) {
        serverLog.append(s);
        if (s.charAt(s.length()-1) != '\n') {
            serverLog.append("\n");
        }
    }

    public void addToHistory(String s) {
        try {
            File history = new File(pathToHistory);
            history.createNewFile();
            PrintStream historyStream = new PrintStream(new FileOutputStream(history, true), true);
            historyStream.append(s + "\n");
            historyStream.close();
        } catch (IOException e) {
            writeLog("Error while creating or accessing the history");
        }
    }

    public String getHistory() {
        String res = "";
        try {
            File history = new File(pathToHistory);
            history.createNewFile();
            Scanner reader = new Scanner(history);
            while (reader.hasNextLine()) {
                res += reader.nextLine() + "\n";
            }
            reader.close();
        } catch (IOException e) {
            writeLog("Error while accessing the history");
        }
        return res;
    }

    public static void main(String args[]) {
        Server server = new Server();
        server.setVisible(true);
    }

}
