package ru.deos.scrambler.tcp;

import ru.deos.scrambler.Main;
import ru.deos.scrambler.Settings;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: Deos
 * Date: 4/5/14
 * Time: 12:37 AM
 */
public class TCPServer extends Thread {
    private ServerSocket socket;

    @Override
    public void run() {
        try {
            socket = new ServerSocket(Settings.TCP_SERVER_PORT);
        } catch (IOException e) {
            System.out.println("Can't start listening at port " + Settings.TCP_SERVER_PORT);
            return;
        }

        Main.getHalterThread().addSocketToClose(socket);

        while(!socket.isClosed()) {
            try {
                Socket clientSocket = socket.accept();
                Client client = new Client(clientSocket);
                client.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
