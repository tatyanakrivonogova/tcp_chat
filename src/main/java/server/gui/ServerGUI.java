package server.gui;


import server.TCPServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ServerGUI {
    private static final int NOTIFICATION_HEIGHT = 10;
    private static final int NOTIFICATION_WIDTH = 10;
    private final TCPServer server;
    private final JFrame frame = new JFrame("Server");
    private final JPanel panel = new JPanel();
    private final JTextArea notifications = new JTextArea(NOTIFICATION_HEIGHT, NOTIFICATION_WIDTH);
    private final JButton startButton = new JButton("START");
    private final JButton stopButton = new JButton("STOP");

    public ServerGUI(TCPServer _server) {
        server = _server;

        notifications.setEditable(false);
        notifications.setLineWrap(true);
        frame.add(new JScrollPane(notifications), BorderLayout.CENTER);
        panel.add(startButton);
        panel.add(stopButton);
        frame.add(panel, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                server.stop();
                System.exit(0);
            }
        });

        startButton.addActionListener(e -> {
            server.start(getPort());
        });
        stopButton.addActionListener(e -> server.stop());
        frame.setVisible(true);
    }
    private int getPort() {
        while (true) {
            String port = JOptionPane.showInputDialog(frame, "Enter server's port: ", JOptionPane.QUESTION_MESSAGE);
            try {
                return Integer.parseInt(port.trim());
            } catch (Exception e) {
                showError("Wrong number of port. Try again...");
            }
        }
    }

    public void showInfo(String text) {
        notifications.append(text + '\n');
    }
    public void showWarning(String text) {
        notifications.append(text + '\n');
    }
    public void showError(String text) {
        notifications.append(text + '\n');
    }
}
