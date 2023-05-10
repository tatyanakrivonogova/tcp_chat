package server;

import message.Message;

public interface TCPServer {
    void run();
    void start(int port);
    void stop();
    void acceptClient();
    void broadcastMessage(Message msg);
    void closeServer();
}
