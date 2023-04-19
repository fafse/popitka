package Server;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

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

    public static String[] getRules() {
        String[] rules = new String[10];

        rules[0] = "Commands:";
        rules[1] = "===================================================";
        rules[2] = "1. add\nExample of using:\nadd 5 3\nServer will answer:8";
        rules[3] = "2. subtract\nExample of using:\nsubtract 5 3\nServer will answer:2";
        rules[4] = "3. multiply\nExample of using:\nmultiply 5 3\nServer will answer:15";
        rules[5] = "4. divide\nExample of using:\ndivide 6 3\nServer will answer:2";
        rules[6] = "5. MD5\n Example of using:\nMD5 torules\nServer will answer:";
        rules[7] = "6. DeshMD5\nExample of using:\nDeshMd5 39D89CD686B43C82A7509A638A4AB6DD 100\nServer will answer:rulesss\nATTENTION:\n" +
                "This command is available for 7 letter message(in decrypted form)";
        rules[8] = "7. help\nExample of using:\nhelp\nServer will write this note.";
        rules[9] = "===================================================";
        return rules;
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
                    isWork=false;
                    throw new RuntimeException(e);
                }
                if (message.equals("/quit")) {
                    isWork = false;
                    break;
                } else if (message.equals("/help")) {
                    for (String rule: getRules()) {
                        sendMessage(rule);
                    }
                }
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
