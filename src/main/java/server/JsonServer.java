//package server;
//
//import connection.Connection;
//import message.Message;
//import message.MessageType;
//import org.apache.logging.log4j.Level;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import server.gui.ServerGUI;
//import server.model.ServerModel;
//
//import java.io.IOException;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.Map;
//
//public class JsonServer implements TCPServer {
//    private static final Logger logger = LogManager.getLogger(SerializationServer.class);
//    private ServerSocket serverSocket;
//    private ServerModel model;
//    private ServerGUI gui;
//    private volatile boolean isRunning = false;
//    private volatile boolean isClosed = false;
//    public class ServerThread extends Thread {
//        private final Socket socket;
//        private String name;
//        public ServerThread(Socket _socket) {
//            socket = _socket;
//        }
//        @Override
//        public void run() {
//            try {
//                Connection connection = new Connection(socket);
//                name = addClient(connection);
//                gui.showInfo("User " + name + " joined the chat");
//                logger.log(Level.INFO, "User " + name + " joined the chat");
//                chatting(connection, name);
//            } catch (IOException e) {
//                gui.showError("Error while adding new user");
//                logger.log(Level.ERROR, "Error while adding new user");
//                logger.log(Level.ERROR, e.getMessage());
//            }
//        }
//        private String addClient(Connection connection) {
//            while (true) {
//                try {
//                    connection.sendMessage(new Message(MessageType.REQUEST_USER_NAME));
//                    Message reply = connection.receiveMessage();
//                    String newName = reply.getText();
//                    if (reply.getType() == MessageType.REPLY_USER_NAME) {
//                        if (newName.isEmpty() || model.getServerUsers().containsKey(newName)) {
//                            connection.sendMessage(new Message(MessageType.NAME_NOT_AVAILABLE));
//                        } else {
//                            model.addUser(newName, connection);
//                            connection.sendMessage(new Message(MessageType.NAME_ACCEPTED, model.getSetOfUsers(), model.getHistory()));
//                            broadcastMessage(new Message("", newName, newName, MessageType.ADD_USER));
//                            return newName;
//                        }
//                    }
//                } catch (IOException | ClassNotFoundException e) {
//                    e.printStackTrace();
//                    gui.showError("Error while adding new client");
//                    logger.log(Level.ERROR, "Error while adding new client");
//                    logger.log(Level.ERROR, e.getMessage());
//                }
//            }
//        }
//        public void chatting(Connection connection, String name) {
//            while (!isClosed) {
//                try {
//                    Message msg = connection.receiveMessage();
//                    if (msg.getType() == MessageType.TEXT_MESSAGE) {
//                        msg.setName(model.getName(connection));
//                        model.addHistory(msg);
//                        broadcastMessage(new Message(msg.getTime(), name, msg.getText(), MessageType.TEXT_MESSAGE));
//                        logger.log(Level.INFO, "Message from " + name);
//                    } else if (msg.getType() == MessageType.DISCONNECT_USER) {
//                        broadcastMessage(new Message(msg.getTime(), name, name, MessageType.DELETE_USER));
//                        connection.close();
//                        model.deleteUser(name);
//                        gui.showInfo("User " + name + " left the chat");
//                        logger.log(Level.INFO, "User " + name + " left the chat");
//                        break;
//                    }
//                } catch (Exception e) {
//                    gui.showError("Error while chatting");
//                    logger.log(Level.ERROR, "Error while chatting");
//                    logger.log(Level.ERROR, e.getMessage());
//                }
//            }
//        }
//        public String getThreadName() { return name; }
//    }
//    @Override
//    public void run() {
//        model = new ServerModel();
//        gui = new ServerGUI(this);
//        while (!isClosed) {
//            if (isRunning) {
//                acceptClient();
//            }
//        }
//    }
//    @Override
//    public void start(int port) {
//        if (!isRunning) {
//            try {
//                serverSocket = new ServerSocket(port);
//                isRunning = true;
//                gui.showInfo("Server started!");
//                logger.log(Level.INFO, "Server started");
//            } catch (IOException e) {
//                gui.showError("Error while starting server");
//                logger.log(Level.ERROR, "Error while starting server");
//                logger.log(Level.ERROR, e.getMessage());
//            }
//        } else {
//            gui.showWarning("Server has already started");
//        }
//    }
//
//    @Override
//    public void stop() {
//        if (isRunning) {
//            try {
//                if (serverSocket != null && !serverSocket.isClosed()) {
//                    for (Map.Entry<String, Connection> entry : model.getServerUsers().entrySet()) {
//                        entry.getValue().close();
//                    }
//                    serverSocket.close();
//                    model.getServerUsers().clear();
//                    isRunning = false;
//                    gui.showInfo("Server stopped!");
//                    logger.log(Level.INFO, "Server stopped");
//                }
//            } catch (Exception e) {
//                gui.showError("Error while stopping server");
//                logger.log(Level.ERROR, "Error while stopping server");
//                logger.log(Level.ERROR, e.getMessage());
//            }
//        } else {
//            gui.showWarning("Server has already stopped");
//        }
//    }
//    @Override
//    public void closeServer() {
//        try {
//            for (Map.Entry<String, Connection> user : model.getServerUsers().entrySet()) {
//                user.getValue().close();
//            }
//            for (Thread t : model.getServerThreads()) t.interrupt();
//            serverSocket.close();
//            isClosed = true;
//        } catch (Exception e) {
//            logger.log(Level.ERROR, "Error while closing server");
//            System.exit(-1);
//        }
//    }
//    @Override
//    public void acceptClient() {
//        try {
//            Socket newSocket = serverSocket.accept();
//            ServerThread thread = new ServerThread(newSocket);
//            model.addThread(thread);
//            thread.start();
//        } catch (IOException e) {
//            if (isRunning && !isClosed) {
//                gui.showError("Error while accepting new socket");
//                logger.log(Level.ERROR, "Error while accepting new socket");
//                logger.log(Level.ERROR, e.getMessage());
//            }
//        }
//    }
//    @Override
//    public void broadcastMessage(Message msg) {
//        for (Map.Entry<String, Connection> entry : model.getServerUsers().entrySet()) {
//            try {
//                entry.getValue().sendMessage(msg);
//            } catch (IOException e) {
//                gui.showError("Error while broadcasting");
//                logger.log(Level.ERROR, "Error while broadcasting");
//                logger.log(Level.ERROR, e.getMessage());
//            }
//        }
//    }
//}