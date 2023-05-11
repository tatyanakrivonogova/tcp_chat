package client;

import client.gui.ClientGUI;
import client.model.ClientModel;
import connection.Connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDateTime;

abstract public class AbstractClient implements TCPClient {
    protected volatile boolean isConnected = false;
    protected volatile boolean isClosed = false;
    protected String name;
    protected Connection connection;
    protected ClientModel model;
    protected ClientGUI gui;
    @Override
    public void connect() {
        if (!isConnected) {
            while (true) {
                try {
                    InetAddress ipAddress = InetAddress.getByName(gui.getAddress());
                    int port = gui.getPort();
                    Socket s = new Socket(ipAddress, port);
                    connection = new Connection(s);
                    isConnected = true;
                    gui.setPort(port);
                    gui.setIpAddress(ipAddress);
                    gui.setConnected(true);
                    gui.showInfo("Connected successfully!");
                    break;
                }
                catch (IOException e) {
                    gui.showWarning("Impossible to connect to this port. Enter another port...");
                }
            }
        } else {
            gui.showWarning("Connection has already existed!");
        }
    }

    @Override
    abstract public void run();
    @Override
    abstract public void disconnect();
    @Override
    public boolean isConnected() {
        return isConnected;
    }
    @Override
    abstract public void sendMessage(String msg);
    @Override
    abstract public void receiveMessage();
    @Override
    abstract public void loginClient();
    protected String getTime() {
        return LocalDateTime.now().getHour() + ":" + (LocalDateTime.now().getMinute() > 9 ? LocalDateTime.now().getMinute() : "0" + LocalDateTime.now().getMinute());
    }
    @Override
    public void close() {
        isClosed = true;
    }
}
