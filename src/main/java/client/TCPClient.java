package client;

public interface TCPClient {
    void connect();
    void run();
    void disconnect();
    boolean isConnected();
    void sendMessage(String msg);
    void chatting();
    void loginClient();
    void close();
}
