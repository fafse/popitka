package GUI;

import Client.ChatHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ClientGUI {
    int port;
    BufferedWriter writer;

    Socket clientSocket;

    ChatHandler chatHandler;
    JFrame frame;
    JMenuBar mb;
    JTextArea textArea;
    JButton helpButton;
    JPanel panel; // панель не видна при выводе
    JLabel label;
    JTextField nameField;
    JTextField addressField;
    JButton sendButton;
    JButton ConnectButton;
    JButton reset;
    JTextField textField; // принимает до 10 символов

    private WindowListener windowListener = new WindowListener() {
        @Override
        public void windowOpened(WindowEvent e) {

        }

        @Override
        public void windowClosing(WindowEvent e) {
            closeAll();
            e.getWindow().setVisible(false);
            System.exit(0);
        }

        @Override
        public void windowClosed(WindowEvent e) {

        }

        @Override
        public void windowIconified(WindowEvent e) {

        }

        @Override
        public void windowDeiconified(WindowEvent e) {

        }

        @Override
        public void windowActivated(WindowEvent e) {

        }

        @Override
        public void windowDeactivated(WindowEvent e) {

        }
    };
    private ActionListener actionListenerHelpButton;
    private ActionListener actionListenerConnectButton = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                clientSocket = new Socket(addressField.getText(),port);
                writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                chatHandler = new ChatHandler(clientSocket);
                textField.setEditable(true);
                addressField.setEditable(false);
            } catch (IOException ex) {
                reportError("Unavailable to connect. Check address");
                try {
                    clientSocket.close();
                    writer.close();
                } catch (IOException exc) {
                    throw new RuntimeException(exc);
                }
                clientSocket=null;
                writer=null;
                throw new RuntimeException(ex);
            }
        }
    };
    private ActionListener actionListenerSendButton = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String message = textField.getText();
            message="one";
            try {
                if(message!="") {
                    writer.write(message);
                    writer.flush();
                    textField.setText("");
                    reportError("I sent "+message);
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    };

    public ClientGUI(int port) {
        this.port = port;
    }

    public void startGUI() {
        frame = new JFrame("Chat");
        mb = new JMenuBar();
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(textArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        helpButton = new JButton("Help");
        ConnectButton = new JButton("Соединиться");
        panel = new JPanel(); // панель не видна при выводе
        label = new JLabel("Введите текст");
        textField = new JTextField(20); // принимает до 50 символов
        nameField = new JTextField(15);
        nameField.setText("Ник");
        addressField = new JTextField(11);
        addressField.setText("localhost");
        sendButton = new JButton("Отправить");
        reset = new JButton("Сброс");

        textField.setEditable(false);
        helpButton.addActionListener(actionListenerHelpButton);
        ConnectButton.addActionListener(actionListenerConnectButton);
        sendButton.addActionListener(actionListenerSendButton);

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(windowListener);
        frame.setSize(600, 600);

        mb.add(helpButton);
        mb.add(nameField);
        mb.add(addressField);
        mb.add(ConnectButton);

        // Создание панели внизу и добавление компонентов

        panel.add(label); // Компоненты, добавленные с помощью макета Flow Layout
        panel.add(textField);
        panel.add(sendButton);
        panel.add(reset);

        // Добавление компонентов в рамку.
        frame.getContentPane().add(BorderLayout.SOUTH, panel);
        frame.getContentPane().add(BorderLayout.NORTH, mb);
        frame.add(scroll);
        frame.setVisible(true);
    }

    private void reportError(String message)
    {
        textArea.append(message+"\n");
    }


    public int closeAll()
    {
        int success=0;
        if(writer!=null)
        {
            try {
                System.out.println("I try close");
                if(writer!=null) {
                    writer.write("quit");
                    writer.flush();
                    writer.close();
                    clientSocket.close();
                }
                success=1;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return success;
    }

}
