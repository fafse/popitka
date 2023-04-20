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
    private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
    public static String[] elements = {"qwertyuiopasdfghjklzxcvbnm"};
    private static String toHexString(byte[] bytes) {
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            hex.append(HEX_DIGITS[(b & 0xff) >> 4]);
            hex.append(HEX_DIGITS[b & 0x0f]);
        }
        return hex.toString();
    }
    static String hashPassword(String password) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        digest.update(password.getBytes());
        byte[] bytes = digest.digest();
        return toHexString(bytes);
    }

    public static String findMD5(String hex_password, int numThreads)
    {
        if(numThreads<0||numThreads>100)
            numThreads=100;
        int numPassword= (int) Math.pow(26,7);
        Thread[] threads = new Thread[numThreads];
        long t0 = System.nanoTime();
        for(int i = 0;i<numThreads;i++)
        {
            threads[i] = new Thread(new MD5Hasher((long)numPassword*(i)/numThreads,(long)numPassword*(i+1)/numThreads,hex_password));
            threads[i].start();
        }
        for(int i = 0;i<numThreads;i++)
        {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        long t = System.nanoTime()-t0;
        System.out.println(t/1e9 +" Seconds required to solve this problem");
        return MD5Hasher.getFoundPassword();
    }
    private static void reportError(String message)
    {
        System.out.println(message);
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
