package client;

import client.gui.ClientGUI;
import client.model.ClientModel;
import com.google.gson.Gson;
import message.*;

import java.io.IOException;

public class JsonClient extends AbstractClient implements TCPClient {
    @Override
    public void run() {
        model = new ClientModel();
        gui = new ClientGUI(this);
        while (!isClosed) {
            if (isConnected) {
                loginClient();
                gui.setName(name);
                receiveMessage();
                //isConnected = false;
            }
        }
    }
    @Override
    public void disconnect() {
        try {
            if (isConnected) {
                Gson gson = new Gson();
                Message message = new Message(MessageType.DISCONNECT_USER);
                String jsonObject = gson.toJson(message);
                connection.sendJsonMessage(jsonObject);
                //connection.sendMessage(new Message(MessageType.DISCONNECT_USER));
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

    @Override
    public void sendMessage(String msg) {
        try {
            Gson gson = new Gson();
            Message message = new Message(getTime(), msg, MessageType.TEXT_MESSAGE);
            String jsonObject = gson.toJson(message);
            connection.sendJsonMessage(jsonObject);
            //connection.sendMessage(new Message(getTime(), msg, MessageType.TEXT_MESSAGE));
        }
        catch (IOException e) {
            gui.showError("Error while sending text message");
            isConnected = false;
            gui.updateUsers(model.getUsers());
        }
    }

    @Override
    public void receiveMessage() {
        while (isConnected) {
            try {
                //Message msg = connection.receiveMessage();
                Gson gson = new Gson();
                String jsonObject = connection.receiveJsonMessage();
                Message msg = gson.fromJson(jsonObject, Message.class);

                if (msg.getType() == MessageType.TEXT_MESSAGE) {
                    gui.addMessage(msg.getTime(), msg.getSender(), msg.getText());
                } else if (msg.getType() == MessageType.ADD_USER) {
                    model.addUser(msg.getText());
                    gui.updateUsers(model.getUsers());
                    gui.addNotification("User '" + msg.getText() + "' joined the chat!");
                } else if (msg.getType() == MessageType.DELETE_USER) {
                    model.deleteUser(msg.getText());
                    gui.updateUsers(model.getUsers());
                    gui.addNotification("User '" + msg.getText() + "' left the chat");
                }
            } catch (IOException | ClassNotFoundException e) {
                //e.printStackTrace();
                gui.showError("Connection with server is lost");
                isConnected = false;
                gui.updateUsers(model.getUsers());
            }
        }
    }

    @Override
    public void loginClient() {
        while (true) {
            try {
                //Message msg = connection.receiveMessage();
                Gson gson = new Gson();
                String jsonObject = connection.receiveJsonMessage();
                Message msg = gson.fromJson(jsonObject, Message.class);

                if (msg.getType() == MessageType.REQUEST_USER_NAME) {
                    name = gui.getUserName();
                    //connection.sendMessage(new Message(getTime(), name,  MessageType.REPLY_USER_NAME));
                    Message message = new Message(getTime(), name,  MessageType.REPLY_USER_NAME);
                    jsonObject = gson.toJson(message);
                    connection.sendJsonMessage(jsonObject);
                }
                //msg = connection.receiveMessage();
                jsonObject = connection.receiveJsonMessage();
                msg = gson.fromJson(jsonObject, Message.class);

                if (msg.getType() == MessageType.NAME_NOT_AVAILABLE) {
                    gui.showWarning("This name is not available. Enter another one...");
                } else if (msg.getType() == MessageType.NAME_ACCEPTED) {
                    gui.showInfo("Name is accepted!");
                    model.setUsers(msg.getUsers());
                    for (Message message : msg.getHistory()) gui.addMessage(message.getTime(), message.getSender(), message.getText());
                    break;
                }
            }
            catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                gui.showError("Problems with connection");
                isConnected = false;
                gui.updateUsers(model.getUsers());
            }
        }
    }
}
