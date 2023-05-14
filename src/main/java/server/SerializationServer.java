package server;

import connection.Connection;
import message.Message;
import message.MessageType;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SerializationServer extends AbstractServer implements TCPServer {
    public SerializationServer(int timeout, int historySize) {
        super(timeout, historySize);
    }
    public class ServerThread extends AbstractServer.ServerThread {
        public ServerThread(Socket _socket) {
            super(_socket);
            //socket = _socket;
        }
        @Override
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
        @Override
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
                    break;
                } catch (Exception e) {
                    gui.showError("Connection has lost");
                    logger.log(Level.ERROR, "Error while chatting");
                    logger.log(Level.ERROR, e.getMessage());
                    timer.cancel();
                    break;
                }
            }
        }
    }
    @Override
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
    @Override
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