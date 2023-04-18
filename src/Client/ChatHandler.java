package Client;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class ChatHandler extends Thread {
    Socket socket;
    BufferedReader reader ;
    BufferedWriter writer;
    volatile boolean isWork=false;
    JTextArea jTextArea;

    public ChatHandler(Socket serverSocket, JTextArea jTextArea)
    {
        this.socket=serverSocket;
        try {
            this.jTextArea=jTextArea;
            reader= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            isWork=true;
            start();
        } catch (IOException e) {
            reportError(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void reportError(String message)
    {
        System.out.println(message);
    }
    @Override
    public void run()
    {
        String message;
        printToUser("Connected Successfully. Now you can chat");
        while(socket.isConnected()&&isWork)
        {
            try {
                writer.flush();
                message = reader.readLine();
                printToUser(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println(message);
        }
    }

    public boolean isWork() {
        return isWork;
    }

    public void sendMessage(String message)
    {
        try {
            writer.write(message+"\n");
            writer.flush();
            if(message.equals("quit"))
            {
                reader.close();
                writer.flush();
                writer.close();
                socket.close();
                setWork(false);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setWork(boolean work) {
        isWork = work;
    }

    public void printToUser(String message)
    {
        jTextArea.append(message+"\n");
    }
}
