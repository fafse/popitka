package Server;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ClientHandler extends Thread{
    private Socket socket;
    private String name;
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
            name=reader.readLine();
            sendAll("connected");
            start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
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

    public String findMD5(String hex_password, int numThreads)
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
        sendMessage(t/1e9 +" Seconds required to solve this problem");
        String password=MD5Hasher.getFoundPassword();
        MD5Hasher.makeDefault();
        return password;
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

    private void commandHandler(String command)
    {
        boolean isCommand = false;
        String[] commandParts = command.split(String.valueOf(' '));
        if(commandParts.length==2&&commandParts[0].charAt(0)=='/')
        {
            if(commandParts[0].equals("/md5"))
            {
                isCommand=true;
                sendMessage(hashPassword(commandParts[1]));
            } else if (commandParts[0].equals("/deshmd5")) {
                isCommand=true;
                if(commandParts[1].length()!=32) {
                    sendMessage("Hash must be a 32 character hex string");
                    return;
                }
                sendMessage("Decoding md5...Please don't send me smth till the process end");
                sendMessage(findMD5(commandParts[1],100));
            }else
            {
                sendMessage("Unavailable to solve your command. Please check data");
            }
        } else if (commandParts.length==3) {
            switch (commandParts[0])
            {
                case "/addition":
                {
                    isCommand=true;
                    sendMessage(String.valueOf(Integer.valueOf(commandParts[1]) + Integer.valueOf(commandParts[2])));
                    break;
                }
                case "/subtraction":
                {
                    isCommand=true;
                    sendMessage(String.valueOf(Integer.valueOf(commandParts[1]) - Integer.valueOf(commandParts[2])));
                    break;
                }
                case "/multiplication":
                {
                    isCommand=true;
                    sendMessage(String.valueOf(Integer.valueOf(commandParts[1]) * Integer.valueOf(commandParts[2])));
                    break;
                }
                case "/division":
                {
                    isCommand=true;
                    sendMessage(String.valueOf(Integer.valueOf(commandParts[1]) / Integer.valueOf(commandParts[2])));
                    break;
                }

                default:
                {
                    sendMessage("Unavailable to solve your command. Please check data");
                    break;
                }
            }
        }else if(command.equals("/quit"))
        {
            isCommand=true;
        }
        if(!isCommand) sendAll(command);
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
                    System.out.println(name+">:"+message);
                    commandHandler(message);
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
                sendAll("disconnected");
                reader.close();
                writer.close();
                socket.close();
                System.out.println(name+" disconnected");
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
    private void sendAll(String message)
    {
        Server.deleteUnavailableUsers();
        String messageToAll;
        if(message.equals("disconnected"))
        {
            messageToAll=name+" disconnected...";
        }else if(message.equals("connected"))
        {
            messageToAll=name+" connected";
        }else
        {
            messageToAll=name+">:"+message;
        }
        for (ClientHandler client:
             Server.getClientList()) {
            if(!client.equals(this))
                client.sendMessage(messageToAll);
        }
    }
}
