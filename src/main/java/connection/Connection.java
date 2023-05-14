package connection;

import com.google.gson.Gson;
import message.Message;

import java.io.*;
import java.net.Socket;

public class Connection implements AutoCloseable {
    Socket s;
    final ObjectInputStream ois;
    final ObjectOutputStream oos;
    final OutputStream os;
    final InputStream is;
    public Connection(Socket _s) throws IOException {
        s = _s;
        os = s.getOutputStream();
        is = s.getInputStream();
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
    public void sendJsonMessage(Message message) throws IOException {
        synchronized (os) {
            Gson gson = new Gson();
            String jsonMessage = gson.toJson(message);

            int length = jsonMessage.length();
            //System.out.println("length " + length);
            byte[] bytes = new byte[4];
            for (int i = 0; i < 4; ++i) {
                bytes[4-i-1] = (byte) (length & 0xFF);
                length >>= 8;
            }
            os.write(bytes);
            os.write(jsonMessage.getBytes());
            os.flush();
        }
    }

    public Message receiveJsonMessage() throws IOException, ClassNotFoundException {
        synchronized (is) {
            Gson gson = new Gson();
            //String jsonObject = connection.receiveJsonMessage();

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[4];
            nRead = is.read(data, 0, 4);
            //System.out.println("get " + Arrays.toString(data));
            if (nRead != 4) throw new IOException("Wrong format of json message");
            int dataSize = 0;
            for (int i = 0; i < 4; ++i) {
                dataSize = (dataSize << 8) + (data[i] & 0xFF);
            }

            data = new byte[dataSize];
            nRead = is.read(data, 0, data.length);
            if (nRead != dataSize) throw new IOException("Wrong format of json message");
            buffer.write(data, 0, nRead);
            buffer.flush();

            return gson.fromJson(buffer.toString(), Message.class);
        }
    }

    @Override
    public void close() throws Exception {
        ois.close();
        oos.close();
        s.close();
    }
}
