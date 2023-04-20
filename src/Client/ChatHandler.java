package Client;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class ChatHandler extends Thread {
    Socket socket;
    BufferedReader reader ;
    BufferedWriter writer;
    volatile boolean isWork=false;
    private String name;
    JTextArea jTextArea;

    public ChatHandler(Socket serverSocket, JTextArea jTextArea,String name)
    {
        this.socket=serverSocket;
        try {
            this.jTextArea=jTextArea;
            reader= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            isWork=true;
            this.name = name;
            sendMessage(name,false);
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
                if(message==null)
                {
                    message="Server disconnected";
                }
                printToUser(message);
            } catch (IOException e) {
                reader=null;
                writer=null;
                socket=null;
                setWork(false);
            }
        }
    }

    public boolean isWork() {
        return isWork;
    }

    public void sendMessage(String message,boolean writeToUser)
    {
        try {
            if (isWork) {
                writer.write(message + "\n");
                writer.flush();
                if (message.equals("/quit")) {
                    if (reader != null) reader.close();
                    if (writer != null) {
                        writer.flush();
                        writer.close();
                    }
                    if (socket != null) socket.close();
                    setWork(false);
                }else
                {
                    if(writeToUser) printToUser(message);
                }
            }
            else
            {
                printToUser("You aren't connected. Please connect to server to send messages");
            }
            } catch(IOException e){
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
