package server;

import configuration.Configuration;

import java.io.IOException;
import java.util.Objects;

public class Server {
    public static void main(String ... args) {
        Configuration configuration;
        try {
            configuration = new Configuration("config.properties");
        } catch (IOException e) {
            System.out.println("Impossible to read config file");
            return;
        }
        if (Objects.equals(configuration.getType(), "serialization")) {
            AbstractServer server = new SerializationServer(configuration.getTimeout(), configuration.getHistorySize());
            server.run();
        } else if (Objects.equals(configuration.getType(), "json")) {
            AbstractServer server = new JsonServer(configuration.getTimeout(), configuration.getHistorySize());
            server.run();
        }
    }
}