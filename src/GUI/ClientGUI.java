package GUI;

import Client.ChatHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.function.BiPredicate;

public class ClientGUI {
    int port;
    Socket clientSocket;
    ChatHandler chatHandler;
    JFrame frame;
    JMenuBar mb;
    JTextArea textArea;
    JButton helpButton;
    JPanel panel; // панель не видна при выводе
    JPanel actionPanel;
    JLabel label;
    JTextField nameField;
    JTextField addressField;
    JTextField firstDigitField,secondDigitField;
    GridLayout actionTable;
    JButton sendButton;
    JButton ConnectButton;
    JButton reset;
    JButton кнопкаСложения, кнопкаВычитания,кнопкаУмножения, кнопкаДеления, кнопкаШифрования,кнопкаРасшифровки;

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
    private ActionListener actionListenerHelpButton= new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String message = "/help";
            sendMessage(message);
        }
    };

    private ActionListener actionListenerКнопкиСложения = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String messaage;
        }
    };
    private ActionListener actionListenerКнопкиВычитания = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };
    private ActionListener actionListenerКнопкиУмножения = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };
    private ActionListener actionListenerКнопкиДеления = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };
    private ActionListener actionListenerКнопкиШифрования = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };

    private ActionListener actionListenerConnectButton = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(chatHandler==null||!chatHandler.isWork())
                try {
                    textArea.setText("");
                    clientSocket = new Socket(addressField.getText(),port);
                    chatHandler = new ChatHandler(clientSocket, textArea);
                    textField.setEditable(true);
                } catch (IOException ex) {
                    reportError("Unavailable to connect. Check address");
                    try {
                        if(clientSocket!=null)  clientSocket.close();
                    } catch (IOException exc) {
                        throw new RuntimeException(exc);
                    }
                    clientSocket=null;
                    throw new RuntimeException(ex);
                }
            else
            {
                reportError("You are already connected to server.");
            }
        }
    };
    private ActionListener actionListenerSendButton = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String message = textField.getText();
            sendMessage(message);
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
        actionTable = new GridLayout(6,2);
        кнопкаСложения= new JButton("+");
        кнопкаВычитания= new JButton("-");
        кнопкаДеления= new JButton("/");
        кнопкаУмножения= new JButton("*");
        кнопкаШифрования= new JButton("MD5");
        кнопкаРасшифровки= new JButton("Расшифровка MD5");
        panel = new JPanel(); // панель не видна при выводе
        actionPanel = new JPanel();
        label = new JLabel("Введите текст");
        textField = new JTextField(20); // принимает до 50 символов
        firstDigitField = new JTextField(5);
        secondDigitField = new JTextField(5);
        nameField = new JTextField(15);
        nameField.setText("Ник");
        addressField = new JTextField(11);
        addressField.setText("localhost");
        sendButton = new JButton("Отправить");
        reset = new JButton("Сброс");

        textField.setEditable(false);
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()== KeyEvent.VK_ENTER)
                {
                    String message = textField.getText();
                    if(chatHandler==null)
                    {
                        chatHandler=null;
                        clientSocket=null;
                    }else if(message!="") {
                        chatHandler.sendMessage(message);
                        textField.setText("");
                    }
                }
            }
        });
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
        GridLayout actionPanelLayout = new GridLayout(2,1,15,15);
        actionPanel.setLayout(actionPanelLayout);
        JPanel actionButtons = new JPanel(new GridLayout(2,2,5,5));
        actionButtons.add(кнопкаСложения);
        actionButtons.add(кнопкаВычитания);
        actionButtons.add(кнопкаДеления);
        actionButtons.add(кнопкаУмножения);
        JPanel actionTextFields = new JPanel(new FlowLayout(FlowLayout.CENTER));
        actionTextFields.add(firstDigitField);
        actionTextFields.add(secondDigitField);
        actionTextFields.add(кнопкаШифрования);
        JPanel mD5Panel = new JPanel(new GridLayout());
        actionPanel.add(actionButtons, BorderLayout.CENTER);
        actionPanel.add(actionTextFields,BorderLayout.PAGE_START);

        panel.add(label); // Компоненты, добавленные с помощью макета Flow Layout
        panel.add(textField);
        panel.add(sendButton);
        panel.add(reset);

        // Добавление компонентов в рамку.
        frame.getContentPane().add(BorderLayout.EAST, actionPanel);
        frame.getContentPane().add(BorderLayout.SOUTH, panel);
        frame.getContentPane().add(BorderLayout.NORTH, mb);
        frame.add(scroll);
        frame.setVisible(true);
        chatHandler=null;
    }

    private void reportError(String message)
    {
        textArea.append(message+"\n");
    }


    public int closeAll()
    {
        int success=0;
        if(chatHandler!=null)
        {
            try {
                System.out.println("I try close");
                if(chatHandler.isWork()) chatHandler.sendMessage("/quit");
                chatHandler=null;
                if(clientSocket!=null)
                    clientSocket.close();
                success=1;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return success;
    }

    private void sendMessage(String message)
    {
        if(chatHandler==null)
        {
            chatHandler=null;
            clientSocket=null;
        }else if(message!="") {
            chatHandler.sendMessage(message);
            textField.setText("");
        }
    }
}
