package Server;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread{
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;
    boolean isWork = false;

    public ClientHandler(Socket socket)
    {
        this.socket = socket;
        try {
            reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            isWork=true;
            sendMessage("ine");
            start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void run()
    {
        String message;
        try {
            while (isWork) {
                try {
                    System.out.println("i try read");
                    sendMessage("one");
                    System.out.println("I sent");
                    message = reader.readLine();
                    System.out.println(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (message.equals("quit")) {
                    isWork = false;
                    System.out.println("QUIT");
                    break;
                }
                sendMessage(message);
            }
        }finally {
            try {
                reader.close();
                writer.close();
                socket.close();
                System.out.println("CLOSED");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void sendMessage(String message)
    {
        try {
            writer.write(message);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
