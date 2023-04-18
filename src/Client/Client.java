package Client;

import GUI.ClientGUI;

import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        int port = 2023;
        ClientGUI client=null;
    try {
    client = new ClientGUI(port);
    client.startGUI();
    }finally {
        client.closeAll();
    }


    }
}