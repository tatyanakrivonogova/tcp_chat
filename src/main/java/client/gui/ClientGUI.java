package client.gui;

import client.SerializationClient;
import client.TCPClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;
import java.util.Set;

public class ClientGUI {
    private static final int NAME_LIMIT = 30;
//    private static final int PARTICIPANTS_AREA_HEIGHT = 30;
//    private static final int PARTICIPANTS_AREA_WIDTH = 20;
//    private static final int CHAT_AREA_HEIGHT = 30;
//    private static final int CHAT_AREA_WIDTH = 15;
//    private static final int MESSAGE_SIZE = 40;
    private final TCPClient client;
    private String name = null;
    private final JFrame frame = new JFrame("My chat");
    private final ClientGUIForm clientPanel;
//    private final JTextArea chat = new JTextArea(CHAT_AREA_HEIGHT, CHAT_AREA_WIDTH);
//    private final JTextArea participants = new JTextArea(PARTICIPANTS_AREA_HEIGHT, PARTICIPANTS_AREA_WIDTH);
//    private final JTextField message = new JTextField(MESSAGE_SIZE);
//    private final JButton connectButton = new JButton("CONNECT");
//    private final JButton disconnectButton = new JButton("DISCONNECT");

    public ClientGUI(SerializationClient _client) {
        client = _client;
        clientPanel = new ClientGUIForm(client);

        frame.setContentPane(clientPanel.panel);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (client.isConnected()) {
                    client.disconnect();
                }
                System.exit(0);
            }
        });
        frame.pack();
        frame.setVisible(true);

//        chat.setEditable(false);
//        participants.setEditable(false);
//        frame.add(new JScrollPane(chat), BorderLayout.CENTER);
//        frame.add(new JScrollPane(participants), BorderLayout.EAST);
//
//        panel.add(message);
//        panel.add(connectButton);
//        panel.add(disconnectButton);
//        frame.add(panel, BorderLayout.SOUTH);
//        frame.pack();
//        frame.setLocationRelativeTo(null);
//        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
//
//        frame.addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent e) {
//                if (client.isConnected()) {
//                    client.disconnect();
//                }
//                System.exit(0);
//            }
//        });
//        connectButton.addActionListener(e -> {
//            client.connect();
//            connectButton.setFocusable(false);
//        });
//        disconnectButton.addActionListener(e -> {
//            client.disconnect();
//            disconnectButton.setFocusable(false);
//        });
//        message.addActionListener(e -> {
//            client.sendMessage(message.getText());
//            message.setText("");
//        });
//        frame.setVisible(true);
    }

    public void setName(String _name) {name = _name;}

    public String getUserName() {
        String name = JOptionPane.showInputDialog(frame, "Enter your name:", JOptionPane.QUESTION_MESSAGE);
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
    public void addMessage(String sender, String text) {
        if (name != null && Objects.equals(sender, name)) {
            clientPanel.chat.setAlignmentX(Component.RIGHT_ALIGNMENT);
        } else {
            clientPanel.chat.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        clientPanel.chat.append(sender + " : " + text + "\n");
        clientPanel.chat.revalidate();
    }
    public void updateUsers(Set<String> users) {
        StringBuilder sb = new StringBuilder();
        if (client.isConnected()) {
            sb.append("Participants:\n");
            for (String user : users) {
                sb.append(user).append('\n');
            }
        }
        clientPanel.participants.setText(sb.toString());
    }
    public int getPort() {
        while (true) {
            String port = JOptionPane.showInputDialog(frame, "Enter server's port: ", JOptionPane.QUESTION_MESSAGE);
            try {
                return Integer.parseInt(port.trim());
            } catch (Exception e) {
                showError("Wrong number of port. Try again...");
            }
        }
    }
    public String getAddress() {
        while (true) {
            String address = JOptionPane.showInputDialog(
                    frame, "Type server's address",
                    "Server address",
                    JOptionPane.QUESTION_MESSAGE
            );
            try {
                return address;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        frame, "Error with typing address occurred",
                        "Wrong address typed", JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}
