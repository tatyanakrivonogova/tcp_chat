package connection;

import message.Message;

import java.io.*;

public interface Connection extends AutoCloseable {
    void sendMessage(Message msg) throws IOException;
    Message receiveMessage() throws IOException, ClassNotFoundException;
    @Override
    void close() throws Exception;
}
