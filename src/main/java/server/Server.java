package server;

import java.util.Objects;

public class Server {
    public static void main(String ... args) {
        if (Objects.equals(args[0], "simple")) {
            SerializationServer server = new SerializationServer();
            server.run();
        } else if (Objects.equals(args[0], "xml")) {
            System.out.println("xml");
        }
    }
}
