package Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Server {
    static JFrame frame;
    static JTextArea textArea;
    static volatile boolean isWork=false;
    private static BlockingQueue<ClientHandler> clientList;

    public static BlockingQueue<ClientHandler> getClientList() {
        return clientList;
    }

    private static void reportError(String message)
    {
        textArea.append(message+"\n");
    }

    public static void deleteUnavailableUsers()
    {
        if(getClientList()!=null) {
            BlockingQueue<ClientHandler> clientAvailableList = getClientList();
            for (ClientHandler client :
                    getClientList()) {
                if (!client.isWork)
                    getClientList().remove(client);
            }
            clientList = clientAvailableList;
        }
    }
    private static WindowListener windowListener = new WindowListener() {
        @Override
        public void windowOpened(WindowEvent e) {

        }

        @Override
        public void windowClosing(WindowEvent e) {
            isWork=false;
            deleteUnavailableUsers();
            for (ClientHandler client:
                 clientList) {
                client.sendMessage("/quit");
                client.isWork=false;
            }
            deleteUnavailableUsers();
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

    public static void main(String[] args) {
        frame = new JFrame("Курсач");
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(textArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(900, 600);
        frame.add(scroll);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(windowListener);
        frame.setVisible(true);
        clientList = new LinkedBlockingDeque<>();
        ServerSocket serverSocket;
        int port = 2023;
        try {
            serverSocket = new ServerSocket(port);
            isWork=true;
        } catch (IOException e) {
            reportError(e.getMessage());
            throw new RuntimeException(e);
        }
        try{
        while (isWork) {
            ClientHandler handler;
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                handler = new ClientHandler(socket);
            } catch (IOException e) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                reportError("Client socket closed");
                throw new RuntimeException(e);
            }
            if (socket != null) {
                clientList.add(handler);
                reportError("Client socket added to list");
            }
        }
        }finally
        {
            try {
                serverSocket.close();
                reportError("Server closed");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
