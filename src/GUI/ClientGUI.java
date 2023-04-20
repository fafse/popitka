package GUI;

import Client.ChatHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Objects;
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
    JTextField nameField, addressField,firstDigitField,secondDigitField, mD5TextField;
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
            sendMessage(message,null);
        }
    };

    private ActionListener actionListenerКнопкиСложения = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String message = "/addition ",firstField,secondField;
            firstField= firstDigitField.getText();
            secondField= secondDigitField.getText();
            JTextField textFields[] = new JTextField[2];
            textFields[0]=firstDigitField;
            textFields[1]=secondDigitField;
            if (!Objects.equals(firstField, "") && !Objects.equals(secondField, ""))
                sendMessage(message+firstField+" "+secondField,textFields);
        }
    };
    private ActionListener actionListenerКнопкиВычитания = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String message = "/subtraction ",firstField,secondField;
            firstField= firstDigitField.getText();
            secondField= secondDigitField.getText();
            JTextField textFields[] = new JTextField[2];
            textFields[0]=firstDigitField;
            textFields[1]=secondDigitField;
            if (!Objects.equals(firstField, "") && !Objects.equals(secondField, ""))
                sendMessage(message+firstField+" "+secondField,textFields);

        }
    };
    private ActionListener actionListenerКнопкиУмножения = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String message = "/multiplication ",firstField,secondField;
            firstField= firstDigitField.getText();
            secondField= secondDigitField.getText();
            JTextField textFields[] = new JTextField[2];
            textFields[0]=firstDigitField;
            textFields[1]=secondDigitField;
            if (!Objects.equals(firstField, "") && !Objects.equals(secondField, ""))
                sendMessage(message+firstField+" "+secondField,textFields);
        }
    };
    private ActionListener actionListenerКнопкиДеления = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String message = "/division ",firstField,secondField;
            firstField= firstDigitField.getText();
            secondField= secondDigitField.getText();
            JTextField textFields[] = new JTextField[2];
            textFields[0]=firstDigitField;
            textFields[1]=secondDigitField;
            if (!Objects.equals(firstField, "") && !Objects.equals(secondField, ""))
                sendMessage(message+firstField+" "+secondField,textFields);
        }
    };
    private ActionListener actionListenerКнопкиШифрования = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String message=mD5TextField.getText();
            JTextField textFields[] = new JTextField[1];
            textFields[0]=mD5TextField;
            if(!message.equals(""))
                sendMessage("/md5 "+message,textFields);
        }
    };

    private ActionListener actionListenerКнопкиРасшифровки = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String message=mD5TextField.getText();
            JTextField textFields[] = new JTextField[1];
            textFields[0]=mD5TextField;
            if(!message.equals(""))
                sendMessage("/deshmd5 "+message,textFields);
        }
    };

    private ActionListener actionListenerConnectButton = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name;
            if(chatHandler==null||!chatHandler.isWork())
                try {
                    name=nameField.getText();
                    if(name!="") {
                        textArea.setText("");
                        clientSocket = new Socket(addressField.getText(), port);
                        chatHandler = new ChatHandler(clientSocket, textArea, name);
                        textField.setEditable(true);
                    }
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
            if(chatHandler==null)
            {
                chatHandler=null;
                clientSocket=null;
            }else if(message!="") {
                chatHandler.sendMessage(message,true);
                textField.setText("");
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
        actionTable = new GridLayout(6,2);
        кнопкаСложения= new JButton("+");
        кнопкаСложения.addActionListener(actionListenerКнопкиСложения);
        кнопкаВычитания= new JButton("-");
        кнопкаВычитания.addActionListener(actionListenerКнопкиВычитания);
        кнопкаДеления= new JButton("/");
        кнопкаДеления.addActionListener(actionListenerКнопкиДеления);
        кнопкаУмножения= new JButton("*");
        кнопкаУмножения.addActionListener(actionListenerКнопкиУмножения);
        кнопкаШифрования= new JButton("MD5");
        кнопкаШифрования.addActionListener(actionListenerКнопкиШифрования);
        кнопкаРасшифровки= new JButton("Расшифровка MD5");
        кнопкаРасшифровки.addActionListener(actionListenerКнопкиРасшифровки);

        panel = new JPanel(); // панель не видна при выводе
        actionPanel = new JPanel();
        label = new JLabel("Введите текст");
        textField = new JTextField(20); // принимает до 50 символов
        mD5TextField = new JTextField(30);
        firstDigitField = new JTextField(5);
        secondDigitField = new JTextField(5);
        nameField = new JTextField(15);
        nameField.setText("Ник");
        addressField = new JTextField(11);
        addressField.setText("localhost");
        sendButton = new JButton("Отправить");
        reset = new JButton("Отсоединиться");
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeAll();
            }
        });

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
                        chatHandler.sendMessage(message,true);
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
        frame.setSize(900, 600);

        mb.add(helpButton);
        mb.add(nameField);
        mb.add(addressField);
        mb.add(ConnectButton);

        // Создание панели внизу и добавление компонентов
        GridLayout actionPanelLayout = new GridLayout(3,1,5,5);
        actionPanel.setLayout(actionPanelLayout);
        JPanel actionButtons = new JPanel(new GridLayout(2,2,5,5));
        actionButtons.add(кнопкаСложения);
        actionButtons.add(кнопкаВычитания);
        actionButtons.add(кнопкаДеления);
        actionButtons.add(кнопкаУмножения);
        JPanel actionTextFields = new JPanel(new FlowLayout(FlowLayout.CENTER));
        actionTextFields.add(firstDigitField);
        actionTextFields.add(secondDigitField);
        JPanel mD5Panel = new JPanel(new GridLayout(3,1,5,5));
        mD5Panel.add(кнопкаШифрования);
        mD5Panel.add(кнопкаРасшифровки);
        mD5TextField.setSize(50,10);
        mD5Panel.add(mD5TextField);
        actionPanel.add(mD5Panel,BorderLayout.CENTER);
        actionPanel.add(actionButtons, BorderLayout.PAGE_END);
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
                if(chatHandler.isWork()) chatHandler.sendMessage("/quit",false);
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

    private void sendMessage(String message, JTextField[] textFields)
    {
        if(chatHandler==null)
        {
            chatHandler=null;
            clientSocket=null;
        }else if(message!="") {
            chatHandler.sendMessage(message,false);
            if(textField!=null)
            for(int i = 0;i<textFields.length;i++)
                if(textField!=null) textField.setText("");
        }
    }
}
