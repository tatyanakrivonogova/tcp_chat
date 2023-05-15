//package server;
//
//import connection.Connection;
//import message.Message;
//import message.MessageType;
//import org.apache.logging.log4j.Level;
//
//import java.io.IOException;
//import java.net.Socket;
//import java.net.SocketTimeoutException;
//import java.util.Map;
//import java.util.Timer;
//import java.util.TimerTask;
//
//public class SerializationServer extends AbstractServer implements TCPServer {
//    public SerializationServer(int timeout, int historySize) {
//        super(timeout, historySize);
//    }
//    public class ServerThread extends AbstractServer.ServerThread {
//        public ServerThread(Socket _socket) {
//            super(_socket);
//        }
//
//    }
//}