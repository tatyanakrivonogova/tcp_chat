package client;

import client.gui.ClientGUI;
import client.model.ClientModel;
import connection.Connection;
import connection.ConnectionFactory;
import message.Message;
import message.MessageType;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class ChatClient implements TCPClient {
    private final String type;
    protected volatile boolean isConnected = false;
    protected volatile boolean isClosed = false;
    protected final int historySize;
    protected String name;
    protected Connection connection;
    protected ClientModel model;
    protected ClientGUI gui;
    public ChatClient(String _type, int _historySize) {
        type = _type;
        historySize = _historySize;
    }
    public void connect() {
        if (!isConnected) {
            while (true) {
                try {
                    InetAddress ipAddress = InetAddress.getByName(gui.getAddress());
                    System.out.println(ipAddress);
                    int port = gui.getPort();
                    System.out.println(port);
                    Socket s = new Socket(ipAddress, port);
                    //connection = new SerializationConnection(s);
                    connection = ConnectionFactory.createConnection(type, s);
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
    public void run() {
        model = new ClientModel(historySize);
        gui = new ClientGUI(this);
        while (!isClosed) {
            if (isConnected) {
                loginClient();
                chatting();
            }
        }
    }
    public void disconnect() {
        try {
            if (isConnected) {
                connection.sendMessage(new Message(MessageType.DISCONNECT_USER));
                isConnected = false;
                model.getUsers().clear();
                gui.updateUsers(model.getUsers());
                gui.setConnected(false);
            } else {
                gui.showWarning("You have already disconnected");
            }
        }
        catch (IOException e) {
            gui.showError("Error while disconnection");
            isConnected = false;
            gui.updateUsers(model.getUsers());
        }
    }
    public boolean isConnected() {
        return isConnected;
    }
    public void sendMessageFromClient(String msg) {
        try {
            connection.sendMessage(new Message(getTime(), msg, MessageType.TEXT_MESSAGE));
        }
        catch (IOException e) {
            gui.showError("Error while sending text message");
            isConnected = false;
            gui.updateUsers(model.getUsers());
        }
    }
    public void chatting() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    connection.sendMessage(new Message(MessageType.PING));
                    System.out.println("ping from client");
                } catch (IOException e) {
                    timer.cancel();
                    isConnected = false;
                    System.out.println("Error while ping");
                }
            }
        }, 0, 1000);
        while (isConnected) {
            try {
                Message msg = connection.receiveMessage();
                if (msg.getType() == MessageType.TEXT_MESSAGE) {
                    model.addMessage(msg);
                    gui.updateChat(model.getChatMessages());
                } else if (msg.getType() == MessageType.ADD_USER) {
                    model.addUser(msg.getText());
                    gui.updateUsers(model.getUsers());
                    gui.addNotification("User '" + msg.getText() + "' joined the chat!");
                } else if (msg.getType() == MessageType.DELETE_USER) {
                    model.deleteUser(msg.getText());
                    gui.updateUsers(model.getUsers());
                    gui.addNotification("User '" + msg.getText() + "' left the chat");
                }
            } catch (SocketTimeoutException e) {
                gui.showError("Timeout exceeded, closing connection");
                disconnect();
                timer.cancel();
                try {
                    connection.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } catch (IOException | ClassNotFoundException e) {
                gui.showError("Connection with server has lost");
                isConnected = false;
                gui.updateUsers(model.getUsers());
                timer.cancel();
            }
        }
    }
    public void loginClient() {
        while (true) {
            try {
                Message msg = connection.receiveMessage();

                if (msg.getType() == MessageType.REQUEST_USER_NAME) {
                    name = gui.getUserName();
                    connection.sendMessage(new Message(getTime(), name,  MessageType.REPLY_USER_NAME));
                }
                msg = connection.receiveMessage();

                if (msg.getType() == MessageType.NAME_NOT_AVAILABLE) {
                    gui.showWarning("This name is not available. Enter another one...");
                } else if (msg.getType() == MessageType.NAME_ACCEPTED) {
                    gui.showInfo("Name is accepted!");
                    gui.setName(name);
                    model.setUsers(msg.getUsers());
                    model.setChatMessages(msg.getHistory());
                    gui.updateChat(model.getChatMessages());
                    break;
                }
            }
            catch (IOException | ClassNotFoundException e) {
                gui.showError("Problems with connection");
                e.printStackTrace();
                isConnected = false;
                gui.updateUsers(model.getUsers());
            }
        }
    }
    protected String getTime() {
        return LocalDateTime.now().getHour() + ":" + (LocalDateTime.now().getMinute() > 9 ? LocalDateTime.now().getMinute() : "0" + LocalDateTime.now().getMinute());
    }
    public void close() {
        isClosed = true;
    }
}
