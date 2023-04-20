package Server;

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
    private static BlockingQueue<ClientHandler> clientList;

    public static BlockingQueue<ClientHandler> getClientList() {
        return clientList;
    }

    private static void reportError(String message)
    {
        System.out.println(message);
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

    public static void main(String[] args) {
        clientList = new LinkedBlockingDeque<>();
        ServerSocket serverSocket;
        boolean isWork=false;
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
