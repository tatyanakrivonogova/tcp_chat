package connection;

import message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection implements AutoCloseable {
    Socket s;
    final ObjectInputStream ois;
    final ObjectOutputStream oos;
    public Connection(Socket _s) throws IOException {
        s = _s;
        oos = new ObjectOutputStream(s.getOutputStream());
        ois = new ObjectInputStream(s.getInputStream());
    }

    public void sendMessage(Message msg) throws IOException {
        synchronized (oos) {
            oos.writeObject(msg);
            oos.flush();
        }
    }

    public Message receiveMessage() throws IOException, ClassNotFoundException {
        synchronized (ois) {
            return (Message) ois.readObject();
        }
    }
    public void sendJsonMessage(String jsonMessage) throws IOException {
        synchronized (oos) {
            oos.writeObject(jsonMessage);
            oos.flush();
        }
    }

    public String receiveJsonMessage() throws IOException, ClassNotFoundException {
        synchronized (ois) {
            return (String) ois.readObject();
        }
    }

    @Override
    public void close() throws Exception {
        ois.close();
        oos.close();
        s.close();
    }
}
