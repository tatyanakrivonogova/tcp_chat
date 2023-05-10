package client;

import java.util.Objects;

public class Client {
    public static void main(String ... args) {
        if (Objects.equals(args[0], "simple")) {
            SerializationClient client = new SerializationClient();
            client.run();
        } else if (Objects.equals(args[0], "xml")) {
            System.out.println("xml");
        }
    }
}
