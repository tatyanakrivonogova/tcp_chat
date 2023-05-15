package connection;

import java.io.IOException;
import java.net.Socket;

public class ConnectionFactory {
    private static ConnectionFactory INSTANCE = null;
    public static Connection createConnection(String type, Socket s) throws IllegalArgumentException, IOException {
        if (type.equalsIgnoreCase("serialization")) {
            return new SerializationConnection(s);
        } else if (type.equalsIgnoreCase("json")) {
            return new JsonConnection(s);
        } else {
            throw new IllegalArgumentException("Unknown type of connection");
        }
    }
    public static void getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConnectionFactory();
        }
    }
}