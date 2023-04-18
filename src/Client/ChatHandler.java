package Client;

import java.io.*;
import java.net.Socket;

public class ChatHandler extends Thread {
    Socket socket;
    BufferedReader reader ;
    boolean isWork=false;

    public ChatHandler(Socket serverSocket)
    {
        this.socket=serverSocket;
        try {
            reader= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            isWork=true;
            System.out.println("I wait");
            System.out.println(reader.readLine());
            System.out.println("I got");
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
        while(socket.isConnected()&&isWork)
        {
            message=null;
            System.out.println("i try read");
            try {
                message = reader.readLine();
                reportError("I got"+message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println(message);
        }
    }
}
