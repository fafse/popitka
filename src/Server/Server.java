package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static List<ClientHandler> clientList;

    public static void main(String[] args) {
        clientList = new ArrayList<>();
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
    private static void reportError(String message)
    {
        System.out.println(message);
    }
}
