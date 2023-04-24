package Server;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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
            Date date = new Date();
            SimpleDateFormat formatForDateNow = new SimpleDateFormat("hh:mm:ss a zzz");
            Server.textArea.append("["+formatForDateNow.format(date)+"]\n"+name+" connected\n");
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
        String[] rules = new String[3];
        rules[0] = "Инструкция:";
        rules[1] = "===================================================";
        rules[2] = "На верхней панели располагаются кнопка Помощь, которая вызывает данное меню\n" +
                "Поле для ввода имени пользователя, поле для ввода адреса, и кнопка Соединиться.\n" +
                "Слева расположен чат, где будут выводиться ваши и чужие сообщения, а также ответы от сервера\n" +
                "на ваши запросы.\n" +
                "Справа представлены кнопки для шифровки и расшифровки MD5.\n" +
                "Для того, чтобы воспользоваться этим необходимо в поле под кнопкой Расшифровка MD5 текст,\n" +
                "нужный для шифровки/расшифровки.\n" +
                "Ниже этого поля расположены кнопки с арифметическими операциями.\n" +
                "Для того, чтобы воспользоваться ими необходимо в полях, расположеными под\n" +
                "кнопками арифметических операций ввести числа.\n" +
                "В нижней панели расположено поле для отправки сообщений другим участникам чата,\n" +
                "Кнопки Отправить, служащая дла отправки сообщения из поля, расположенного слева от этой кнопки,\n" +
                "а также кнопка Отсоединиться, нужная дла того, чтобы отсоединиться от текущего сервера\n" +
                "===================================================\n";
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
                    Date date = new Date();
                    SimpleDateFormat formatForDateNow = new SimpleDateFormat("hh:mm:ss a zzz");
                    Server.textArea.append("["+formatForDateNow.format(date)+"]\n"+name+">:"+message+"\n");
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
                Date date = new Date();
                SimpleDateFormat formatForDateNow = new SimpleDateFormat("hh:mm:ss a zzz");
                Server.textArea.append("["+formatForDateNow.format(date)+"]\n"+name+" disconnected");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void sendMessage(String message)
    {
        Date date = new Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("hh:mm:ss a zzz");
        try {
            writer.write("["+formatForDateNow.format(date)+"]\n"+message+"\n");
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
