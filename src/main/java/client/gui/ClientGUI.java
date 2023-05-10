package client.gui;

import client.SerializationClient;
import client.TCPClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.util.Set;

public class ClientGUI {
    private static final int NAME_LIMIT = 30;
    private final TCPClient client;
    private String name = null;
    private final JFrame frame = new JFrame("My chat");
    private final ClientGUIForm clientPanel;

    public ClientGUI(SerializationClient _client) {
        client = _client;
        clientPanel = new ClientGUIForm(this, client);

        frame.setContentPane(clientPanel.panel);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (client.isConnected()) {
                    client.disconnect();
                }
                frame.dispose();
                for (Frame f : Frame.getFrames()) f.dispose();
                client.close();
            }
        });
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void setName(String _name) {name = _name;}
    public void setPort(int port) {
        clientPanel.setPort(port);
    }
    public void setIpAddress(InetAddress ip) {
        clientPanel.setIpAddress(ip);
    }
    public void setConnected(boolean value) { clientPanel.setConnected(value); }

    public String getUserName() {
        String name = JOptionPane.showInputDialog(frame, "Enter your name:", "");
        if (name == null) return "";
        name = (name.length() > NAME_LIMIT) ? name.substring(0, NAME_LIMIT) : name;
        return name;
    }
    public void showError(String text) {
        JOptionPane.showMessageDialog(frame, text, "ERROR", JOptionPane.ERROR_MESSAGE);
    }
    public void showWarning(String text) {
        JOptionPane.showMessageDialog(frame, text, "WARNING", JOptionPane.WARNING_MESSAGE);
    }
    public void showInfo(String text) {
        JOptionPane.showMessageDialog(frame, text, "INFO", JOptionPane.INFORMATION_MESSAGE);
    }
    public void addMessage(String dt, String sender, String text) {
        if (sender.equals(name)) {
            clientPanel.chat.append("\t" + dt + " " + sender + " : " + text + "\n");
        } else {
            clientPanel.chat.append(dt + " " + sender + " : " + text + "\n");
        }
        clientPanel.chat.setCaretPosition(clientPanel.chat.getDocument().getLength());
    }
    public void addNotification(String text) {
        clientPanel.chat.append(text + "\n");
        clientPanel.chat.setCaretPosition(clientPanel.chat.getDocument().getLength());
    }
    public void updateUsers(Set<String> users) {
        StringBuilder sb = new StringBuilder();
        if (client.isConnected()) {
            sb.append("Participants:\n");
            for (String user : users) {
                if (user.equals(name)) {
                    sb.append("YOU : ").append(user).append('\n');
                } else {
                    sb.append(user).append('\n');
                }
            }
        }
        clientPanel.participants.setText(sb.toString());
    }
    public int getPort() {
        while (true) {
            String port = JOptionPane.showInputDialog(frame, "Enter server's port: ", "2048");
            try {
                return Integer.parseInt(port.trim());
            } catch (Exception e) {
                showError("Wrong number of port. Try again...");
            }
        }
    }
    public String getAddress() {
        while (true) {
            String address = JOptionPane.showInputDialog(frame, "Enter server's address", "127.0.0.1");
            try {
                return address;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Error with typing address occurred",
                        "Wrong address typed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
