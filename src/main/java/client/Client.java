package client;

import configuration.Configuration;

import java.io.IOException;
import java.util.Objects;

public class Client {
    public static void main(String ... args) {
        Configuration configuration;
        try {
            configuration = new Configuration("config.properties");
        } catch (IOException e) {
            System.out.println("Impossible to read config file");
            return;
        }
        System.out.println(configuration.getType());
        if (Objects.equals(configuration.getType(), "serialization")) {
            AbstractClient client = new SerializationClient(configuration.getHistorySize());
            client.run();
        } else if (Objects.equals(configuration.getType(), "json")) {
            AbstractClient client = new JsonClient(configuration.getHistorySize());
            client.run();
        }
    }
}
