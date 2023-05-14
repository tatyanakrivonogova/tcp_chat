package client;

public interface TCPClient {
    void connect();
    void run();
    void disconnect();
    boolean isConnected();
    void sendMessageFromClient(String msg);
    void chatting();
    void loginClient();
    void close();
}
