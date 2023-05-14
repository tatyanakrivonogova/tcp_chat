package server;

import connection.Connection;
import message.Message;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.gui.ServerGUI;
import server.model.ServerModel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public abstract class AbstractServer implements TCPServer {
    protected static final Logger logger = LogManager.getLogger(SerializationServer.class);
    protected int timeout;
    protected int historySize;
    protected ServerSocket serverSocket;
    protected ServerModel model;
    protected ServerGUI gui;
    protected volatile boolean isRunning = false;
    protected volatile boolean isClosed = false;
    public AbstractServer(int _timeout, int _historySize) {
        timeout = _timeout;
        historySize = _historySize;
    }
    public abstract class ServerThread extends Thread {
        protected final Socket socket;
        protected String name;
        public ServerThread(Socket _socket) {
            socket = _socket;
        }
        @Override
        public void run() {
            try {
                Connection connection = new Connection(socket);
                name = addClient(connection);
                gui.showInfo("User " + name + " joined the chat");
                logger.log(Level.INFO, "User " + name + " joined the chat");
                chatting(connection, name);
            } catch (IOException e) {
                gui.showError("Error while adding new user");
                logger.log(Level.ERROR, "Error while adding new user");
                logger.log(Level.ERROR, e.getMessage());
            }
        }
        abstract public String addClient(Connection connection);
        abstract public void chatting(Connection connection, String name);
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
    @Override
    abstract public void acceptClient();
    @Override
    abstract public void broadcastMessage(Message msg);
}