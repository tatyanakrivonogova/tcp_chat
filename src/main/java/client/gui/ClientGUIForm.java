package client.gui;

import client.TCPClient;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.net.InetAddress;
import java.util.Locale;

public class ClientGUIForm {
    private final TCPClient client;
    private final ClientGUI gui;
    public JPanel panel;
    public JTextArea chat;
    public JTextArea participants;
    private JButton connectButton;
    private JButton disconnectButton;
    private JTextField IPAddressField;
    private JTextField portField;
    private JTextField message;

    public ClientGUIForm(ClientGUI _gui, TCPClient _client) {
        gui = _gui;
        client = _client;
        disconnectButton.addActionListener(e -> {
            ClientGUIForm.this.client.disconnect();
            disconnectButton.setFocusable(false);
        });
        connectButton.addActionListener(e -> {
            ClientGUIForm.this.client.connect();
            connectButton.setFocusable(false);
        });
        message.addActionListener(e -> {
            if (client.isConnected()) {
                ClientGUIForm.this.client.sendMessage(message.getText());
            } else {
                gui.showWarning("Connection is not established");
            }
            message.setText("");
        });
    }

    public void setPort(int port) {
        portField.setText("PORT : " + port);
    }

    public void setIpAddress(InetAddress ip) {
        IPAddressField.setText("IP address : " + ip);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel = new JPanel();
        panel.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel.setAutoscrolls(false);
        panel.setBackground(new Color(-4941447));
        panel.setDoubleBuffered(true);
        panel.setEnabled(false);
        Font panelFont = this.$$$getFont$$$("JetBrains Mono ExtraBold", -1, 12, panel.getFont());
        if (panelFont != null) panel.setFont(panelFont);
        connectButton = new JButton();
        connectButton.setBackground(new Color(-14830824));
        connectButton.setBorderPainted(false);
        connectButton.setContentAreaFilled(true);
        connectButton.setDefaultCapable(true);
        connectButton.setDoubleBuffered(true);
        Font connectButtonFont = this.$$$getFont$$$("Arial Black", -1, -1, connectButton.getFont());
        if (connectButtonFont != null) connectButton.setFont(connectButtonFont);
        connectButton.setForeground(new Color(-1));
        connectButton.setHideActionText(true);
        connectButton.setRolloverEnabled(true);
        connectButton.setText("CONNECT");
        panel.add(connectButton, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        disconnectButton = new JButton();
        disconnectButton.setBackground(new Color(-14778954));
        disconnectButton.setBorderPainted(false);
        disconnectButton.setContentAreaFilled(true);
        Font disconnectButtonFont = this.$$$getFont$$$("Arial Black", -1, -1, disconnectButton.getFont());
        if (disconnectButtonFont != null) disconnectButton.setFont(disconnectButtonFont);
        disconnectButton.setForeground(new Color(-7));
        disconnectButton.setSelected(true);
        disconnectButton.setText("DISCONNECT");
        panel.add(disconnectButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        IPAddressField = new JTextField();
        IPAddressField.setAutoscrolls(false);
        IPAddressField.setBackground(new Color(-2514655));
        IPAddressField.setEditable(false);
        IPAddressField.setForeground(new Color(-16777216));
        IPAddressField.setText("IP address : ");
        panel.add(IPAddressField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        portField = new JTextField();
        portField.setAutoscrolls(false);
        portField.setBackground(new Color(-2514655));
        portField.setEditable(false);
        portField.setForeground(new Color(-16777216));
        portField.setText("PORT : ");
        panel.add(portField, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        message = new JTextField();
        message.setBackground(new Color(-1));
        message.setEditable(true);
        message.setFocusCycleRoot(false);
        message.setFocusTraversalPolicyProvider(false);
        Font messageFont = this.$$$getFont$$$("Droid Sans Mono Slashed", -1, -1, message.getFont());
        if (messageFont != null) message.setFont(messageFont);
        message.setForeground(new Color(-16777216));
        panel.add(message, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel.add(scrollPane1, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(400, 600), new Dimension(400, 600), null, 0, false));
        chat = new JTextArea();
        chat.setBackground(new Color(-4473925));
        chat.setEditable(false);
        Font chatFont = this.$$$getFont$$$("Arial Black", -1, 16, chat.getFont());
        if (chatFont != null) chat.setFont(chatFont);
        chat.setForeground(new Color(-16777216));
        chat.setMaximumSize(new Dimension(-1, -1));
        chat.setMinimumSize(new Dimension(400, 600));
        scrollPane1.setViewportView(chat);
        final JScrollPane scrollPane2 = new JScrollPane();
        panel.add(scrollPane2, new GridConstraints(1, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        participants = new JTextArea();
        participants.setBackground(new Color(-5787085));
        participants.setEditable(false);
        Font participantsFont = this.$$$getFont$$$("Arial Black", -1, 14, participants.getFont());
        if (participantsFont != null) participants.setFont(participantsFont);
        participants.setForeground(new Color(-16777216));
        participants.setMaximumSize(new Dimension(-1, -1));
        participants.setMinimumSize(new Dimension(-1, -1));
        participants.setPreferredSize(new Dimension(150, 50));
        scrollPane2.setViewportView(participants);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}
