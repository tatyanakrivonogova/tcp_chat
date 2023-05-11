package server;

import java.util.Objects;

public class Server {
    public static void main(String ... args) {
        if (Objects.equals(args[0], "simple")) {
            AbstractServer server = new SerializationServer();
            server.run();
        } else if (Objects.equals(args[0], "json")) {
            System.out.println("json");
        }
    }
}