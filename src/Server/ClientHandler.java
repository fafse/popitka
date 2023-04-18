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
                    writer.flush();
                    message = reader.readLine();
                    System.out.println(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (message.equals("quit")) {
                    isWork = false;
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
            writer.write(message+"\n");
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
