package client;

import configuration.Configuration;
import connection.ConnectionFactory;

import java.io.IOException;

public class Client {
    public static void main(String ... args) {
        ConnectionFactory.getInstance();
        Configuration configuration;
        try {
            configuration = new Configuration("config.properties");
        } catch (IOException e) {
            System.out.println("Impossible to read config file");
            return;
        }
        System.out.println(configuration.getType());
        ChatClient client = new ChatClient(configuration.getType(), configuration.getHistorySize());
        client.run();
    }
}
