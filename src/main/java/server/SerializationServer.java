package server;

import connection.Connection;
import message.Message;
import message.MessageType;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class SerializationServer extends AbstractServer implements TCPServer {
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
                    e.printStackTrace();
                    gui.showError("Error while adding new client");
                    logger.log(Level.ERROR, "Error while adding new client");
                    logger.log(Level.ERROR, e.getMessage());
                }
            }
        }
        @Override
        public void chatting(Connection connection, String name) {
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
                        break;
                    }
                } catch (Exception e) {
                    gui.showError("Error while chatting");
                    logger.log(Level.ERROR, "Error while chatting");
                    logger.log(Level.ERROR, e.getMessage());
                    break;
                }
            }
        }
    }
    @Override
    public void acceptClient() {
        try {
            Socket newSocket = serverSocket.accept();
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