package connection;

import com.google.gson.Gson;
import message.Message;

import java.io.*;
import java.net.Socket;

public class JsonConnection implements Connection {
    Socket s;
    final OutputStream os;
    final InputStream is;
    public JsonConnection(Socket _s) throws IOException {
        s = _s;
        os = s.getOutputStream();
        is = s.getInputStream();
    }
    @Override
    public void sendMessage(Message message) throws IOException {
        synchronized (os) {
            Gson gson = new Gson();
            String jsonMessage = gson.toJson(message);

            int length = jsonMessage.length();
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
    @Override
    public Message receiveMessage() throws IOException {
        synchronized (is) {
            Gson gson = new Gson();

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[4];
            nRead = is.read(data, 0, 4);
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
        os.close();
        is.close();
        s.close();
    }
}
