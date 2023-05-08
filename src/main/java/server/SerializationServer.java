package server;

import connection.Connection;
import message.Message;
import message.MessageType;
import server.gui.ServerGUI;
import server.model.ServerModel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class SerializationServer implements TCPServer {
    private ServerSocket serverSocket;
    private ServerModel model;
    private ServerGUI gui;
    private volatile boolean isRunning = false;

    private class ServerThread extends Thread {
        private final Socket socket;
        public ServerThread(Socket _socket) {
            socket = _socket;
        }
        @Override
        public void run() {
            try {
                Connection connection = new Connection(socket);
                String name = addClient(connection);
                gui.showInfo("User " + name + " joined the chat");
                chatting(connection, name);
            } catch (IOException e) {
                gui.showError("Error while adding new user");
            }
        }
        private String addClient(Connection connection) {
            while (true) {
                try {
                    connection.sendMessage(new Message(MessageType.REQUEST_USER_NAME));
                    Message reply = connection.receiveMessage();
                    String newName = reply.getText();
                    if (reply.getType() == MessageType.REPLY_USER_NAME && !newName.isEmpty()) {
                        if (model.getServerUsers().containsKey(newName)) {
                            connection.sendMessage(new Message(MessageType.NAME_NOT_AVAILABLE));
                        } else {
                            model.addUser(newName, connection);
                            connection.sendMessage(new Message(MessageType.NAME_ACCEPTED));
                            broadcastMessage(new Message(newName, MessageType.ADD_USER));
                            return newName;
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    gui.showError("Error while adding new client");
                }
            }
        }
        public void chatting(Connection connection, String name) {
            while (true) {
                try {
                    Message msg = connection.receiveMessage();
                    if (msg.getType() == MessageType.TEXT_MESSAGE) {
                        broadcastMessage(new Message(name + " : " + msg.getText(), MessageType.TEXT_MESSAGE));
                    } else if (msg.getType() == MessageType.DISCONNECT_USER) {
                        broadcastMessage(new Message(name, MessageType.DELETE_USER));
                        connection.close();
                        model.deleteUser(name);
                        gui.showInfo("User " + name + " left the chat");
                        break;
                    }
                } catch (Exception e) {
                    gui.showError("Error while chatting");
                    break;
                }
            }
        }
    }
    @Override
    public void run() {
        model = new ServerModel();
        gui = new ServerGUI(this);
        while (true) {
            if (isRunning) {
                acceptClient();
            }
        }
    }

    @Override
    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            isRunning = true;
            gui.showInfo("Server started!");
        } catch (IOException e) {
            gui.showError("Error while starting server");
        }
    }

    @Override
    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                for (Map.Entry<String, Connection> entry : model.getServerUsers().entrySet()) {
                    entry.getValue().close();
                }
                serverSocket.close();
                model.getServerUsers().clear();
                isRunning = false;
                gui.showInfo("Server stopped!");
            }
        } catch (Exception e) {
            gui.showError("Error while stopping server");
        }
    }

    @Override
    public void acceptClient() {
        while (true) {
            try {
                Socket newSocket = serverSocket.accept();
                ServerThread thread = new ServerThread(newSocket);
                thread.start();
            } catch (IOException e) {
                gui.showError("Error while accepting new socket");
                break;
            }
        }
    }

    @Override
    public void broadcastMessage(Message msg) {
        for (Map.Entry<String, Connection> entry : model.getServerUsers().entrySet()) {
            try {
                entry.getValue().sendMessage(msg);
            } catch (IOException e) {
                gui.showError("Error while broadcasting");
            }
        }
    }

}
