package client;

public interface ConnectionListener {

    public void onReceiveMessage(String message);

    public void onConnectionLost(String message);
}