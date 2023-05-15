package server;

import connection.Connection;
import connection.ConnectionFactory;
import message.Message;
import message.MessageType;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.gui.ServerGUI;
import server.model.ServerModel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ChatServer implements TCPServer {
    private final String type;
    protected static final Logger logger = LogManager.getLogger(ChatServer.class);
    protected int timeout;
    protected int historySize;
    protected ServerSocket serverSocket;
    protected ServerModel model;
    protected ServerGUI gui;
    protected volatile boolean isRunning = false;
    protected volatile boolean isClosed = false;
    public ChatServer(String _type, int _timeout, int _historySize) {
        type = _type;
        timeout = _timeout;
        historySize = _historySize;
    }
    public class ServerThread extends Thread {
        protected final Socket socket;
        protected String name;
        public ServerThread(Socket _socket) {
            socket = _socket;
        }
        public void run() {
            try {
                Connection connection = ConnectionFactory.createConnection(type, socket);
                name = addClient(connection);
                gui.showInfo("User " + name + " joined the chat");
                logger.log(Level.INFO, "User " + name + " joined the chat");
                chatting(connection, name);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                logger.log(Level.ERROR, "Error while creating connection by factory");
                logger.log(Level.ERROR, e.getMessage());
                gui.showError("Problem with creating connection. Try again...");
            } catch (IOException e) {
                gui.showError("Error while adding new user");
                logger.log(Level.ERROR, "Error while adding new user");
                logger.log(Level.ERROR, e.getMessage());
            }
        }
        public String addClient(Connection connection) {
            while (true) {
                try {
                    connection.sendMessage(new Message(MessageType.REQUEST_USER_NAME));
                    Message reply = connection.receiveMessage();
                    String newName = reply.getText();
                    if (reply.getType() == MessageType.REPLY_USER_NAME) {
                        if (newName.isEmpty() || model.getServerUsers().containsKey(newName)) {
                            connection.sendMessage(new Message(MessageType.NAME_NOT_AVAILABLE));
                        } else {
                            model.addUser(newName, connection);
                            connection.sendMessage(new Message(MessageType.NAME_ACCEPTED, model.getSetOfUsers(), model.getHistory()));
                            broadcastMessage(new Message("", newName, newName, MessageType.ADD_USER));
                            return newName;
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    gui.showError("Error while adding new client");
                    logger.log(Level.ERROR, "Error while adding new client");
                    logger.log(Level.ERROR, e.getMessage());
                }
            }
        }
        public void chatting(Connection connection, String name) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        connection.sendMessage(new Message(MessageType.PING));
                        System.out.println("ping from server to " + name);
                    } catch (IOException e) {
                        logger.log(Level.ERROR, "Error while ping");
                        cancel();
                    }
                }
            }, 0, 1000);

            while (!isClosed) {
                try {
                    Message msg = connection.receiveMessage();
                    if (msg.getType() == MessageType.TEXT_MESSAGE) {
                        msg.setName(model.getName(connection));
                        model.addHistory(msg);
                        broadcastMessage(new Message(msg.getTime(), name, msg.getText(), MessageType.TEXT_MESSAGE));
                        logger.log(Level.INFO, "Message from " + name);
                    } else if (msg.getType() == MessageType.DISCONNECT_USER) {
                        broadcastMessage(new Message(msg.getTime(), name, name, MessageType.DELETE_USER));
                        connection.close();
                        model.deleteUser(name);
                        gui.showInfo("User " + name + " left the chat");
                        logger.log(Level.INFO, "User " + name + " left the chat");
                        timer.cancel();
                        break;
                    }
                } catch (SocketTimeoutException e) {
                    gui.showError("Timeout exceeded, closing connection");
                    logger.log(Level.ERROR, "Timeout exceeded");
                    timer.cancel();
                    model.deleteUser(name);
                    try {
                        connection.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        logger.log(Level.ERROR, "Error while closing connection");
                    }
                    break;
                } catch (Exception e) {
                    gui.showError("Error while chatting");
                    logger.log(Level.ERROR, "Error while chatting");
                    logger.log(Level.ERROR, e.getMessage());
                    timer.cancel();
                    break;
                }
            }
        }
        public String getThreadName() { return name; }
    }
    @Override
    public void run() {
        model = new ServerModel(historySize);
        gui = new ServerGUI(this);
        while (!isClosed) {
            if (isRunning) {
                acceptClient();
            }
        }
    }
    @Override
    public void start(int port) {
        if (!isRunning) {
            try {
                serverSocket = new ServerSocket(port);
                isRunning = true;
                gui.showInfo("Server started!");
                logger.log(Level.INFO, "Server started");
            } catch (IOException e) {
                gui.showError("Error while starting server");
                logger.log(Level.ERROR, "Error while starting server");
                logger.log(Level.ERROR, e.getMessage());
            }
        } else {
            gui.showWarning("Server has already started");
        }
    }

    @Override
    public void stop() {
        if (isRunning) {
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    for (Map.Entry<String, Connection> entry : model.getServerUsers().entrySet()) {
                        entry.getValue().close();
                    }
                    serverSocket.close();
                    model.getServerUsers().clear();
                    isRunning = false;
                    gui.showInfo("Server stopped!");
                    logger.log(Level.INFO, "Server stopped");
                }
            } catch (Exception e) {
                gui.showError("Error while stopping server");
                logger.log(Level.ERROR, "Error while stopping server");
                logger.log(Level.ERROR, e.getMessage());
            }
        } else {
            gui.showWarning("Server has already stopped");
        }
    }
    @Override
    public void closeServer() {
        try {
            for (Map.Entry<String, Connection> user : model.getServerUsers().entrySet()) {
                user.getValue().close();
            }
            for (Thread t : model.getServerThreads()) t.interrupt();
            serverSocket.close();
            isClosed = true;
        } catch (Exception e) {
            logger.log(Level.ERROR, "Error while closing server");
            System.exit(-1);
        }
    }
    public void acceptClient() {
        try {
            Socket newSocket = serverSocket.accept();
            newSocket.setSoTimeout(timeout);
            ServerThread thread = new ServerThread(newSocket);
            model.addThread(thread);
            thread.start();
        } catch (IOException e) {
            if (isRunning && !isClosed) {
                gui.showError("Error while accepting new socket");
                logger.log(Level.ERROR, "Error while accepting new socket");
                logger.log(Level.ERROR, e.getMessage());
            }
        }
    }
    public void broadcastMessage(Message msg) {
        for (Map.Entry<String, Connection> entry : model.getServerUsers().entrySet()) {
            try {
                entry.getValue().sendMessage(msg);
            } catch (IOException e) {
                gui.showError("Error while broadcasting");
                logger.log(Level.ERROR, "Error while broadcasting");
                logger.log(Level.ERROR, e.getMessage());
            }
        }
    }
}