package server.gui;


import server.TCPServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ServerGUI {
    private final TCPServer server;
    private final JFrame frame = new JFrame("Server");
    private final ServerGUIForm serverPanel;

    public ServerGUI(TCPServer _server) {
        server = _server;
        serverPanel = new ServerGUIForm(this, server);

        frame.setContentPane(serverPanel.panel);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
                for (Frame f : Frame.getFrames()) f.dispose();
                server.closeServer();
            }
        });
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

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
    public void showInfo(String text) {
        serverPanel.notifications.append(text + '\n');
    }
    public void showWarning(String text) {
        serverPanel.notifications.append(text + '\n');
    }
    public void showError(String text) {
        serverPanel.notifications.append(text + '\n');
    }
}
