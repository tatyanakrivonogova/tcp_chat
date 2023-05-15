package connection;

import message.Message;

import java.io.*;
import java.net.Socket;

public class SerializationConnection implements Connection {
    Socket s;
    final ObjectInputStream ois;
    final ObjectOutputStream oos;
    public SerializationConnection(Socket _s) throws IOException {
        s = _s;
        oos = new ObjectOutputStream(s.getOutputStream());
        ois = new ObjectInputStream(s.getInputStream());
    }
    @Override
    public void sendMessage(Message msg) throws IOException {
        synchronized (oos) {
            oos.writeObject(msg);
            oos.flush();
        }
    }
    @Override
    public Message receiveMessage() throws IOException, ClassNotFoundException {
        synchronized (ois) {
            return (Message) ois.readObject();
        }
    }
    @Override
    public void close() throws Exception {
        ois.close();
        oos.close();
        s.close();
    }
}
