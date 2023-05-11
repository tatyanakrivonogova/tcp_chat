package client;

import java.util.Objects;

public class Client {
    public static void main(String ... args) {
        if (Objects.equals(args[0], "simple")) {
            AbstractClient client = new SerializationClient();
            client.run();
        } else if (Objects.equals(args[0], "json")) {
            System.out.println("json");
            AbstractClient client = new JsonClient();
            client.run();
        }
    }
}
